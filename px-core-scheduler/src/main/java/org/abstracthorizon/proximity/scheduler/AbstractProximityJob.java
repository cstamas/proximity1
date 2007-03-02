package org.abstracthorizon.proximity.scheduler;

import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.indexer.Indexer;
import org.abstracthorizon.proximity.stats.StatisticsGatherer;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractProximityJob implements Job {
	
	public static final String PROXIMITY_KEY = "proximity";
	public static final String INDEXER_KEY = "indexer";
	public static final String STATISTICS_GATHERER_KEY = "statisticGatherer";

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Proximity proximity;

	private Indexer indexer;

	private StatisticsGatherer statisticGatherer;

	protected Logger getLogger() {
		return this.logger;
	}

	protected Proximity getProximity() {
		return this.proximity;
	}

	protected Indexer getIndexer() {
		return this.indexer;
	}

	protected StatisticsGatherer getStatisticsGatherer() {
		return this.statisticGatherer;
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		JobDataMap jobDataMap = context.getMergedJobDataMap();
		
		this.proximity = (Proximity) jobDataMap.get(PROXIMITY_KEY);
		this.indexer = (Indexer) jobDataMap.get(INDEXER_KEY);
		this.statisticGatherer = (StatisticsGatherer) jobDataMap.get(STATISTICS_GATHERER_KEY);
		
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
