package nl.unionsoft.sysstate.plugins.impl.notifier;

import java.util.Properties;

import net.xeoh.plugins.base.annotations.Capabilities;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.plugins.ConfigurablePlugin;
import nl.unionsoft.sysstate.common.plugins.NotifierPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginImplementation
public class MockNotifierPluginImpl implements NotifierPlugin, ConfigurablePlugin {
    private static final Logger LOG = LoggerFactory.getLogger(MockNotifierPluginImpl.class);

    public void notify(StateDto state, StateDto lastState, Properties notifierConfig) {
        LOG.info("Notification for state '{}' from lastState '{}'", state, lastState);
    }

    public void stop() {
        LOG.info("stop()");
    }

    public void start() {
        LOG.info("start()");
    }

    public void config(Properties configuration) {
        LOG.info("config()");
    }

    public void setActionId(Long id) {
        // TODO Auto-generated method stub

    }

    @Capabilities
    public String[] capabilities() {
        return new String[] { "mockNotifier" };
    }

}
