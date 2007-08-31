package org.abstracthorizon.proximity;

import java.util.List;

import org.abstracthorizon.proximity.indexer.Indexer;
import org.abstracthorizon.proximity.stats.StatisticsGatherer;
import org.codehaus.plexus.PlexusTestCase;

public class PlexusProximityTest
    extends PlexusTestCase
{
    
    protected Proximity proximity;
    
    protected Indexer indexer;
    
    protected StatisticsGatherer statisticsGatherer;

    public void setUp() throws Exception {
        super.setUp();

        proximity = (Proximity) lookup( Proximity.class, "logic-driven" );
        indexer = (Indexer) lookup( Indexer.class, "lucene" );
        indexer.initialize();
        statisticsGatherer = (StatisticsGatherer) lookup( StatisticsGatherer.class, "simple");
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
    public void testSimple() throws Exception
    {
        Repository repo = proximity.getRepository( "central");
        assertEquals( "central", repo.getId() );
        assertEquals( "public", repo.getGroupId() );
    }
    
    public void testSimpleArtifact() throws Exception
    {
        try
        {
            Item item = proximity.retrieveItem( getRequest( "/public/antlr/antlr/2.7.5/antlr-2.7.5.jar" ) );
            System.out.println( "Got response of type " + item.getClass() + ":" + item );
        }
        catch ( ProximityException ex )
        {
            System.out.println( "Got exception");
            ex.printStackTrace();
            fail();
        }
        try
        {
            Item item = proximity.retrieveItem( getRequest( "/public/antlr/antlr/2.7.5/antlr-2.7.5.jar-NO_SUCH" ) );
            System.out.println( "Got response of type " + item.getClass() + ":" + item );
            fail();
        }
        catch ( ProximityException ex )
        {
            System.out.println( "Good, got exception");
            ex.printStackTrace();
        }
    }

    /**
     * Test pom artifact.
     */
    public void testPomArtifact() throws Exception
    {
        try
        {
            Item item = proximity.retrieveItem( getRequest( "/public/antlr/antlr/2.7.5/antlr-2.7.5.pom" ) );
            System.out.println( "Got response of type " + item.getClass() + ":" + item );
        }
        catch ( ProximityException ex )
        {
            System.out.println( "BAD, got exception");
            ex.printStackTrace();
            fail();
        }
    }

    /**
     * Test metadatada artifact.
     */
    public void testMetadatadaArtifact() throws Exception
    {
        try
        {
            Item item = proximity.retrieveItem( getRequest( "/public/ant/ant/maven-metadata.xml" ) );
            System.out.println( "Got response of type " + item.getClass() + ":" + item );
        }
        catch ( ProximityException ex )
        {
            System.out.println( "Got exception");
            ex.printStackTrace();
            fail();
        }
        try
        {
            Item item = proximity.retrieveItem( getRequest( "/public/ant/ant/maven-metadata.xml.md5" ) );
            System.out.println( "Got response of type " + item.getClass() + ":" + item );
        }
        catch ( ProximityException ex )
        {
            System.out.println( "Got exception");
            ex.printStackTrace();
            fail();
        }
    }

    /**
     * Test simple dir.
     */
    public void testSimpleDir() throws Exception
    {
        try
        {
            List items = proximity.listItems( getRequest( "/" ) );
            System.out.println( "Got response of type " + items.getClass() + ":" + items );
            items = proximity.listItems( getRequest( "/public" ) );
            System.out.println( "Got response of type " + items.getClass() + ":" + items );
            items = proximity.listItems( getRequest( "/public/ant" ) );
            System.out.println( "Got response of type " + items.getClass() + ":" + items );
        }
        catch ( ProximityException ex )
        {
            System.out.println( "Got ex but i should not have it!");
            ex.printStackTrace();
            fail();
        }
    }

}
