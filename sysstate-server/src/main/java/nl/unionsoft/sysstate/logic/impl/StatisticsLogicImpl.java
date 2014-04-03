package nl.unionsoft.sysstate.logic.impl;

import javax.inject.Inject;
import javax.inject.Named;

import nl.unionsoft.sysstate.common.dto.StatisticsDto;
import nl.unionsoft.sysstate.common.logic.StatisticsLogic;
import nl.unionsoft.sysstate.dao.StatisticsDao;
import nl.unionsoft.sysstate.logic.PluginLogic;

import org.springframework.stereotype.Service;

@Service("statisticsLogic")
public class StatisticsLogicImpl implements StatisticsLogic {

    @Inject
    @Named("statisticsDao")
    private StatisticsDao statisticsDao;

    @Inject
    @Named("pluginLogic")
    private PluginLogic pluginLogic;

    public StatisticsDto getStatistics() {
        return statisticsDao.getStatistics();
    }

    public String getApplicationVersion() {
        String result = "Undefined";
        // Plugin sysstatePlugin = pluginLogic.getPlugin("sysstate");
        // if (sysstatePlugin != null) {
        // result = sysstatePlugin.getVersion();
        // }
        return result;
    }

}
