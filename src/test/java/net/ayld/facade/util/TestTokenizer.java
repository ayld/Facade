package net.ayld.facade.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

public class TestTokenizer {
	
	@Test
	public void tokenize() {
		final String test = "why did the chicken cross the road ?";
		
		final List<String> tokens = Tokenizer.delimiter(" ").tokenize(test).tokens();
		assertNotNull(tokens);
		
		final List<String> splitTest = Lists.newArrayList(Splitter.on(" ").split(test));
		assertTrue(tokens.size() == splitTest.size());
		
		for (String part : splitTest) {
			assertTrue(tokens.contains(part));
		}
	}
	
	@Test
	public void makeSureTokenizeDoesNotUseRegularExpressions() {
		
		final String test = "regexp.sux.donkey.balls";
		
		final List<String> regexSplit = ImmutableList.copyOf(Splitter.onPattern(".").split(test));
		final List<String> nonRegexSplit = Tokenizer.delimiter(".").tokenize(test).tokens();
		
		assertFalse(regexSplit.equals(nonRegexSplit));
	}
	
	@Test
	public void range() {
		
		final String test = "somewhere.over.dat.rainbow";
		
		final List<String> tokens = Tokenizer.delimiter(".").tokenize(test).tokens();
		for (int i = 0; i < tokens.size(); i++) {
			assertTrue(tokens.subList(0, i).equals(Tokenizer.delimiter(".").tokenize(test).tokensIn(Range.closed(0, i))));
		}
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
