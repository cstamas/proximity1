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
package org.abstracthorizon.proximity.indexer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.abstracthorizon.proximity.HashMapItemPropertiesImpl;
import org.abstracthorizon.proximity.ItemProperties;
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

// TODO: Auto-generated Javadoc
/**
 * The Class LuceneIndexer.
 */
public class LuceneIndexer
    extends AbstractIndexer
{

    /** The dirty item treshold. */
    private int dirtyItemTreshold = 100;

    /** The dirty items. */
    private int dirtyItems = 0;

    /** The index directory. */
    private Directory indexDirectory;

    /** The analyzer. */
    private Analyzer analyzer = new StandardAnalyzer();

    /**
     * Gets the dirty item treshold.
     * 
     * @return the dirty item treshold
     */
    public int getDirtyItemTreshold()
    {
        return dirtyItemTreshold;
    }

    /**
     * Sets the dirty item treshold.
     * 
     * @param dirtyItemTreshold the new dirty item treshold
     */
    public void setDirtyItemTreshold( int dirtyItemTreshold )
    {
        this.dirtyItemTreshold = dirtyItemTreshold;
    }

    /**
     * Sets the index directory.
     * 
     * @param path the new index directory
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void setIndexDirectory( String path )
        throws IOException
    {
        File pathFile = new File( path );
        if ( !( pathFile.exists() ) )
        {
            if ( !pathFile.mkdirs() )
            {
                throw new IllegalArgumentException( "The supplied directory parameter " + pathFile
                    + " does not exists and cannot be created!" );
            }
            else
            {
                logger.info( "Created indexer basedir {}", pathFile.getAbsolutePath() );
            }
        }
        else
        {
            if ( !pathFile.isDirectory() )
            {
                throw new IllegalArgumentException( "The supplied parameter " + path + " is not a directory!" );
            }
        }
        this.indexDirectory = FSDirectory.getDirectory( pathFile, false );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.indexer.AbstractIndexer#doInitialize()
     */
    protected void doInitialize()
    {
        try
        {
            IndexWriter writer = new IndexWriter( indexDirectory, analyzer, isRecreateIndexes()
                || !IndexReader.indexExists( indexDirectory ) );
            writer.optimize();
            writer.close();
        }
        catch ( IOException ex )
        {
            throw new StorageException( "Got IOException during index creation.", ex );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.indexer.AbstractIndexer#doAddItemProperties(org.abstracthorizon.proximity.ItemProperties)
     */
    protected synchronized void doAddItemProperties( ItemProperties item )
        throws StorageException
    {
        logger.debug( "Adding item to index" );
        try
        {
            // prevent duplication on idx
            IndexReader reader = IndexReader.open( indexDirectory );
            int deleted = reader.deleteDocuments( new Term( "UID", getItemUid( item ) ) );
            dirtyItems = dirtyItems + deleted;
            reader.close();
            IndexWriter writer = new IndexWriter( indexDirectory, analyzer, false );
            addItemToIndex( writer, getItemUid( item ), item );
            writer.close();
        }
        catch ( IOException ex )
        {
            throw new StorageException( "Got IOException during addition.", ex );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.indexer.AbstractIndexer#doAddItemProperties(java.util.List)
     */
    protected synchronized void doAddItemProperties( List items )
        throws StorageException
    {
        logger.debug( "Adding batch items to index" );
        try
        {
            IndexReader reader = IndexReader.open( indexDirectory );
            for ( Iterator i = items.iterator(); i.hasNext(); )
            {
                ItemProperties ip = (ItemProperties) i.next();
                // prevent duplication on idx
                int deleted = reader.deleteDocuments( new Term( "UID", getItemUid( ip ) ) );
                dirtyItems = dirtyItems + deleted;
            }
            reader.close();
            IndexWriter writer = new IndexWriter( indexDirectory, analyzer, false );
            for ( Iterator i = items.iterator(); i.hasNext(); )
            {
                ItemProperties ip = (ItemProperties) i.next();
                addItemToIndex( writer, getItemUid( ip ), ip );
            }
            // forcing optimize after a batch addition
            writer.optimize();
            writer.close();
            dirtyItems = 0;
        }
        catch ( IOException ex )
        {
            throw new StorageException( "Got IOException during addition.", ex );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.indexer.AbstractIndexer#doDeleteItemProperties(org.abstracthorizon.proximity.ItemProperties)
     */
    protected synchronized void doDeleteItemProperties( ItemProperties ip )
        throws StorageException
    {
        logger.debug( "Deleting item from index" );
        try
        {
            IndexReader reader = IndexReader.open( indexDirectory );
            int deleted = reader.deleteDocuments( new Term( "UID", getItemUid( ip ) ) );
            reader.close();
            logger.debug( "Deleted {} items from index for UID={}", Integer.toString( deleted ), getItemUid( ip ) );
            dirtyItems = dirtyItems + deleted;

            if ( dirtyItems > dirtyItemTreshold )
            {
                IndexWriter writer = new IndexWriter( indexDirectory, analyzer, false );
                logger.debug( "Optimizing Lucene index as dirtyItemTreshold is exceeded." );
                writer.optimize();
                dirtyItems = 0;
                writer.close();
            }

        }
        catch ( IOException ex )
        {
            throw new StorageException( "Got IOException during deletion.", ex );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.indexer.AbstractIndexer#doSearchByItemPropertiesExample(org.abstracthorizon.proximity.ItemProperties)
     */
    protected List doSearchByItemPropertiesExample( ItemProperties ip )
        throws StorageException
    {
        BooleanQuery query = new BooleanQuery();
        for ( Iterator i = ip.getAllMetadata().keySet().iterator(); i.hasNext(); )
        {
            String key = (String) i.next();
            if ( ip.getMetadata( key ) != null && ip.getMetadata( key ).length() > 0 )
            {
                Query termQ;
                if ( ip.getMetadata( key ).indexOf( "?" ) != -1 || ip.getMetadata( key ).indexOf( "*" ) != -1 )
                {
                    termQ = new WildcardQuery( new Term( key, ip.getMetadata( key ) ) );
                }
                else
                {
                    termQ = new FuzzyQuery( new Term( key, ip.getMetadata( key ) ) );
                }
                query.add( termQ, BooleanClause.Occur.MUST );
            }
        }
        return search( query );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abstracthorizon.proximity.indexer.AbstractIndexer#doSearchByQuery(java.lang.String)
     */
    protected List doSearchByQuery( String queryStr )
        throws IndexerException,
            StorageException
    {
        QueryParser qparser = new QueryParser( ItemProperties.METADATA_NAME, analyzer );
        try
        {
            Query query = qparser.parse( queryStr );
            return search( query );
        }
        catch ( ParseException ex )
        {
            throw new IndexerException( "Bad Query syntax!", ex );
        }
    }

    /**
     * Gets the item uid.
     * 
     * @param ip the ip
     * 
     * @return the item uid
     */
    protected String getItemUid( ItemProperties ip )
    {
        return ip.getRepositoryId() + ":" + ip.getPath();
    }

    /**
     * Adds the item to index.
     * 
     * @param writer the writer
     * @param UID the UID
     * @param item the item
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void addItemToIndex( IndexWriter writer, String UID, ItemProperties item )
        throws IOException
    {
        Document ipDoc = itemProperties2Document( item );
        ipDoc.add( new Field( "UID", UID, Field.Store.YES, Field.Index.UN_TOKENIZED ) );
        writer.addDocument( ipDoc );
        dirtyItems++;
        if ( dirtyItems > dirtyItemTreshold )
        {
            logger.debug( "Optimizing Lucene index as dirtyItemTreshold is exceeded." );
            writer.optimize();
            dirtyItems = 0;
        }
    }

    /**
     * Item properties2 document.
     * 
     * @param item the item
     * 
     * @return the document
     */
    protected Document itemProperties2Document( ItemProperties item )
    {
        Document result = new Document();
        String key;
        String md;
        result.add( new Field( DOC_PATH, item.getDirectoryPath(), Field.Store.YES, Field.Index.UN_TOKENIZED ) );
        result.add( new Field( DOC_NAME, item.getName(), Field.Store.YES, Field.Index.UN_TOKENIZED ) );
        result.add( new Field( DOC_REPO, item.getRepositoryId(), Field.Store.YES, Field.Index.UN_TOKENIZED ) );
        result.add( new Field( DOC_GROUP, item.getRepositoryGroupId(), Field.Store.YES, Field.Index.UN_TOKENIZED ) );
        // index all other stuff
        for ( Iterator i = getSearchableKeywords().iterator(); i.hasNext(); )
        {
            key = (String) i.next();
            md = item.getMetadata( key );
            if ( md != null )
            {
                result.add( new Field( key, item.getMetadata( key ), Field.Store.NO, Field.Index.TOKENIZED ) );
            }
        }
        return result;
    }

    /**
     * Search.
     * 
     * @param query the query
     * 
     * @return the list
     */
    protected List search( Query query )
    {
        try
        {
            IndexSearcher searcher = new IndexSearcher( indexDirectory );
            Hits hits = searcher.search( query );
            List result = new ArrayList( hits.length() );
            for ( int i = 0; i < hits.length(); i++ )
            {
                ItemProperties rip = new HashMapItemPropertiesImpl();
                Document doc = hits.doc( i );
                rip.setDirectoryPath( doc.getField( DOC_PATH ).stringValue() );
                rip.setName( doc.getField( DOC_NAME ).stringValue() );
                rip.setRepositoryId( doc.getField( DOC_REPO ).stringValue() );
                rip.setRepositoryGroupId( doc.getField( DOC_GROUP ).stringValue() );
                result.add( rip );
            }
            searcher.close();
            return result;
        }
        catch ( IOException ex )
        {
            throw new StorageException( "Got IOException during search by query.", ex );
        }
    }

}
