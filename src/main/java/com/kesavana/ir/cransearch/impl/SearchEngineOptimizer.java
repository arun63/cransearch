package com.kesavana.ir.cransearch.impl;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.MultiSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;

import com.kesavana.ir.cransearch.common.SimpleAnalyzerInstance;
import com.kesavana.ir.cransearch.common.StandardAnalyzerInstance;
import com.kesavana.ir.cransearch.common.StopAnalyzerInstance;
import com.kesavana.ir.cransearch.common.WhitespaceAnalyzerInstance;
import com.kesavana.ir.cransearch.model.DocumentObject;
import com.kesavana.ir.cransearch.model.ResultantObject;

/**
 * 
 * @author arun
 *
 */
public class SearchEngineOptimizer {
	
	private IndexWriter indexWriter;
    private FSDirectory indexFSDir;

    public SearchEngineOptimizer(String indexPath) throws IOException {
        indexFSDir = FSDirectory.open(Paths.get(indexPath));
    }

    public void configAndProcess(String analyserName, Map<Analyzer, Similarity[]> analyserSimilarity) throws IOException {
        //Map<String, Analyzer> hashMap = new HashMap<>();
        //hashMap.put("Abstract", new StandardAnalyzerInstance());
        
        for (Entry<Analyzer, Similarity[]> anaSim : analyserSimilarity.entrySet()) {
            System.out.println("Config - Analyzer :: " + analyserName);         
            Analyzer analyzer = new PerFieldAnalyzerWrapper(anaSim.getKey() /*,getAbstractAnalyzerInstance(anaSim.getKey())*/);
            IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);
            iwConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            iwConfig.setSimilarity(new MultiSimilarity(anaSim.getValue()));
            indexWriter = new IndexWriter(indexFSDir, iwConfig);
            
        }
    }

    public void createSingleDocument(DocumentObject singleDoc) throws IOException {
        Document document = new Document();
        document.add(new StringField("ID", singleDoc.getDocId(), Field.Store.YES));
        TextField titleField = new TextField("Title", singleDoc.getDocTitle(), Field.Store.NO);
        document.add(titleField);
        document.add(new TextField("Authors", singleDoc.getAuth(), Field.Store.NO));
        document.add(new TextField("Bib", singleDoc.getBib(), Field.Store.NO));
        TextField abstractField = new TextField("Words", singleDoc.getWords(), Field.Store.NO);
        document.add(abstractField);
        indexWriter.addDocument(document);
    }

    public ArrayList<ResultantObject> invokeSearch(String searchQuery, Map<Analyzer, Similarity[]> analyserSimilarity) throws IOException, ParseException {
        DirectoryReader indexDirReader = DirectoryReader.open(indexFSDir);
        IndexSearcher indexSearcher = new IndexSearcher(indexDirReader);
        String fieldsArr[] = {"Title", "Abstract"};
        String qString = normalize(searchQuery);
        
        //Map<String, Analyzer> map = new HashMap<>();
        //map.put("Abstract", new StandardAnalyzerInstance());
        Analyzer analyzer = null;
        for (Entry<Analyzer, Similarity[]> anaSim : analyserSimilarity.entrySet()) {
            //System.out.println("Inside invoke search Analyzer: " + anaSim.getKey() + " sim:" + anaSim.getValue());          
            indexSearcher.setSimilarity(new MultiSimilarity(anaSim.getValue()));           
            analyzer = new PerFieldAnalyzerWrapper(anaSim.getKey(), getAbstractAnalyzerInstance(anaSim.getKey()));           
            
        }
        QueryParser queryParser = new MultiFieldQueryParser(fieldsArr, analyzer);
        Query query = queryParser.parse(qString);
        TopDocs topDocs = indexSearcher.search(query, 1000);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        ArrayList<ResultantObject> resultDocs = new ArrayList<>();
        int i = 1;
        for (ScoreDoc scoreDoc: scoreDocs) {
            String currentID = indexSearcher.doc(scoreDoc.doc).get("ID");
            ResultantObject currentDoc = new ResultantObject(currentID, scoreDoc.score, i++);
            resultDocs.add(currentDoc);
        }
        
        return resultDocs;       
    }
    
    
    public Map<String, Analyzer> getAbstractAnalyzerInstance(Analyzer ana) {
    		Map<String, Analyzer> map = new HashMap<>();
    		String key = "Abstract";
    		if(ana instanceof StandardAnalyzer) {
    			map.put(key, new StandardAnalyzerInstance());
    		} else if(ana instanceof SimpleAnalyzer) {
    			map.put(key, new SimpleAnalyzerInstance());
    		} else if(ana instanceof StopAnalyzer) {
    			map.put(key, new StopAnalyzerInstance());
    		} else if(ana instanceof WhitespaceAnalyzer) {
    			map.put(key, new WhitespaceAnalyzerInstance());
    		}
    		return map;
    }

    public void close() throws IOException {
        indexWriter.close();
    }
    
    private String normalize(String query) {
    		return QueryParser.escape(query);
    }
}
