package com.kesavana.ir.cransearch.model;

/**
 * 
 * @author arun
 *
 */
public class DocumentObject {
	
	private String docId;
    private String docTitle;
    private String auth;
    private String bib;
    private String words;

    public DocumentObject() {
    	
    }
    
    public DocumentObject(String docId, String docTitle, String auth, String bib, String words) {
        this.setDocId(docId);
        this.setDocTitle(docTitle);
        this.setAuth(auth);
        this.setBib(bib);
        this.setWords(words);
    }

	public String getDocId() {
		return docId;
	}


	public void setDocId(String docId) {
		this.docId = docId;
	}


	public String getDocTitle() {
		return docTitle;
	}


	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}


	public String getAuth() {
		return auth;
	}


	public void setAuth(String auth) {
		this.auth = auth;
	}


	public String getBib() {
		return bib;
	}


	public void setBib(String bib) {
		this.bib = bib;
	}


	public String getWords() {
		return words;
	}


	public void setWords(String words) {
		this.words = words;
	}

}
