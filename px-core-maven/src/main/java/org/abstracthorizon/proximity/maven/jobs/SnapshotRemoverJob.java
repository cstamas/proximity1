package org.abstracthorizon.proximity.maven.jobs;

import java.io.File;
import java.util.Iterator;

import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.Repository;
import org.abstracthorizon.proximity.indexer.Indexer;
import org.abstracthorizon.proximity.scheduler.AbstractProximityJob;
import org.abstracthorizon.proximity.storage.local.LocalStorage;
import org.abstracthorizon.proximity.storage.local.WritableFileSystemStorage;
import org.quartz.JobExecutionContext;

public class SnapshotRemoverJob extends AbstractProximityJob {

    private Indexer indexer;

    private Proximity proximity;

    private String repositoryId = null;

    private int minCountOfSnapshotsToKeep = 5;

    private int removeSnapshotsOlderThanDays = 7;

    protected Proximity getProximity() {
	return this.proximity;
    }

    public void setProximity(Proximity proximity) {
	this.proximity = proximity;
    }

    protected Indexer getIndexer() {
	return this.indexer;
    }

    public void setIndexer(Indexer indexer) {
	this.indexer = indexer;
    }

    public int getMinCountOfSnapshotsToKeep() {
	return minCountOfSnapshotsToKeep;
    }

    public void setMinCountOfSnapshotsToKeep(int minCountOfSnapshotsToKeep) {
	this.minCountOfSnapshotsToKeep = minCountOfSnapshotsToKeep;
    }

    public int getRemoveSnapshotsOlderThanDays() {
	return removeSnapshotsOlderThanDays;
    }

    public void setRemoveSnapshotsOlderThanDays(int removeSnapshotsOlderThanDays) {
	this.removeSnapshotsOlderThanDays = removeSnapshotsOlderThanDays;
    }

    public String getRepositoryId() {
	return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
	this.repositoryId = repositoryId;
    }

    protected void doExecute(JobExecutionContext ctx) throws Exception {
	if (getRepositoryId() == null) {
	    getLogger().info("Removing old SNAPSHOT deployments from all repositories.");
	    for (Iterator i = getProximity().getRepositories().iterator(); i.hasNext();) {
		Repository repository = (Repository) i.next();
		removeSnapshotsFromMavenRepository(repository);
	    }
	} else {
	    getLogger().info("Removing old SNAPSHOT deployments from {} repository.", getRepositoryId());
	    Repository repository = getProximity().getRepository(repositoryId);
	    removeSnapshotsFromMavenRepository(repository);
	}
    }

    protected void removeSnapshotsFromMavenRepository(Repository repository) throws Exception {
	LocalStorage localStorage = repository.getLocalStorage();
	if (localStorage != null && localStorage instanceof WritableFileSystemStorage) {
	    WritableFileSystemStorage rwRepository = (WritableFileSystemStorage) localStorage;
	    removeSnapshotsFromMavenRepository(rwRepository.getStorageBaseDir());
	    if (getIndexer() != null) {
		getIndexer().reindex(repository.getId());
	    }
	}

    }

    protected void removeSnapshotsFromMavenRepository(File repositoryRoot) throws Exception {
	// implement snapshot removal from repositoryRoot File as root of the
	// remote repos
	getLogger().debug(
		"Removing old snapshot deployments from " + repositoryRoot.getAbsolutePath()
			+ ", keeping minimum {} of them, removing older than {} days.", Integer.toString(minCountOfSnapshotsToKeep),
		Integer.toString(removeSnapshotsOlderThanDays));
    }

}
