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
package org.abstracthorizon.proximity.scheduler.jobs;

import org.abstracthorizon.proximity.indexer.Indexer;
import org.abstracthorizon.proximity.scheduler.AbstractProximityJob;
import org.quartz.JobExecutionContext;
import org.quartz.StatefulJob;

// TODO: Auto-generated Javadoc
/**
 * The Class RepositoryReindexerJob.
 */
public class RepositoryReindexerJob
    extends AbstractProximityJob
    implements StatefulJob
{

    /** The repository id. */
    private String repositoryId = null;

    /** The indexer. */
    private Indexer indexer;

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

    /* (non-Javadoc)
     * @see org.abstracthorizon.proximity.scheduler.AbstractProximityJob#doExecute(org.quartz.JobExecutionContext)
     */
    protected void doExecute( JobExecutionContext ctx )
        throws Exception
    {
        if ( repositoryId != null )
        {
            getLogger().info( "Scheduled reindexing of repository {}", repositoryId );
            getIndexer().reindex( repositoryId );
        }
        else
        {
            getLogger().info( "Scheduled reindexing all repositories" );
            getIndexer().reindex();
        }
    }

}
