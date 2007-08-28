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
package org.abstracthorizon.proximity.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.Proximity;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.ProximityRequestListener;
import org.abstracthorizon.proximity.events.ItemCacheEvent;
import org.abstracthorizon.proximity.events.ItemRetrieveEvent;
import org.abstracthorizon.proximity.events.ItemStoreEvent;
import org.abstracthorizon.proximity.events.ProximityRequestEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class SimpleStatisticsGathererImpl.
 */
public class SimpleStatisticsGathererImpl
    implements StatisticsGatherer, ProximityRequestListener
{

    /** The logger. */
    protected Logger logger = LoggerFactory.getLogger( this.getClass() );

    /** The proximity. */
    private Proximity proximity;

    /** The last10 local hits. */
    private List last10LocalHits = new ArrayList( 10 );

    /** The last10 remote hits. */
    private List last10RemoteHits = new ArrayList( 10 );

    /** The last10 artifacts. */
    private List last10Artifacts = new ArrayList( 10 );

    /** The last10 requester ip addresses. */
    private List last10RequesterIpAddresses = new ArrayList( 10 );

    /** The last10 deployer ip addresses. */
    private List last10DeployerIpAddresses = new ArrayList( 10 );

    /** The last10 deployments. */
    private List last10Deployments = new ArrayList( 10 );

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.stats.StatisticsGatherer#getProximity()
     */
    public Proximity getProximity()
    {
        return this.proximity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.stats.StatisticsGatherer#setProximity(org.abstracthorizon.proximity.Proximity)
     */
    public void setProximity( Proximity proximity )
    {
        this.proximity = proximity;
        proximity.addProximityRequestListener( this );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.stats.StatisticsGatherer#initialize()
     */
    public void initialize()
    {
        logger.info( "Initializing..." );
        // nothing
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.ProximityRequestListener#proximityRequestEvent(org.abstracthorizon.proximity.events.ProximityRequestEvent)
     */
    public void proximityRequestEvent( ProximityRequestEvent event )
    {
        if ( ItemCacheEvent.class.isAssignableFrom( event.getClass() ) )
        {
            remoteHit( event.getRequest(), ( (ItemCacheEvent) event ).getItemProperties() );
        }
        else if ( ItemStoreEvent.class.isAssignableFrom( event.getClass() ) )
        {
            deploymentHit( event.getRequest(), ( (ItemStoreEvent) event ).getItemProperties() );
        }
        else if ( ItemRetrieveEvent.class.isAssignableFrom( event.getClass() ) )
        {
            localHit( event.getRequest(), ( (ItemRetrieveEvent) event ).getItemProperties() );
        }

    }

    /**
     * Local hit.
     * 
     * @param req the req
     * @param ip the ip
     */
    public void localHit( ProximityRequest req, ItemProperties ip )
    {
        if ( ip.isFile() )
        {
            addMaxTen( last10Artifacts, ip );
            addMaxTen( last10LocalHits, ip );
            if ( req.getAttributes().get( ProximityRequest.REQUEST_REMOTE_ADDRESS ) != null )
            {
                addMaxTen( last10RequesterIpAddresses, req
                    .getAttributes().get( ProximityRequest.REQUEST_REMOTE_ADDRESS ) );
            }
        }
    }

    /**
     * Deployment hit.
     * 
     * @param req the req
     * @param ip the ip
     */
    public void deploymentHit( ProximityRequest req, ItemProperties ip )
    {
        if ( ip.isFile() )
        {
            addMaxTen( last10Deployments, ip );
            if ( req.getAttributes().get( ProximityRequest.REQUEST_REMOTE_ADDRESS ) != null )
            {
                addMaxTen( last10DeployerIpAddresses, req.getAttributes().get( ProximityRequest.REQUEST_REMOTE_ADDRESS ) );
            }
        }
    }

    /**
     * Remote hit.
     * 
     * @param req the req
     * @param ip the ip
     */
    public void remoteHit( ProximityRequest req, ItemProperties ip )
    {
        if ( ip.isFile() )
        {
            addMaxTen( last10Artifacts, ip );
            addMaxTen( last10RemoteHits, ip );
            if ( req.getAttributes().get( ProximityRequest.REQUEST_REMOTE_ADDRESS ) != null )
            {
                addMaxTen( last10RequesterIpAddresses, req
                    .getAttributes().get( ProximityRequest.REQUEST_REMOTE_ADDRESS ) );
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.stats.StatisticsGatherer#getStatistics()
     */
    public Map getStatistics()
    {
        Map result = new HashMap();
        result.put( "last10LocalHits", last10LocalHits );
        result.put( "last10RemoteHits", last10RemoteHits );
        result.put( "last10Artifacts", last10Artifacts );
        result.put( "last10Deployments", last10Deployments );
        result.put( "last10RequesterIpAddresses", last10RequesterIpAddresses );
        result.put( "last10DeployerIpAddresses", last10DeployerIpAddresses );
        return new HashMap( result );
    }

    /**
     * Adds the max ten.
     * 
     * @param list the list
     * @param obj the obj
     */
    protected void addMaxTen( List list, Object obj )
    {
        while ( list.size() > 10 )
        {
            list.remove( 0 );
        }
        if ( !list.contains( obj ) )
        {
            list.add( obj );
        }
    }

}
