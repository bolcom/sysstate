package nl.unionsoft.sysstate.common.extending;

import java.util.Collection;
import java.util.Properties;

import nl.unionsoft.sysstate.common.queue.ReferenceRunnable;

public interface Discovery {

    public Collection<? extends ReferenceRunnable> discover(Properties properties);

    public void updatePropertiesTemplate(Properties properties);

}
