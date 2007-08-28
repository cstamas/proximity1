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
package org.abstracthorizon.proximity.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractProximityJob.
 */
public abstract class AbstractProximityJob
    extends QuartzJobBean
    implements Job
{

    /** The logger. */
    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    /**
     * Gets the logger.
     * 
     * @return the logger
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /* (non-Javadoc)
     * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.quartz.JobExecutionContext)
     */
    public void executeInternal( JobExecutionContext context )
        throws JobExecutionException
    {
        getLogger().info( "Job {} started.", context.getJobDetail().getFullName() );
        try
        {
            doExecute( context );
            getLogger().info( "Job {} finished.", context.getJobDetail().getFullName() );
        }
        catch ( Exception ex )
        {
            throw new JobExecutionException( ex );
        }
    }

    /**
     * Do execute.
     * 
     * @param ctx the ctx
     * 
     * @throws Exception the exception
     */
    protected abstract void doExecute( JobExecutionContext ctx )
        throws Exception;

}
