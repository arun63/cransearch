package com.kesavana.ir.cransearch.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.kesavana.ir.cransearch.model.DocumentObject;
import com.kesavana.ir.cransearch.model.QueryObject;
import com.kesavana.ir.cransearch.utils.FileUtils;

/**
 * 
 * @author arun
 *
 */
public class SearchIndexer {
	
	private String allCranRecordsPath;
    private String individualDocsPath;
    private static String allQueriesPath;
    private static String individualQueryPath;

    public SearchIndexer(String allCranRecordsPath, String individualDocsPath, String allQueriesPath, String individualQueryPath) {
        this.allCranRecordsPath = allCranRecordsPath;
        this.individualDocsPath = individualDocsPath;
        this.allQueriesPath = allQueriesPath;
        this.individualQueryPath = individualQueryPath;
    }

    public void splitDocs() throws IOException {
    		splitRecords(allCranRecordsPath, individualDocsPath, FileUtils.getIndividualDocumentFilename());
    }

    public void splitQueries() throws IOException {
    		splitRecords(allQueriesPath, individualQueryPath, FileUtils.getIndividualQueryFilename());
    }
    
    private void splitRecords(String readerPath, String writerPath, String fileName) { 
    		FileReader fReader = null;
    		BufferedReader bReader = null;
    		FileWriter fWriter = null;
			try {
				fReader = new FileReader(new File(readerPath));
				bReader = new BufferedReader(fReader);
				String q = bReader.readLine();
		        int i = 1;
		        while (q != null) {
		            fWriter = new FileWriter(writerPath + fileName + i);
		            fWriter.append(q).append("\n");
		            q = bReader.readLine();
		            while (q != null && !q.startsWith(".I")) {
		                fWriter.append(q).append("\n");
		                q = bReader.readLine();
		            }
		            i++;
		            fWriter.close();
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
    }

    public DocumentObject getDocumentById(int id) {
        FileReader fReader = null;
        BufferedReader bReader = null;
        String docTitle = "";
        String docAuthors = "";
        String docBib = "";
        String docWords = "";
		try {
			fReader = new FileReader(new File(individualDocsPath + FileUtils.getIndividualDocumentFilename() + id));
			bReader = new BufferedReader(fReader);
			String document = bReader.readLine();
			//System.out.println("doc id: " + document);
			document = bReader.readLine();
	        while (document != null) {
	            switch (document) {
	                case ".T":
	                    document = bReader.readLine();
	                    while (!document.equals(".A")) {
	                        docTitle += document + "\n";
	                        document = bReader.readLine();
	                    }
	                    break;
	                case ".A":
	                    document = bReader.readLine();
	                    while (!document.equals(".B")) {
	                        docAuthors += document + "\n";
	                        document = bReader.readLine();
	                    }
	                    break;
	                case ".B":
	                    document = bReader.readLine();
	                    while (!document.equals(".W")) {
	                        docBib += document + "\n";
	                        document = bReader.readLine();
	                    }
	                    break;
	                case ".W":
	                    document = bReader.readLine();
	                    while (document != null) {
	                        docWords += document + "\n";
	                        document = bReader.readLine();
	                    }
	                    break;	          
	                default:
	                    break;
	            }
	        }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        try {
				fReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
        String curDocId = Integer.toString(id);
        DocumentObject singleDoc = new DocumentObject(curDocId, docTitle, docAuthors, docBib, docWords);
        return singleDoc;
    }

    public static QueryObject getQueryById(int id) {
        FileReader fReader = null;
        BufferedReader bReader = null;
        String question = "";
		try {
			fReader = new FileReader(new File(individualQueryPath + FileUtils.getIndividualQueryFilename() + id));
			bReader = new BufferedReader(fReader);
			String q = bReader.readLine();
			//System.out.println("query id: " + q);
	        q = bReader.readLine();
	        //System.out.println(" W: " + q);
	        q = bReader.readLine();
	        //System.out.println("question: " + q);
	        while (q != null) {
	            question += q + "\n";
	            q = bReader.readLine();
	        }
		} catch ( IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        try {
				fReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		
        String currentId = Integer.toString(id);
        QueryObject queryObj = new QueryObject(currentId, question);
        return queryObj;
    }
}
