package org.abstracthorizon.proximity.scheduler.jobs;

import org.abstracthorizon.proximity.scheduler.AbstractProximityJob;
import org.quartz.JobExecutionContext;
import org.quartz.StatefulJob;

public class RepositoryReindexerJob extends AbstractProximityJob implements StatefulJob {
	
	public static final String REPOSITORY_ID_KEY = "repositoryId";

	protected void doExecute(JobExecutionContext ctx) throws Exception {
		if (ctx.getMergedJobDataMap().containsKey(REPOSITORY_ID_KEY)) {
			String repositoryId = (String) ctx.getMergedJobDataMap().getString(REPOSITORY_ID_KEY);
			getLogger().info("Scheduled reindexing of repository {}", repositoryId);
			getIndexer().reindex(repositoryId);
		} else {
			getLogger().info("Scheduled reindexing all repositories");
			getIndexer().reindex();
		}
	}

}
