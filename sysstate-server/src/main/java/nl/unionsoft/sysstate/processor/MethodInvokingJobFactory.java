package nl.unionsoft.sysstate.processor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * This class is used to create QuartzJobs that can invoke targets through reflection
 * based on the Spring applicationContext.
 * 
 * @author ckramer
 */
public class MethodInvokingJobFactory implements FactoryBean<JobDetail>, InitializingBean, BeanNameAware {
    private static final Logger LOG = LoggerFactory.getLogger(MethodInvokingJobFactory.class);

    private String group;
    private String name;
    private String beanName;
    private Boolean concurrent;

    private String targetObjectRef;
    private String targetMethodName;

    private JobDetail jobDetail;

    private String callMethod;

    public JobDetail getObject() throws Exception {

        return jobDetail;
    }

    public Class<?> getObjectType() {
        return JobDetail.class;
    }

    public boolean isSingleton() {
        return true;
    }

    /**
     * AfterPropertiesSet prepare the jobDetail
     */
    public void afterPropertiesSet() throws Exception {

        // Use specific name if given, else fall back to bean name.
        final String name = this.name == null ? beanName : this.name;

        // Consider the concurrent flag to choose between stateful and stateless
        // job.
        final Class<?> jobClass = (concurrent ? MethodInvokingJob.class : StatefulMethodInvokingJob.class);

        // Build JobDetail instance.
        jobDetail = new JobDetail(name, group, jobClass);
        final JobDataMap jobDataMap = jobDetail.getJobDataMap();
        jobDataMap.put("beanRef", targetObjectRef);
        jobDataMap.put("methodName", targetMethodName);
        jobDataMap.put("callMethod", callMethod);

        jobDetail.setVolatility(false);
        jobDetail.setDurability(true);
    }

    /**
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(final String group) {
        this.group = group;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the beanName
     */
    public String getBeanName() {
        return beanName;
    }

    /**
     * @param beanName the beanName to set
     */
    public void setBeanName(final String beanName) {
        this.beanName = beanName;
    }

    /**
     * @return the concurrent
     */
    public Boolean getConcurrent() {
        return concurrent;
    }

    /**
     * @param concurrent the concurrent to set
     */
    public void setConcurrent(final Boolean concurrent) {
        this.concurrent = concurrent;
    }

    /**
     * @return the targetObjectRef
     */
    public String getTargetObjectRef() {
        return targetObjectRef;
    }

    /**
     * @param targetObjectRef the targetObjectRef to set
     */
    public void setTargetObjectRef(final String targetObjectRef) {
        this.targetObjectRef = targetObjectRef;
    }

    /**
     * @return the targetMethodName
     */
    public String getTargetMethodName() {
        return targetMethodName;
    }

    /**
     * @param targetMethodName the targetMethodName to set
     */
    public void setTargetMethodName(final String targetMethodName) {
        this.targetMethodName = targetMethodName;
    }

    /**
     * @return the callMethod
     */
    public String getCallMethod() {
        return callMethod;
    }

    /**
     * @param callMethod the callMethod to set
     */
    public void setCallMethod(final String callMethod) {
        this.callMethod = callMethod;
    }

    /**
     * Helperclass that contains the actual job and knows how to invoke the given beanRef
     * 
     * @author ckramer
     */
    public static class MethodInvokingJob extends QuartzJobBean {

        @Override
        protected void executeInternal(final JobExecutionContext context) throws JobExecutionException {
            final Scheduler scheduler = context.getScheduler();
            final JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            final String beanRef = jobDataMap.getString("beanRef");
            final String methodName = jobDataMap.getString("methodName");

            CallMethod callMethod = null;
            final String callMethodString = jobDataMap.getString("callMethod");
            if (StringUtils.isEmpty(callMethodString)) {
                callMethod = CallMethod.ONCE;
            } else {
                callMethod = CallMethod.valueOf(callMethodString);
            }
            if (LOG.isInfoEnabled()) {
                LOG.info("callMethod for '" + beanRef + "." + methodName + "() is set to : " + callMethod);
            }

            try {
                final SchedulerContext schedulerCOntext = scheduler.getContext();
                final ApplicationContext applicationContext = (ApplicationContext) schedulerCOntext.get("applicationContext");
                final Object beanObject = applicationContext.getBean(beanRef);
                final Class<?> beanObjectClass = beanObject.getClass();
                final Method method = beanObjectClass.getMethod(methodName, new Class[] {});
                if (CallMethod.ONCE.equals(callMethod)) {
                    method.invoke(beanObject, new Object[] {});
                } else {
                    while ((Boolean) method.invoke(beanObject, new Object[] {}) == CallMethod.WHILE_RETURNS_TRUE.equals(callMethod)) {
                        LOG.info("Invoked object for while loop.");
                    }
                }

            } catch(final SchedulerException e) {
                throw new JobExecutionException(e);
            } catch(final SecurityException e) {
                throw new JobExecutionException(e);
            } catch(final NoSuchMethodException e) {
                throw new JobExecutionException(e);
            } catch(final IllegalAccessException e) {
                throw new JobExecutionException(e);
            } catch(final InvocationTargetException e) {
                throw new JobExecutionException(e);
            }
        }

    }

    public static class StatefulMethodInvokingJob extends MethodInvokingJob implements StatefulJob {
        // Nothing to see here, move along!
    }

    public enum CallMethod {
        ONCE, WHILE_RETURNS_TRUE, WHILE_RETURNS_FALSE;
    }
}
