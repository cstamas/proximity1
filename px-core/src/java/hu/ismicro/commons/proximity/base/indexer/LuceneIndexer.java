package hu.ismicro.commons.proximity.base.indexer;

import hu.ismicro.commons.proximity.ItemProperties;
import hu.ismicro.commons.proximity.base.Indexer;

import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;

public class LuceneIndexer implements Indexer {
	
	private IndexReader indexReader;
	private IndexWriter indexWriter;

	public void addItemProperties(ItemProperties ip) {
		// TODO Auto-generated method stub
		
	}

	public void deleteItemProperties(ItemProperties ip) {
		// TODO Auto-generated method stub
		
	}

	public List searchByItemPropertiesExample(ItemProperties ip) {
		// TODO Auto-generated method stub
		return null;
	}

}
