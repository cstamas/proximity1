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
package org.abstracthorizon.proximity;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class ProximityIntegrationTest.
 */
public class ProximityIntegrationTest
    extends AbstractProximityIntegrationTest
{

    /** The proximity. */
    private Proximity proximity;

    /* (non-Javadoc)
     * @see org.springframework.test.AbstractSingleSpringContextTests#getConfigLocations()
     */
    protected String[] getConfigLocations()
    {
        return new String[] {
            "/org/abstracthorizon/proximity/applicationContext.xml",
            "/org/abstracthorizon/proximity/proximityHelpers.xml",
            "/org/abstracthorizon/proximity/proximityRepositories.xml" };
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
     * Gets the proximity.
     * 
     * @return the proximity
     */
    public Proximity getProximity()
    {
        return proximity;
    }

    /**
     * Gets the request.
     * 
     * @param path the path
     * 
     * @return the request
     */
    protected ProximityRequest getRequest( String path )
    {
        ProximityRequest request = new ProximityRequest();
        request.setPath( path );
        request.getAttributes().put( ProximityRequest.REQUEST_REMOTE_ADDRESS, "127.0.0.1" );
        return request;
    }

    /**
     * Test simple artifact.
     */
    public void testSimpleArtifact()
    {
        try
        {
            Item item = proximity.retrieveItem( getRequest( "/public/antlr/antlr/2.7.5/antlr-2.7.5.jar" ) );
            logger.info( "Got response of type " + item.getClass() + ":" + item );
        }
        catch ( ProximityException ex )
        {
            logger.error( "Got exception", ex );
            fail();
        }
        try
        {
            Item item = proximity.retrieveItem( getRequest( "/public/antlr/antlr/2.7.5/antlr-2.7.5.jar-NO_SUCH" ) );
            logger.info( "Got response of type " + item.getClass() + ":" + item );
            fail();
        }
        catch ( ProximityException ex )
        {
            logger.error( "Good, got exception", ex );
        }
    }

    /**
     * Test pom artifact.
     */
    public void testPomArtifact()
    {
        try
        {
            Item item = proximity.retrieveItem( getRequest( "/public/antlr/antlr/2.7.5/antlr-2.7.5.pom" ) );
            logger.info( "Got response of type " + item.getClass() + ":" + item );
        }
        catch ( ProximityException ex )
        {
            logger.error( "BAD, got exception", ex );
            fail();
        }
    }

    /**
     * Test metadatada artifact.
     */
    public void testMetadatadaArtifact()
    {
        try
        {
            Item item = proximity.retrieveItem( getRequest( "/public/ant/ant/maven-metadata.xml" ) );
            logger.info( "Got response of type " + item.getClass() + ":" + item );
        }
        catch ( ProximityException ex )
        {
            logger.error( "Got exception", ex );
            fail();
        }
        try
        {
            Item item = proximity.retrieveItem( getRequest( "/public/ant/ant/maven-metadata.xml.md5" ) );
            logger.info( "Got response of type " + item.getClass() + ":" + item );
        }
        catch ( ProximityException ex )
        {
            logger.error( "Got exception", ex );
            fail();
        }
    }

    /**
     * Test simple dir.
     */
    public void testSimpleDir()
    {
        try
        {
            List items = proximity.listItems( getRequest( "/" ) );
            logger.info( "Got response of type " + items.getClass() + ":" + items );
            items = proximity.listItems( getRequest( "/public" ) );
            logger.info( "Got response of type " + items.getClass() + ":" + items );
            items = proximity.listItems( getRequest( "/public/ant" ) );
            logger.info( "Got response of type " + items.getClass() + ":" + items );
        }
        catch ( ProximityException ex )
        {
            logger.error( "Got ex but i should not have it!", ex );
            fail();
        }
    }

}
