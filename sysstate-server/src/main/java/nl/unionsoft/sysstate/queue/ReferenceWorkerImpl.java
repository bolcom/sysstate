package nl.unionsoft.sysstate.queue;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import nl.unionsoft.sysstate.common.queue.ReferenceRunnable;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ReferenceWorkerImpl implements ReferenceWorker, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ReferenceWorkerImpl.class);

    protected final BlockingQueue<Runnable> queue;
    protected final ReferenceThreadPoolExecutor threadPoolExecutor;
    protected ApplicationContext applicationContext;
    private final Set<Task> tasks;

    public ReferenceWorkerImpl () {
        this(6, 12);
    }

    public ReferenceWorkerImpl (int corePoolSize, int maxPoolSize) {
        tasks = Collections.synchronizedSet(new LinkedHashSet<Task>());
        queue = new LinkedBlockingQueue<Runnable>();

        final RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
            public void rejectedExecution(final Runnable runnable, final ThreadPoolExecutor executor) {

                if (runnable instanceof ReferenceRunnable) {
                    final ReferenceRunnable referenceRunnable = (ReferenceRunnable) runnable;
                    LOG.info("Rejecting ReferenceRunnable with reference: " + referenceRunnable.getReference());
                    final Task task = getTaskByReference(referenceRunnable.getReference());
                    task.setState(State.REJECTED);
                    dismiss(referenceRunnable);
                }
            }
        };
        threadPoolExecutor = new ReferenceThreadPoolExecutor(corePoolSize, maxPoolSize, 60, TimeUnit.SECONDS, queue, rejectedExecutionHandler);
    }

    public void destroy() {
        LOG.info(getClass().getCanonicalName() + ".destroy()");
        threadPoolExecutor.shutdown();
        try {
            LOG.info("Waiting for termination...");
            threadPoolExecutor.awaitTermination(180, TimeUnit.SECONDS);
        } catch(final InterruptedException e) {
            LOG.error("Caught InterruptedException while waiting for termination.", e);
        }
    }

    public void enqueue(final ReferenceRunnable referenceRunnable) {
        final String reference = referenceRunnable.getReference();
        if (referenceOnQueue(reference)) {
            LOG.warn("Request for runnable with reference '" + referenceRunnable.getReference() + "' is already on queue. Skipping...");
        } else {
            final Task task = new Task();
            task.setReference(reference);
            task.setDescription(referenceRunnable.getDescription());
            applicationContext.getAutowireCapableBeanFactory().autowireBean(referenceRunnable);
            task.setReferenceRunnable(referenceRunnable);
            task.setCreationDate(new DateTime());
            task.setState(State.DECLARED);
            synchronized (tasks) {
                tasks.add(task);
            }
            threadPoolExecutor.execute(referenceRunnable);
            LOG.info("Request for runnable with reference '" + referenceRunnable.getReference() + "' added to the queue!");
        }
    }

    private boolean referenceOnQueue(String reference) {
        return getTaskByReference(reference) != null;
    }

    private Task getTaskByReference(String reference) {
        Task result = null;
        synchronized (tasks) {
            for (final Task task : tasks) {
                if (StringUtils.equals(reference, task.getReference())) {
                    result = task;
                    break;
                }
            }

        }
        return result;
    }

    public void dismiss(final ReferenceRunnable referenceRunnable) {
        LOG.info("Releasing reference '" + referenceRunnable.getReference() + "' from the tasks.");
        final Task task = getTaskByReference(referenceRunnable.getReference());
        task.setState(State.DISMISSED);
        synchronized (tasks) {
            tasks.remove(task);
        }
    }

    public String getStackTrace(String reference) {
        final StringBuilder stackTraceBuilder = new StringBuilder();
        final Task task = getTaskByReference(reference);
        if (task != null) {
            final Thread thread = task.getThread();
            if (thread != null) {
                for (final StackTraceElement stackTraceElement : thread.getStackTrace()) {
                    stackTraceBuilder.append(stackTraceElement);
                    stackTraceBuilder.append('\n');
                }
            }
        }
        return stackTraceBuilder.toString();
    }

    public Statistics getStatistics() {
        final Statistics queueStatistics = new Statistics();
        final long totalTasks = threadPoolExecutor.getTaskCount();
        final long executedTasks = threadPoolExecutor.getCompletedTaskCount();
        queueStatistics.setTotalTasks(totalTasks);
        queueStatistics.setExecutedTasks(executedTasks);
        queueStatistics.setRemainingTasks(totalTasks - executedTasks);
        queueStatistics.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
        queueStatistics.setMaxPoolSize(threadPoolExecutor.getMaximumPoolSize());
        queueStatistics.setKeepAliveTimeSecs(threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        final Set<Task> statTasks = queueStatistics.getTasks();
        synchronized (tasks) {
            for (final Task aTask : tasks) {
                final Task task = new Task();
                task.setReference(aTask.getReference());
                task.setDescription(aTask.getDescription());
                task.setCreationDate(aTask.getCreationDate());
                task.setState(aTask.getState());
                statTasks.add(task);
            }
        }
        return queueStatistics;
    }

    public class Task {
        private String reference;
        private String description;
        private ReferenceRunnable referenceRunnable;
        private DateTime creationDate;
        private State state;
        private Thread thread;

        public ReferenceRunnable getReferenceRunnable() {
            return referenceRunnable;
        }

        public void setReferenceRunnable(ReferenceRunnable referenceRunnable) {
            this.referenceRunnable = referenceRunnable;
        }

        public DateTime getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(DateTime creationDate) {
            this.creationDate = creationDate;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public State getState() {
            return state;
        }

        public void setState(State state) {
            this.state = state;
        }

        public Thread getThread() {
            return thread;
        }

        public void setThread(Thread thread) {
            this.thread = thread;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }

    public static enum State {
        DECLARED, EXECUTING, FINISHED, REJECTED, DISMISSED
    }

    public class Statistics {
        private long totalTasks;
        private long executedTasks;
        private long remainingTasks;
        private int corePoolSize;
        private int maxPoolSize;
        private long keepAliveTimeSecs;
        private Set<Task> tasks;

        public Statistics () {
            tasks = new LinkedHashSet<Task>();
        }

        public long getTotalTasks() {
            return totalTasks;
        }

        public void setTotalTasks(final long totalTasks) {
            this.totalTasks = totalTasks;
        }

        public long getExecutedTasks() {
            return executedTasks;
        }

        public void setExecutedTasks(final long executedTasks) {
            this.executedTasks = executedTasks;
        }

        public long getRemainingTasks() {
            return remainingTasks;
        }

        public void setRemainingTasks(final long remainingTasks) {
            this.remainingTasks = remainingTasks;
        }

        public Set<Task> getTasks() {
            return tasks;
        }

        public void setTasks(Set<Task> tasks) {
            this.tasks = tasks;
        }

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public long getKeepAliveTimeSecs() {
            return keepAliveTimeSecs;
        }

        public void setKeepAliveTimeSecs(long keepAliveTimeSecs) {
            this.keepAliveTimeSecs = keepAliveTimeSecs;
        }

    }

    public class ReferenceThreadPoolExecutor extends ThreadPoolExecutor {

        public ReferenceThreadPoolExecutor (//
                final int corePoolSize, final int maxPoolSize, final long keepAliveTime, //
                final TimeUnit timeUnit, final BlockingQueue<Runnable> blockingQueue,//
                final RejectedExecutionHandler rejectedExceutionHandler) {
            super(corePoolSize, maxPoolSize, keepAliveTime, timeUnit, blockingQueue, rejectedExceutionHandler);
        }

        @Override
        protected void beforeExecute(Thread thread, Runnable runnable) {
            if (runnable instanceof ReferenceRunnable) {
                final ReferenceRunnable referenceRunnable = (ReferenceRunnable) runnable;
                LOG.info("Executing ReferenceRunnable with reference: {}", referenceRunnable.getReference());
                final Task task = getTaskByReference(referenceRunnable.getReference());
                task.setState(State.EXECUTING);
                task.setThread(thread);
            } else {
                LOG.warn("About to execute unexpected runnable:{}", runnable);
            }
            super.beforeExecute(thread, runnable);
        }

        @Override
        protected void afterExecute(final Runnable runnable, final Throwable t) {
            if (runnable instanceof ReferenceRunnable) {
                final ReferenceRunnable referenceRunnable = (ReferenceRunnable) runnable;
                LOG.info("Finished executing ReferenceRunnable with reference: " + referenceRunnable.getReference());
                final Task task = getTaskByReference(referenceRunnable.getReference());
                task.setState(State.FINISHED);
                task.setThread(null);
                dismiss(((ReferenceRunnable) runnable));
            } else {
                LOG.warn("Finished executing unexpected runnable:{}", runnable);
            }
            super.afterExecute(runnable, t);
        }

    }

    public void clearWorkLog() {
        // TODO Auto-generated method stub

    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

}
