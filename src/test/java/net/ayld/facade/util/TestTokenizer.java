package net.ayld.facade.util;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

public class TestTokenizer {
	
	@Test
	public void tokenize() {
		final List<String> tokens = Tokenizer.delimiter(" ").tokenize("why did the chicken cross the road ?").tokens();
		
		assertNotNull(tokens);
		assertTrue(tokens.size() == 8);
		assertTrue(tokens.contains("why"));
		assertTrue(tokens.contains("did"));
		assertTrue(tokens.contains("the"));
		assertTrue(tokens.contains("chicken"));
		assertTrue(tokens.contains("cross"));
		assertTrue(tokens.contains("the"));
		assertTrue(tokens.contains("road"));
		assertTrue(tokens.contains("?"));
	}
	
	@Test
	public void makeSureTokenizeDoesNotUseRegularExpressions() {
		
		final String test = "regexp.sux.donkey.balls";
		
		final List<String> regexSplit = ImmutableList.copyOf(Splitter.onPattern(".").split(test));
		final List<String> nonRegexSplit = Tokenizer.delimiter(".").tokenize(test).tokens();
		
		assertFalse(regexSplit.equals(nonRegexSplit));
	}
	
	@Test
	public void lastToken() {
		
		final String last = "one";
		final String lastToken = Tokenizer.delimiter(" ").tokenize("the source is strong in this " + last).lastToken();
		
		assertEquals(last, lastToken);
	}
	
	@Test
	public void firstToken() {
		
		final String first = "the";
		final String firstToken = Tokenizer.delimiter(" ").tokenize(first + " source is strong in this one").firstToken();
		
		assertEquals(first, firstToken);
	}
}
