package nl.unionsoft.sysstate.logic.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import nl.unionsoft.sysstate.common.enums.WorkStatus;
import nl.unionsoft.sysstate.dao.WorkDao;
import nl.unionsoft.sysstate.domain.Work;
import nl.unionsoft.sysstate.logic.WorkLogic;

@Service("workLogic")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class WorkLogicImpl implements WorkLogic {

    private static final Logger logger = LoggerFactory.getLogger(WorkLogicImpl.class);
    private final WorkDao workDao;
    private final String nodeId;

    private final Map<String, String> workIdMappings;

    @Inject
    public WorkLogicImpl(WorkDao workDao, @Value("${node}") String nodeId) {
        this.workDao = workDao;
        this.nodeId = nodeId;
        workIdMappings = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<String> aquireWorkLock(String reference) {
        logger.debug("Aquiring lock for reference [{}]", reference);
        Work work = workDao.getWork(reference).orElse(createWork(reference));
        if (!isClaimable(work)) {
            logger.debug("Work cannot be claimed for reference [{}]", reference);
            return Optional.empty();
        }

        work.setState(WorkStatus.ACQUIRING);
        work.setNodeId(nodeId);
        workDao.createOrUpdateWork(work);

        Work aquiredWork = workDao.getWork(reference).orElseThrow(() -> new IllegalStateException("Work doesn't exist anymore!"));
        if (!isMine(aquiredWork)) {
            logger.debug("Another node [{}] picked up the work for this reference", aquiredWork.getNodeId());
            return Optional.empty();
        }
        aquiredWork.setState(WorkStatus.ACQUIRED);
        workDao.createOrUpdateWork(aquiredWork);

        String workId = UUID.randomUUID().toString();
        workIdMappings.put(workId, reference);
        return Optional.of(workId);
    }

    private Work createWork(String reference) {
        Work work = new Work();
        work.setReference(reference);
        return work;
    }

    private boolean isMine(Work work) {
        return StringUtils.equals(work.getNodeId(), nodeId);
    }

    private boolean isClaimable(Work work) {
        return true;
    }

    @Override
    public void release(String workLock) {
        logger.debug("Releasing lock for workLock [{}]", workLock);
        Optional.ofNullable(workIdMappings.get(workLock))
                .flatMap(reference -> workDao.getWork(reference))
                .ifPresent(work -> {
                    work.setState(WorkStatus.RELEASED);
                    workDao.createOrUpdateWork(work);
                });
        workIdMappings.remove(workLock);
    }

}
