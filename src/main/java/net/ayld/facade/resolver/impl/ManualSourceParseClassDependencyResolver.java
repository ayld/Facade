package net.ayld.facade.resolver.impl;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import net.ayld.facade.resolver.ClassDependencyResolver;
import net.ayld.facade.util.Tokenizer;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

public class ManualSourceParseClassDependencyResolver implements ClassDependencyResolver{

	private static final String JAVA_SOURCE_FILE_EXTENTION = "java";
	private static final String JAVA_PACKAGE_KEYWORD = "package";
	private static final String JAVA_IMPORT_KEYWOD = "import";
	
	private static Set<String> VALID_SOURCE_FILE_FIRST_WORDS = ImmutableSet.of(JAVA_IMPORT_KEYWOD, JAVA_PACKAGE_KEYWORD);
	
	@Override
	public Set<String> resolve(File sourceFile) throws IOException {
		if (!isSourceFile(sourceFile)) {
			throw new IllegalArgumentException("source file: " + sourceFile + ", is not a Java source file or does not exist");
		}
		
		final String sourceFileContent = Resources.toString(sourceFile.toURI().toURL(), Charsets.UTF_8);
		
		// we can somehow select only lines starting with import so we don't need to iterate over every single line
		final Set<String> result = Sets.newHashSet();
		for (String line : Splitter.on("\n").split(sourceFileContent)) {
			
			if (line.startsWith(JAVA_IMPORT_KEYWOD)) {
				
				final String dependency = Tokenizer.delimiter(" ").tokenize(line).lastToken().replaceAll(";", "");
				result.add(dependency);
			}
		}
		return ImmutableSet.copyOf(result);
	}
	
	private static boolean isSourceFile(File sourceFile) throws IOException {
		if (sourceFile == null) {
			return false;
		}
		
		final String name = sourceFile.getName();
		final String extention = Tokenizer.delimiter(".").tokenize(name).lastToken();
		
		if (!extention.equals(JAVA_SOURCE_FILE_EXTENTION)) {
			return false;
		}
		
		final String sourceFileContent = Resources.toString(sourceFile.toURI().toURL(), Charsets.UTF_8);
		
		final String firstLine = Tokenizer.delimiter("\n").tokenize(sourceFileContent).firstToken();
		final String firstWord = Tokenizer.delimiter(" ").tokenize(firstLine).firstToken();
		
		if (!VALID_SOURCE_FILE_FIRST_WORDS.contains(firstWord)) {
			return false;
		}
		
		return true;
	}
}
