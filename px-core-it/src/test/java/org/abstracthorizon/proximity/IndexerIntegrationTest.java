package org.abstracthorizon.proximity;

import java.util.List;

import org.abstracthorizon.proximity.impl.ItemPropertiesImpl;
import org.abstracthorizon.proximity.indexer.Indexer;

public class IndexerIntegrationTest extends AbstractProximityIntegrationTest {

    private Indexer indexer;

    public void setIndexer(Indexer indexer) {
        this.indexer = indexer;
    }

    public Indexer getIndexer() {
        return indexer;
    }

    public void testSearchByExample1() throws ProximityException {
        ItemPropertiesImpl example = new ItemPropertiesImpl();
        example.setName("antlr-2.7.5.jar");
        List result = indexer.searchByItemPropertiesExample(example);
        logger.info("Got search result:" + result);
        example = new ItemPropertiesImpl();
        example.setName("antlr*");
        result = indexer.searchByItemPropertiesExample(example);
        logger.info("Got search result:" + result);
        example = new ItemPropertiesImpl();
        example.setAbsolutePath("/ismicro/jars");
        result = indexer.searchByItemPropertiesExample(example);
        logger.info("Got search result:" + result);
    }

}
