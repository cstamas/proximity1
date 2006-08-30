package org.abstracthorizon.proximity.indexer;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.abstracthorizon.proximity.ItemNotFoundException;
import org.abstracthorizon.proximity.ItemProperties;
import org.abstracthorizon.proximity.impl.ItemPropertiesImpl;
import org.abstracthorizon.proximity.storage.StorageException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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

    public static String DOC_PATH = "_path";

    public static String DOC_NAME = "_name";

    public static String DOC_REPO = "_repo";

    private boolean recreateIndexes = true;

    private int dirtyItemTreshold = 100;

    private int dirtyItems = 0;

    private Directory indexDirectory;

    private Analyzer analyzer = new StandardAnalyzer();

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
        List kws = getSearchableKeywords();
        kws.add(DOC_PATH);
        kws.add(DOC_NAME);
        kws.add(DOC_REPO);
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
                logger.info("Created indexer basedir {}", pathFile.getAbsolutePath());
            }
        }
        if (!pathFile.isDirectory()) {
            throw new IllegalArgumentException("The supplied parameter " + path + " is not a directory!");
        }
        this.indexDirectory = FSDirectory.getDirectory(pathFile, false);
    }

    public synchronized void addItemProperties(ItemProperties item) throws StorageException {
        logger.debug("Adding item to index");
        try {
            // prevent duplication on idx
            IndexReader reader = IndexReader.open(indexDirectory);
            int deleted = reader.deleteDocuments(new Term("UID", getItemUid(item)));
            dirtyItems = dirtyItems + deleted;
            reader.close();
            IndexWriter writer = new IndexWriter(indexDirectory, analyzer, false);
            addItemToIndex(writer, getItemUid(item), item);
            writer.close();
        } catch (IOException ex) {
            throw new StorageException("Got IOException during addition.", ex);
        }
    }

    public synchronized void addItemProperties(List items) throws StorageException {
        logger.debug("Adding batch items to index");
        try {
            IndexReader reader = IndexReader.open(indexDirectory);
            for (Iterator i = items.iterator(); i.hasNext();) {
                ItemProperties ip = (ItemProperties) i.next();
                // prevent duplication on idx
                int deleted = reader.deleteDocuments(new Term("UID", getItemUid(ip)));
                dirtyItems = dirtyItems + deleted;
            }
            reader.close();
            IndexWriter writer = new IndexWriter(indexDirectory, analyzer, false);
            for (Iterator i = items.iterator(); i.hasNext();) {
                ItemProperties ip = (ItemProperties) i.next();
                addItemToIndex(writer, getItemUid(ip), ip);
            }
            // forcing optimize after a batch addition
            writer.optimize();
            writer.close();
            dirtyItems = 0;
        } catch (IOException ex) {
            throw new StorageException("Got IOException during addition.", ex);
        }
    }

    public synchronized void deleteItemProperties(ItemProperties ip) throws ItemNotFoundException, StorageException {
        logger.debug("Deleting item from index");
        try {
            IndexReader reader = IndexReader.open(indexDirectory);
            int deleted = reader.deleteDocuments(new Term("UID", getItemUid(ip)));
            reader.close();
            logger.info("Deleted {} items from index for UID={}", Integer.toString(deleted), getItemUid(ip));
            dirtyItems = dirtyItems + deleted;

            if (dirtyItems > dirtyItemTreshold) {
                IndexWriter writer = new IndexWriter(indexDirectory, analyzer, false);
                logger.info("Optimizing Lucene index as dirtyItemTreshold is exceeded.");
                writer.optimize();
                dirtyItems = 0;
                writer.close();
            }

        } catch (IOException ex) {
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
            query.add(termQ, BooleanClause.Occur.MUST);
        }
        return search(query);
    }

    public List searchByQuery(String queryStr) throws IndexerException, StorageException {
        QueryParser qparser = new QueryParser(ItemProperties.METADATA_NAME, analyzer);
        try {
            Query query = qparser.parse(queryStr);
            return search(query);
        } catch (ParseException ex) {
            throw new IndexerException("Bad Query syntax!", ex);
        }
    }

    protected String getItemUid(ItemProperties ip) {
        return ip.getRepositoryId() + ":" + ip.getPath();
    }

    protected Document itemProperties2Document(ItemProperties item) {
        Document result = new Document();
        String key;
        String md;
        result.add(new Field(DOC_PATH, item.getAbsolutePath(), Field.Store.YES,
                Field.Index.UN_TOKENIZED));
        result.add(new Field(DOC_NAME, item.getName(), Field.Store.YES, Field.Index.UN_TOKENIZED));
        result.add(new Field(DOC_REPO, item.getRepositoryId(), Field.Store.YES,
                Field.Index.UN_TOKENIZED));
        // index all other stuff
        for (Iterator i = getSearchableKeywords().iterator(); i.hasNext();) {
            key = (String) i.next();
            md = item.getMetadata(key);
            if (md != null) {
                result.add(new Field(key, item.getMetadata(key), Field.Store.NO, Field.Index.TOKENIZED));
            }
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
                ItemPropertiesImpl rip = new ItemPropertiesImpl();
                Document doc = hits.doc(i);
                rip.setAbsolutePath(doc.getField(DOC_PATH).stringValue());
                rip.setName(doc.getField(DOC_NAME).stringValue());
                rip.setRepositoryId(doc.getField(DOC_REPO).stringValue());
                result.add(rip);
            }
            searcher.close();
            return result;
        } catch (IOException ex) {
            throw new StorageException("Got IOException during search by query.", ex);
        }
    }

}
