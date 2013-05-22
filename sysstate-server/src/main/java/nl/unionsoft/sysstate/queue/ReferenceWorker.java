package nl.unionsoft.sysstate.queue;

import nl.unionsoft.sysstate.common.queue.ReferenceRunnable;

public interface ReferenceWorker {
    public void enqueue(ReferenceRunnable referenceRunnable);

    public void clearWorkLog();

    public String getStackTrace(String reference);

}
