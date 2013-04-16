package nl.unionsoft.sysstate.queue;

import nl.unionsoft.sysstate.common.queue.ReferenceRunnable;
import nl.unionsoft.sysstate.queue.ReferenceWorkerImpl.Statistics;

public interface ReferenceWorker {
    public void enqueue(ReferenceRunnable referenceRunnable);

    public void clearWorkLog();

    public Statistics getStatistics();

    public String getStackTrace(String reference);

}
