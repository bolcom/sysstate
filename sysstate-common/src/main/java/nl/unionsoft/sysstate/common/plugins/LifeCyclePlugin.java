package nl.unionsoft.sysstate.common.plugins;

import net.xeoh.plugins.base.Plugin;

public interface LifeCyclePlugin extends Plugin {

    public void stop();

    public void start();

}
