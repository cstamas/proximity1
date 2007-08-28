/*

   Copyright 2005-2007 Tamas Cservenak (t.cservenak@gmail.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
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

// TODO: Auto-generated Javadoc
/**
 * The Class SnapshotRemoverJob.
 */
public class SnapshotRemoverJob
    extends AbstractProximityJob
{

    /** The indexer. */
    private Indexer indexer;

    /** The proximity. */
    private Proximity proximity;

    /** The repository id. */
    private String repositoryId = null;

    /** The min count of snapshots to keep. */
    private int minCountOfSnapshotsToKeep = 5;

    /** The remove snapshots older than days. */
    private int removeSnapshotsOlderThanDays = 7;

    /**
     * Gets the proximity.
     * 
     * @return the proximity
     */
    protected Proximity getProximity()
    {
        return this.proximity;
    }

    /**
     * Sets the proximity.
     * 
     * @param proximity the new proximity
     */
    public void setProximity( Proximity proximity )
    {
        this.proximity = proximity;
    }

    /**
     * Gets the indexer.
     * 
     * @return the indexer
     */
    protected Indexer getIndexer()
    {
        return this.indexer;
    }

    /**
     * Sets the indexer.
     * 
     * @param indexer the new indexer
     */
    public void setIndexer( Indexer indexer )
    {
        this.indexer = indexer;
    }

    /**
     * Gets the min count of snapshots to keep.
     * 
     * @return the min count of snapshots to keep
     */
    public int getMinCountOfSnapshotsToKeep()
    {
        return minCountOfSnapshotsToKeep;
    }

    /**
     * Sets the min count of snapshots to keep.
     * 
     * @param minCountOfSnapshotsToKeep the new min count of snapshots to keep
     */
    public void setMinCountOfSnapshotsToKeep( int minCountOfSnapshotsToKeep )
    {
        this.minCountOfSnapshotsToKeep = minCountOfSnapshotsToKeep;
    }

    /**
     * Gets the remove snapshots older than days.
     * 
     * @return the remove snapshots older than days
     */
    public int getRemoveSnapshotsOlderThanDays()
    {
        return removeSnapshotsOlderThanDays;
    }

    /**
     * Sets the remove snapshots older than days.
     * 
     * @param removeSnapshotsOlderThanDays the new remove snapshots older than days
     */
    public void setRemoveSnapshotsOlderThanDays( int removeSnapshotsOlderThanDays )
    {
        this.removeSnapshotsOlderThanDays = removeSnapshotsOlderThanDays;
    }

    /**
     * Gets the repository id.
     * 
     * @return the repository id
     */
    public String getRepositoryId()
    {
        return repositoryId;
    }

    /**
     * Sets the repository id.
     * 
     * @param repositoryId the new repository id
     */
    public void setRepositoryId( String repositoryId )
    {
        this.repositoryId = repositoryId;
    }

    /* (non-Javadoc)
     * @see org.abstracthorizon.proximity.scheduler.AbstractProximityJob#doExecute(org.quartz.JobExecutionContext)
     */
    protected void doExecute( JobExecutionContext ctx )
        throws Exception
    {
        if ( getRepositoryId() == null )
        {
            getLogger().info( "Removing old SNAPSHOT deployments from all repositories." );
            for ( Iterator i = getProximity().getRepositories().iterator(); i.hasNext(); )
            {
                Repository repository = (Repository) i.next();
                removeSnapshotsFromMavenRepository( repository );
            }
        }
        else
        {
            getLogger().info( "Removing old SNAPSHOT deployments from {} repository.", getRepositoryId() );
            Repository repository = getProximity().getRepository( repositoryId );
            removeSnapshotsFromMavenRepository( repository );
        }
    }

    /**
     * Removes the snapshots from maven repository.
     * 
     * @param repository the repository
     * 
     * @throws Exception the exception
     */
    protected void removeSnapshotsFromMavenRepository( Repository repository )
        throws Exception
    {
        LocalStorage localStorage = repository.getLocalStorage();
        if ( localStorage != null && localStorage instanceof WritableFileSystemStorage )
        {
            WritableFileSystemStorage rwRepository = (WritableFileSystemStorage) localStorage;
            removeSnapshotsFromMavenRepository( rwRepository.getStorageBaseDir() );
            if ( getIndexer() != null )
            {
                getIndexer().reindex( repository.getId() );
            }
        }

    }

    /**
     * Removes the snapshots from maven repository.
     * 
     * @param repositoryRoot the repository root
     * 
     * @throws Exception the exception
     */
    protected void removeSnapshotsFromMavenRepository( File repositoryRoot )
        throws Exception
    {
        // implement snapshot removal from repositoryRoot File as root of the
        // remote repos
        getLogger().debug(
            "Removing old snapshot deployments from " + repositoryRoot.getAbsolutePath()
                + ", keeping minimum {} of them, removing older than {} days.",
            Integer.toString( minCountOfSnapshotsToKeep ),
            Integer.toString( removeSnapshotsOlderThanDays ) );
    }

}
