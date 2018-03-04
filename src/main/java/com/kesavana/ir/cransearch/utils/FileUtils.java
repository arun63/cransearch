package com.kesavana.ir.cransearch.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 
 * @author arun
 *
 */
public class FileUtils {
	
	private static final String CURRENT_USER_DIRECTORY = System.getProperty("user.dir");
    private static final String ALL_DOCUMENTS_RECORDS = CURRENT_USER_DIRECTORY + "/cran/cran.all.1400";
    private static final String ALL_QUERIES_RECORDS = CURRENT_USER_DIRECTORY + "/cran/cran.qry";
    private static final String INDIVIDUAL_DOCS_PATH = CURRENT_USER_DIRECTORY + "/data/alldocs";
    private static final String DOC_INDEX_PATH = CURRENT_USER_DIRECTORY + "/data/index";
    private static final String INDIVIDUAL_QUERIES_PATH = CURRENT_USER_DIRECTORY + "/data/allqueries";
    private static final String RESULTANT_PATH = CURRENT_USER_DIRECTORY + "/test/results";
    private static final String INDIVIDUAL_DOCUMENT_FILENAME = "/ir.doc.";
    private static final String INDIVIDUAL_QUERY_FILENAME = "/ir.query.";
    private static final String RESULTANT_FILENAME = "/resultant";
    
    public static void createDirectories(String path) {
		Path p = Paths.get(path);
        if (!Files.exists(p)) {
            try {
            	    Files.createDirectories(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
    
    public static void checkFileExists(String fileName) throws IOException {
    		File f = new File(fileName);
        if(!f.exists() && !f.isDirectory()) { 
        		Files.createFile(Paths.get(fileName));
        }
    }
    
    public static void createAllReqDir() {
		createDirectories(FileUtils.getIndividualDocsPath());
		createDirectories(FileUtils.getIndividualQueriesPath());
		createDirectories(FileUtils.getDocIndexPath());
		createDirectories(FileUtils.getResultantPath());
	}
    
	public static String getAllDocumentsRecords() {
		return ALL_DOCUMENTS_RECORDS;
	}
	public static String getAllQueriesRecords() {
		return ALL_QUERIES_RECORDS;
	}
	public static String getIndividualDocsPath() {
		return INDIVIDUAL_DOCS_PATH;
	}
	public static String getIndividualQueriesPath() {
		return INDIVIDUAL_QUERIES_PATH;
	}
	public static String getResultantPath() {
		return RESULTANT_PATH;
	}
	public static String getDocIndexPath() {
		return DOC_INDEX_PATH;
	}
	public static String getIndividualDocumentFilename() {
		return INDIVIDUAL_DOCUMENT_FILENAME;
	}
	public static String getIndividualQueryFilename() {
		return INDIVIDUAL_QUERY_FILENAME;
	}
	public static String getResultantFilename() {
		return RESULTANT_FILENAME;
	}
}
