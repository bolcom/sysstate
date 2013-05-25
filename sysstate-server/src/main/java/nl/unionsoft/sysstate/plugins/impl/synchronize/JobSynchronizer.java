package nl.unionsoft.sysstate.plugins.impl.synchronize;

import java.util.Properties;

// @PluginImplementation
public class JobSynchronizer {

    private Properties configuration;

    Thread synchronizer = null;

    public void setActionId(final Long id) {

    }

    public void stop() {
        if (synchronizer != null && synchronizer.isAlive()) {
            synchronizer.interrupt();
        }
    }

    public void start() {

        if (synchronizer != null && synchronizer.isAlive()) {
            throw new IllegalStateException("Cannot start thread, it is still alive!");
        }
        synchronizer = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(3000);
                    }
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        synchronizer.start();

    }

    public void config(final Properties configuration) {
        this.configuration = configuration;

    }

}
