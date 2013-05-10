package net.ayld.facade.util;

import java.util.Collections;
import java.util.List;

import net.ayld.facade.util.annotation.NotThreadSafe;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

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
