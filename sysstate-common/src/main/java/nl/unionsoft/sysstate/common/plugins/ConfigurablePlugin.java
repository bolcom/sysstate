package nl.unionsoft.sysstate.common.plugins;

import java.util.Properties;

import net.xeoh.plugins.base.Plugin;

public interface ConfigurablePlugin extends Plugin {
    public void config(Properties configuration);
}
