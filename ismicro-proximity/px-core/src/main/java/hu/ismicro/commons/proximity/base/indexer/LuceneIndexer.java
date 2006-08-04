package hu.ismicro.commons.proximity.base.indexer;

import hu.ismicro.commons.proximity.ItemNotFoundException;
import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.base.IndexerException;
import hu.ismicro.commons.proximity.base.ProxiedItemProperties;
import hu.ismicro.commons.proximity.base.StorageException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneIndexer extends AbstractIndexer {

    private boolean recreateIndexes = true;

    private int dirtyItemTreshold = 100;

    private int dirtyItems = 0;

    private Directory indexDirectory;

    private Analyzer analyzer = new SimpleAnalyzer();

    public boolean isRecreateIndexes() {
        return recreateIndexes;
    }

    public void setRecreateIndexes(boolean recreateIndexes) {
        this.recreateIndexes = recreateIndexes;
    }

    public int getDirtyItemTreshold() {
        return dirtyItemTreshold;
    }

    public void setDirtyItemTreshold(int dirtyItemTreshold) {
        this.dirtyItemTreshold = dirtyItemTreshold;
    }

    protected void doInitialize() {
        try {
            if (recreateIndexes) {
                logger.info("Recreating indexes as instructed by recreateIndexes parameter.");
                String[] files = indexDirectory.list();
                for (int i = 0; i < files.length; i++) {
                    indexDirectory.deleteFile(files[i]);
                }
            }
            IndexWriter writer = new IndexWriter(indexDirectory, analyzer, recreateIndexes
                    || !IndexReader.indexExists(indexDirectory));
            writer.optimize();
            writer.close();
        } catch (IOException ex) {
            logger.error("Got IOException during index addition.", ex);
            throw new StorageException("Got IOException during index creation.", ex);
        }
    }

    public void setIndexDirectory(String path) throws IOException {
        File pathFile = new File(path);
        if (!(pathFile.exists())) {
            if (!pathFile.mkdirs()) {
                throw new IllegalArgumentException("The supplied directory parameter " + pathFile
                        + " does not exists and cannot be created!");
            } else {
                logger.info("Created indexer basedir " + pathFile.getAbsolutePath());
            }
        }
        if (!pathFile.isDirectory()) {
            throw new IllegalArgumentException("The supplied parameter " + path + " is not a directory!");
        }
        this.indexDirectory = FSDirectory.getDirectory(pathFile, false);
    }

    public synchronized void addItemProperties(String UID, ItemProperties item) throws StorageException {
        logger.debug("Adding item to index");
        try {
            // prevent duplication on idx
            IndexReader reader = IndexReader.open(indexDirectory);
            int deleted = reader.deleteDocuments(new Term("UID", UID));
            dirtyItems = dirtyItems + deleted;
            reader.close();
            IndexWriter writer = new IndexWriter(indexDirectory, analyzer, false);
            addItemToIndex(writer, UID, item);
            writer.close();
        } catch (IOException ex) {
            logger.error("Got IOException during index addition.", ex);
            throw new StorageException("Got IOException during addition.", ex);
        }
    }

    public synchronized void addItemProperties(Map uidWithItems) throws StorageException {
        logger.debug("Adding batch items to index");
        try {
            IndexReader reader = IndexReader.open(indexDirectory);
            String UID = null;
            ItemProperties item = null;
            for (Iterator i = uidWithItems.keySet().iterator(); i.hasNext();) {
                UID = (String) i.next();
                // prevent duplication on idx
                int deleted = reader.deleteDocuments(new Term("UID", UID));
                dirtyItems = dirtyItems + deleted;
            }
            reader.close();
            IndexWriter writer = new IndexWriter(indexDirectory, analyzer, false);
            for (Iterator i = uidWithItems.keySet().iterator(); i.hasNext();) {
                UID = (String) i.next();
                item = (ItemProperties) uidWithItems.get(UID);
                addItemToIndex(writer, UID, item);
            }
            // forcing optimize after a batch addition
            writer.optimize();
            writer.close();
            dirtyItems = 0;
        } catch (IOException ex) {
            logger.error("Got IOException during index addition.", ex);
            throw new StorageException("Got IOException during addition.", ex);
        }
    }

    public synchronized void deleteItemProperties(String UID, ItemProperties ip) throws ItemNotFoundException,
            StorageException {
        logger.debug("Deleting item from index");
        try {
            IndexReader reader = IndexReader.open(indexDirectory);
            int deleted = reader.deleteDocuments(new Term("UID", UID));
            reader.close();
            logger.info("Deleted " + deleted + " items from index for UID=" + UID);
            dirtyItems = dirtyItems + deleted;

            if (dirtyItems > dirtyItemTreshold) {
                IndexWriter writer = new IndexWriter(indexDirectory, analyzer, false);
                logger.info("Optimizing Lucene index as dirtyItemTreshold is exceeded.");
                writer.optimize();
                dirtyItems = 0;
                writer.close();
            }

        } catch (IOException ex) {
            logger.error("Got IOException during index deletion.", ex);
            throw new StorageException("Got IOException during deletion.", ex);
        }
    }

    public List searchByItemPropertiesExample(ItemProperties ip) throws StorageException {
        BooleanQuery query = new BooleanQuery();
        for (Iterator i = ip.getAllMetadata().keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            Query termQ;
            if (ip.getMetadata(key).indexOf("?") != -1 || ip.getMetadata(key).indexOf("*") != -1) {
                termQ = new WildcardQuery(new Term(key, ip.getMetadata(key)));
            } else {
                termQ = new FuzzyQuery(new Term(key, ip.getMetadata(key)));
            }
            query.add(termQ, BooleanClause.Occur.SHOULD);
        }
        return search(query);
    }

    public List searchByQuery(String queryStr) throws IndexerException, StorageException {
        QueryParser qparser = new QueryParser(ItemProperties.METADATA_NAME, analyzer);
        try {
            Query query = qparser.parse(queryStr);
            return search(query);
        } catch (ParseException ex) {
            logger.error("Bad query syntax!", ex);
            throw new IndexerException("Bad Query syntax!", ex);
        }
    }

    protected Document itemProperties2Document(ItemProperties item) {
        Document result = new Document();
        for (Iterator i = item.getAllMetadata().keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            result.add(new Field(key, item.getMetadata(key), Field.Store.YES, Field.Index.UN_TOKENIZED));
        }
        return postProcessDocument(item, result);
    }

    protected Document postProcessDocument(ItemProperties item, Document doc) {
        return doc;
    }

    protected void addItemToIndex(IndexWriter writer, String UID, ItemProperties item) throws IOException {
        Document ipDoc = itemProperties2Document(item);
        ipDoc.add(new Field("UID", UID, Field.Store.YES, Field.Index.UN_TOKENIZED));
        writer.addDocument(ipDoc);
        dirtyItems++;
        if (dirtyItems > dirtyItemTreshold) {
            logger.info("Optimizing Lucene index as dirtyItemTreshold is exceeded.");
            writer.optimize();
            dirtyItems = 0;
        }
    }

    protected List search(Query query) {
        try {
            IndexSearcher searcher = new IndexSearcher(indexDirectory);
            Hits hits = searcher.search(query);
            List result = new ArrayList(hits.length());
            for (int i = 0; i < hits.length(); i++) {
                ProxiedItemProperties rip = new ProxiedItemProperties();
                Map props = new HashMap();
                Document doc = hits.doc(i);
                for (Enumeration fields = doc.fields(); fields.hasMoreElements();) {
                    Field field = (Field) fields.nextElement();
                    props.put(field.name(), field.stringValue());
                }
                rip.getAllMetadata().putAll(props);
                result.add(rip);
            }
            searcher.close();
            return result;
        } catch (IOException ex) {
            logger.error("Got IOException during index deletion.", ex);
            throw new StorageException("Got IOException during deletion.", ex);
        }
    }

}
