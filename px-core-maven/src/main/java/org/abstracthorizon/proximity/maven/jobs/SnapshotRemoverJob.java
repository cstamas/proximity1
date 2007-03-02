package org.abstracthorizon.proximity.maven.jobs;

import org.abstracthorizon.proximity.scheduler.AbstractProximityJob;
import org.quartz.JobExecutionContext;

public class SnapshotRemoverJob extends AbstractProximityJob {

	protected void doExecute(JobExecutionContext ctx) throws Exception {
		// TODO Auto-generated method stub
		getLogger().info("DUMMY JOB");
	}

}
