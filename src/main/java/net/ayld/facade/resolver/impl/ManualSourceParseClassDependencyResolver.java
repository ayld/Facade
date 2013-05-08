package net.ayld.facade.resolver.impl;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import net.ayld.facade.resolver.ClassDependencyResolver;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
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
		// TODO Auto-generated method stub
		return null;
	}
	
	private static boolean isSourceFile(File sourceFile) throws IOException {
		if (sourceFile == null) {
			return false;
		}
		
		final String name = sourceFile.getName();
		final String extention = Strings.nullToEmpty(name.split("\\.")[1]);
		
		if (!extention.equals(JAVA_SOURCE_FILE_EXTENTION)) {
			return false;
		}
		
		final String sourceFileContent = Resources.toString(sourceFile.toURI().toURL(), Charsets.UTF_8);
		
		// XXX unsafe call to iterator.next()
		final String firstLine = Splitter.on("\n").split(sourceFileContent).iterator().next();
		final String firstWord = Splitter.on(" ").split(firstLine).iterator().next();
		
		if (!VALID_SOURCE_FILE_FIRST_WORDS.contains(firstWord)) {
			return false;
		}
		
		return true;
	}
}
