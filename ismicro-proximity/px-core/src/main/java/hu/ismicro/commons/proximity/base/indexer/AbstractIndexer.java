package hu.ismicro.commons.proximity.base.indexer;

import hu.ismicro.commons.proximity.Item;
import hu.ismicro.commons.proximity.base.Indexer;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public abstract class AbstractIndexer implements Indexer {

    protected Log logger = LogFactory.getLog(this.getClass());

    public void initialize() {
        logger.info("Initializing indexer " + this.getClass().getName() + "...");
        doInitialize();
    }
    
    protected abstract void doInitialize();

    protected Document itemProperties2Document(Item item) {
        Document result = new Document();
        for (Iterator i = item.getProperties().getAllMetadata().keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            result.add(Field.Keyword(key, item.getProperties().getMetadata(key)));
        }
        return postProcessDocument(item, result);
    }

    protected Document postProcessDocument(Item item, Document doc) {
        return doc;
    }

}
