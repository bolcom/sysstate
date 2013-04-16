package nl.unionsoft.sysstate.logic.impl;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.StatisticsDto;
import nl.unionsoft.sysstate.dao.StatisticsDao;
import nl.unionsoft.sysstate.logic.StatisticsLogic;
import nl.unionsoft.sysstate.queue.ReferenceWorker;
import nl.unionsoft.sysstate.queue.ReferenceWorkerImpl.Statistics;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service("statisticsLogic")
public class StatisticsLogicImpl implements StatisticsLogic {

    @Inject
    @Named("statisticsDao")
    private StatisticsDao statisticsDao;

    @Inject
    @Named("referenceWorker")
    private ReferenceWorker referenceWorker;

    @Cacheable("statisticsCache")
    public StatisticsDto getStatistics() {
        final StatisticsDto statistics = new StatisticsDto();
        statisticsDao.updateStatistics(statistics);
        final Statistics queueStatistics = referenceWorker.getStatistics();
        statistics.setUpdates(queueStatistics.getExecutedTasks());
        return statistics;
    }
}
