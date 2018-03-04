package com.kesavana.ir.cransearch.model;

/**
 * 
 * @author arun
 *
 */
public class QueryObject {

	private String qId;
    private String qWords;
    
    public QueryObject() {
    	
    }
    
    public QueryObject(String qId, String qWords) {
    		this.setqId(qId);
    		this.setqWords(qWords);
    }

	public String getqId() {
		return qId;
	}

	public void setqId(String qId) {
		this.qId = qId;
	}

	public String getqWords() {
		return qWords;
	}

	public void setqWords(String qWords) {
		this.qWords = qWords;
	}
}
