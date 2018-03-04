package com.kesavana.ir.cransearch.common;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * 
 * @author arun
 *
 */
public class StopAnalyzerInstance extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer tokenizer = new KeywordTokenizer();
        TokenStream tokenStream = new EdgeNGramTokenFilter(tokenizer, 1, 4);
        tokenStream = new LengthFilter(tokenStream, 3, 7);
        tokenStream = new StopFilter(tokenStream, StandardAnalyzer.ENGLISH_STOP_WORDS_SET);
        tokenStream = new TrimFilter(tokenStream);
        TokenStreamComponents tokenStreamComponents = new TokenStreamComponents(tokenizer, tokenStream);
        return tokenStreamComponents;
	}

}
