package org.abstracthorizon.proximity.scheduler.jobs;

import org.abstracthorizon.proximity.indexer.Indexer;
import org.abstracthorizon.proximity.scheduler.AbstractProximityJob;
import org.quartz.JobExecutionContext;
import org.quartz.StatefulJob;

public class RepositoryReindexerJob extends AbstractProximityJob implements StatefulJob {
    
    private String repositoryId = null;

    private Indexer indexer;

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    protected Indexer getIndexer() {
	return this.indexer;
    }

    public void setIndexer(Indexer indexer) {
	this.indexer = indexer;
    }

    protected void doExecute(JobExecutionContext ctx) throws Exception {
	if (repositoryId != null) {
	    getLogger().info("Scheduled reindexing of repository {}", repositoryId);
	    getIndexer().reindex(repositoryId);
	} else {
	    getLogger().info("Scheduled reindexing all repositories");
	    getIndexer().reindex();
	}
    }

}
