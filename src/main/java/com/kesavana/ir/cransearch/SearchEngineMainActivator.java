package com.kesavana.ir.cransearch;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.similarities.AfterEffectL;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BasicModelIn;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.DFRSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.NormalizationH1;
import org.apache.lucene.search.similarities.Similarity;

import com.kesavana.ir.cransearch.impl.SearchEngineOptimizer;
import com.kesavana.ir.cransearch.impl.SearchIndexer;
import com.kesavana.ir.cransearch.model.QueryObject;
import com.kesavana.ir.cransearch.model.ResultantObject;
import com.kesavana.ir.cransearch.utils.FileUtils;
/**
 * 
 * @author arun
 *
 */
public class SearchEngineMainActivator {
	
	public static void main(String[] args) throws IOException, ParseException {
		
		FileUtils.createAllReqDir();
        SearchIndexer searchIndexer = new SearchIndexer(FileUtils.getAllDocumentsRecords(), FileUtils.getIndividualDocsPath(), 
        		FileUtils.getAllQueriesRecords(), FileUtils.getIndividualQueriesPath());
        SearchEngineOptimizer searchEngOpt = new SearchEngineOptimizer(FileUtils.getDocIndexPath());

        Map<String, Map<Analyzer, Similarity[]>> analyzersSimilarities = getAnalyzersAndSimilarities();        
        //analyzersSimilarities.forEach((k,v) -> System.out.println("filename : " + k + " Similarity : " + v));        
        //System.out.println(analyzersSimilarities.size());
        
        long sTime = System.currentTimeMillis();
        for (Entry<String, Map<Analyzer, Similarity[]>> anaSimIter : analyzersSimilarities.entrySet()) {   
        	
            String fileName = anaSimIter.getKey();
            Map<Analyzer, Similarity[]> anaSim = anaSimIter.getValue();
            searchEngOpt.configAndProcess(fileName, anaSim);

            searchIndexer.splitDocs();
            File docsDir = new File(FileUtils.getIndividualDocsPath());
            File[] filesDoc = docsDir.listFiles();
            System.out.println(fileName + " Processing the documents");
            int i = 0;
            for (File file: filesDoc) {
                if (file.isFile()) {
                		if(i <= 1400) {
                			searchEngOpt.createSingleDocument(searchIndexer.getDocumentById(++i));
                		}           
                }
            }
            System.out.println(fileName + " Finished processing the documents");
            searchEngOpt.close();

            searchIndexer.splitQueries();

            File queryDir = new File(FileUtils.getIndividualQueriesPath());
            File[] filesQuery = queryDir.listFiles();
            String fileToWrite = FileUtils.getResultantPath() + "/" + fileName;
            FileUtils.checkFileExists(fileToWrite);
            FileWriter resultSet = new FileWriter(fileToWrite);
            System.out.println(fileName + " Processing the queries");
            for(int j = 1; j < filesQuery.length + 1; j++) {
                QueryObject query = SearchIndexer.getQueryById(j);
                String result = "";
                String queryString = query.getqWords();
                ArrayList<ResultantObject> resultList = searchEngOpt.invokeSearch(queryString, anaSim);
                for (ResultantObject currentResult: resultList) {
                    resultSet.append(query.getqId()).append(" Q0 ").append(currentResult.getrId());
                    resultSet.append(" ").append(String.valueOf(currentResult.getRank())).append(" ");
                    resultSet.append(String.valueOf(currentResult.getScore())).append(" STANDARD\n");
                }
                resultSet.write(result);
            }
            System.out.println(fileName + " Finished processing the queries");
            resultSet.close();  
        }
        System.out.println("Total time for process: " + analyzersSimilarities.size() + " combination is (sec): " + (System.currentTimeMillis() - sTime)/1000);
    }
		
	public static Map<String, Map<Analyzer, Similarity[]>> getAnalyzersAndSimilarities() {
		Map<Analyzer, Similarity[]> analyzersSimilarities;
		ArrayList<Analyzer> analyzers = getAnalyzers();
		ArrayList<Similarity[]> similarityCombinations = getAllSimilaritiesCombinations();
		Map<String, Map<Analyzer, Similarity[]>> analyzersSimilaritiesMap = new HashMap<>();
		
		ArrayList<String> variablesNames = variableFileNames();
		int i = 0;
		for(Analyzer analyzer: analyzers) {
			for(Similarity[] similarityList: similarityCombinations) {
				analyzersSimilarities = new HashMap<Analyzer, Similarity[]>();	
				analyzersSimilarities.put(analyzer, similarityList);
				analyzersSimilaritiesMap.put(variablesNames.get(i++), analyzersSimilarities);
			}			
		}
		return analyzersSimilaritiesMap;
	}
	
	
	public static ArrayList<String> variableFileNames() {
		ArrayList<String> variableNames = new ArrayList<>();
		String namespace1[] = {"standard", "simple", "stop", "whitespace"};
		String namespace2[] = {"bm25", "idf", "bm25_idf", "dfrL", "bm25_idf_dfrL", "bm25_idf_dfrL_LM"};
		for(String var1: namespace1) {
			for(String var2: namespace2) {
				variableNames.add(var1 + "_" + var2);
			}
		}
		return variableNames;
	}
	
	public static ArrayList<Analyzer> getAnalyzers() {
		
		ArrayList<Analyzer> analyzerList = new ArrayList<>();
        analyzerList.add(new StandardAnalyzer());
        analyzerList.add(new SimpleAnalyzer());
        analyzerList.add(new StopAnalyzer());
        analyzerList.add(new WhitespaceAnalyzer());
        
        return analyzerList;
	}
	
	public static ArrayList<Similarity[]> getAllSimilaritiesCombinations() {
		
		ArrayList<Similarity[]> similaritiesList = new ArrayList<>();
		
		Similarity bm25[] = {
	            new BM25Similarity(2, (float) 0.90)
	        };
		similaritiesList.add(bm25);
		
		Similarity idf[] = {
	            new ClassicSimilarity()
	        };
		similaritiesList.add(idf);
		
		Similarity bm25_idf[] = {
	            new BM25Similarity(2, (float) 0.90),	            
	            new ClassicSimilarity()
	        };
		similaritiesList.add(bm25_idf);
		
		Similarity dfrL[] = {
	            new BM25Similarity(2, (float) 0.90),
	            new DFRSimilarity(new BasicModelIn(), new AfterEffectL(), new NormalizationH1()),
	            new ClassicSimilarity()
	        };
		similaritiesList.add(dfrL);
		
		Similarity bm25_idf_dfrL[] = {
	            new BM25Similarity(2, (float) 0.90),
	            new DFRSimilarity(new BasicModelIn(), new AfterEffectL(), new NormalizationH1()),
	            new ClassicSimilarity()
	        };
		similaritiesList.add(bm25_idf_dfrL);
		
		Similarity bm25_idf_dfrL_LM[] = {
	            new BM25Similarity(2, (float) 0.90),
	            new DFRSimilarity(new BasicModelIn(), new AfterEffectL(), new NormalizationH1()),
	            new LMDirichletSimilarity(),
	            new ClassicSimilarity()
	        };
		
		similaritiesList.add(bm25_idf_dfrL_LM);
		return similaritiesList;
		
	}
}
