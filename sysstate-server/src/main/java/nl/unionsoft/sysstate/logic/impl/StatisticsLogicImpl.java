package nl.unionsoft.sysstate.logic.impl;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.StatisticsDto;
import nl.unionsoft.sysstate.common.logic.StatisticsLogic;
import nl.unionsoft.sysstate.dao.StatisticsDao;

import org.springframework.stereotype.Service;
@Service("statisticsLogic")
public class StatisticsLogicImpl implements StatisticsLogic{


    @Inject
    @Named("statisticsDao")
    private StatisticsDao statisticsDao;

    public StatisticsDto getStatistics() {
        return statisticsDao.getStatistics();
    }

}
