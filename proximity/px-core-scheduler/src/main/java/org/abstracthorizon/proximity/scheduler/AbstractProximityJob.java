package org.abstracthorizon.proximity.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

public abstract class AbstractProximityJob extends QuartzJobBean implements Job {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    protected Logger getLogger() {
	return this.logger;
    }

    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
	getLogger().info("Job {} started.", context.getJobDetail().getFullName());
	try {
	    doExecute(context);
	    getLogger().info("Job {} finished.", context.getJobDetail().getFullName());
	} catch (Exception ex) {
	    throw new JobExecutionException(ex);
	}
    }

    protected abstract void doExecute(JobExecutionContext ctx) throws Exception;

}
