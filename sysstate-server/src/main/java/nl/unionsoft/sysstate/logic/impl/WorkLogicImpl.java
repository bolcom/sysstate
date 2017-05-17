package nl.unionsoft.sysstate.logic.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import nl.unionsoft.sysstate.common.dto.WorkDto;
import nl.unionsoft.sysstate.common.enums.WorkStatus;
import nl.unionsoft.sysstate.converter.WorkConverter;
import nl.unionsoft.sysstate.dao.WorkDao;
import nl.unionsoft.sysstate.domain.Work;
import nl.unionsoft.sysstate.logic.WorkLogic;

@Service("workLogic")
public class WorkLogicImpl implements WorkLogic {

    private static final Logger logger = LoggerFactory.getLogger(WorkLogicImpl.class);
    private final WorkDao workDao;
    private final WorkConverter workConverter;

    private final String nodeId;

    private final Map<String, WorkDto> myWork;

    @Inject
    public WorkLogicImpl(WorkDao workDao, WorkConverter workConverter, @Value("${work.node}") String nodeId) {
        this.workDao = workDao;
        this.workConverter = workConverter;
        this.nodeId = nodeId;

        myWork = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<String> aquireWorkLock(String reference, int timeoutSeconds) {
        logger.debug("Aquiring lock for reference [{}]", reference);
        Work work = workDao.getWork(reference).orElse(createWork(reference));
        if (!isClaimable(work)) {
            logger.debug("Work cannot be claimed for reference [{}], it is  handled by node [{}], checking if it has expired...", reference, work.getNodeId());
            if (!hasExpired(work, timeoutSeconds)) {
                logger.debug("Work [{}] has not expired. Returning emtpy...", reference);
                return Optional.empty();
            }
            logger.debug("Work [{}] and node [{}] has expired after a timeout of [{}] seconds. Claiming...", new Object[] { reference, work.getNodeId(),
                    timeoutSeconds });
        }

        logger.debug("Work is claimable for reference [{}], will attempt to acquire...", reference);
        work.setState(WorkStatus.ACQUIRING);
        work.setNodeId(nodeId);
        workDao.createOrUpdateWork(work);

        Work aquiredWork = workDao.getWork(reference).orElseThrow(() -> new IllegalStateException("Work doesn't exist anymore!"));
        if (!isMine(aquiredWork)) {
            logger.debug("Another node [{}] picked up the work for this reference", aquiredWork.getNodeId());
            return Optional.empty();
        }

        logger.debug("Acquired work for reference [{}] succesfully. Making it final...", reference);
        aquiredWork.setState(WorkStatus.ACQUIRED);
        aquiredWork.setInitialized(new Date());
        workDao.createOrUpdateWork(aquiredWork);

        String workId = UUID.randomUUID().toString();
        myWork.put(workId, workConverter.convert(work));
        return Optional.of(workId);
    }

    private Work createWork(String reference) {
        Work work = new Work();
        work.setReference(reference);
        work.setState(WorkStatus.NEW);
        work.setInitialized(new Date());
        return work;
    }

    private boolean isMine(Work work) {
        return StringUtils.equals(work.getNodeId(), nodeId);
    }

    private boolean isClaimable(Work work) {
        return work.getState().equals(WorkStatus.NEW) || work.getState().equals(WorkStatus.RELEASED);
    }

    private boolean hasExpired(Work work, int timeoutSeconds) {
        if (work.getInitialized() == null) {
            return true;
        }
        DateTime now = new DateTime();
        logger.debug("Checking if work initializationTime [{}] plus [{}] seconds is before [{}]", new Object[] { work.getInitialized(), timeoutSeconds, now });
        return new DateTime(work.getInitialized()).plusSeconds(timeoutSeconds).isBefore(now);
    }

    @Override
    public void release(String workId) {
        logger.debug("Releasing lock for workLock [{}]", workId);
        if (!Optional.of(myWork.get(workId)).isPresent()) {
            throw new IllegalStateException("Work with lock [" + workId + "] is uknown...");
        }
        try {
            Optional.ofNullable(myWork.get(workId))
                    .flatMap(work -> workDao.getWork(work.getReference()))
                    .ifPresent(work -> {
                        work.setState(WorkStatus.RELEASED);
                        workDao.createOrUpdateWork(work);
                    });
        } finally {
            myWork.remove(workId);
        }

    }

    @Override
    public Collection<WorkDto> getMyWork() {
        return myWork.values().stream().map(work -> {
            WorkDto workDto = new WorkDto();
            workDto.setInitialized(work.getInitialized());
            workDto.setNodeId(work.getNodeId());
            workDto.setReference(work.getReference());
            workDto.setState(work.getState());
            return workDto;
        }).collect(Collectors.toList());
    }

    @Override
    public void process(String reference, int timeoutSeconds, Runnable runnable) {
        logger.info("Processing work for reference [{}] with a timeout of [{}]", reference, timeoutSeconds);
        CompletableFuture.runAsync(() -> {
            aquireWorkLock(reference, timeoutSeconds).ifPresent(workId -> {
                logger.debug("Acquired lock for reference [{}], executing runnable...", reference);
                try {
                    CompletableFuture.runAsync(runnable).get(timeoutSeconds, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    throw new IllegalStateException("Unable to process, caught exception", e);
                } finally {
                    logger.debug("Work complete, releasing work for process with reference [{}]", reference);
                    release(workId);
                }
            });
        });
    }

}
