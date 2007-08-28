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

import org.abstracthorizon.proximity.indexer.Indexer;

// TODO: Auto-generated Javadoc
/**
 * The Class IndexerIntegrationTest.
 */
public class IndexerIntegrationTest
    extends AbstractProximityIntegrationTest
{

    /** The indexer. */
    private Indexer indexer;

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
     * Sets the indexer.
     * 
     * @param indexer the new indexer
     */
    public void setIndexer( Indexer indexer )
    {
        this.indexer = indexer;
    }

    /**
     * Gets the indexer.
     * 
     * @return the indexer
     */
    public Indexer getIndexer()
    {
        return indexer;
    }

    /**
     * Test search by example1.
     * 
     * @throws ProximityException the proximity exception
     */
    public void testSearchByExample1()
        throws ProximityException
    {
        HashMapItemPropertiesImpl example = new HashMapItemPropertiesImpl();
        example.setName( "antlr-2.7.5.jar" );
        List result = indexer.searchByItemPropertiesExample( example );
        logger.info( "Got search result:" + result );
        example = new HashMapItemPropertiesImpl();
        example.setName( "antlr*" );
        result = indexer.searchByItemPropertiesExample( example );
        logger.info( "Got search result:" + result );
        example = new HashMapItemPropertiesImpl();
        example.setDirectoryPath( "/ismicro/jars" );
        result = indexer.searchByItemPropertiesExample( example );
        logger.info( "Got search result:" + result );
    }

}
