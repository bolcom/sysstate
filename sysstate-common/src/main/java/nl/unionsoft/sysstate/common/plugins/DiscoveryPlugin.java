package nl.unionsoft.sysstate.common.plugins;

import java.util.Collection;
import java.util.Properties;

import net.xeoh.plugins.base.Plugin;
import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.queue.ReferenceRunnable;

public interface DiscoveryPlugin extends Plugin {

    public Collection<? extends ReferenceRunnable> discover(Properties properties);

    public void updatePropertiesTemplate(Properties properties);

}
