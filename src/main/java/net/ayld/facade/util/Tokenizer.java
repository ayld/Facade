package net.ayld.facade.util;

import java.util.Collections;
import java.util.List;

import net.ayld.facade.util.annotation.NotThreadSafe;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

@NotThreadSafe // or is it ?!?
public final class Tokenizer {
	
	private String delimiter;
	private List<String> tokens = Collections.emptyList();
	
	private Tokenizer(String delimiter) {
		this.delimiter = delimiter;
	}
	
	public static Tokenizer delimiter(String delimiter) {
		return new Tokenizer(delimiter);
	}

	public Tokenizer tokenize(String string) {
		tokens = Lists.newArrayList(Splitter.on(delimiter).split(string));
		return this;
	}
	
	public List<String> tokensIn(Range<Integer> range) {
		if (!(range.hasUpperBound() && range.hasLowerBound())) {
			throw new IllegalArgumentException("boundless ranges are not currently supported");
		}
		
		if (tokens.isEmpty()) {
			return tokens;
		}
		
		final Integer lower = range.lowerEndpoint();
		final Integer upper = range.upperEndpoint();
		
		if (lower < 0) {
			throw new IndexOutOfBoundsException("lower bound: " + lower + ", is negative");
		}
		
		if (upper > tokens.size()) {
			throw new IndexOutOfBoundsException("upper bound: " + upper + ", is over the tokens size: " + tokens.size());
		}
		
		return ImmutableList.copyOf(tokens.subList(lower, upper));
	}
	
	public List<String> tokens() {
		return ImmutableList.copyOf(tokens);
	}
	
	public String firstToken() {
		return tokenByIndex(0);
	}
	
	public String lastToken() {
		return tokenByIndex(tokens.size() - 1);
	}
	
	public String tokenByIndex(int index) {
		if (index < 0 || index >= tokens.size()) {
			return "";
		}
		
		return tokens.get(index);
	}
}
