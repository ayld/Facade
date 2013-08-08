package net.ayld.facade.model;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import net.ayld.facade.util.Tokenizer;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;

/** 
 * Represents a Java source file.
 * */
public class SourceFile {
	
	public static final String EXTENTION = "java";
	public static final String PACKAGE_KEYWORD = "package";
	public static final String IMPORT_KEYWOD = "import";
	
	private static Set<String> VALID_SOURCE_FILE_FIRST_WORDS = ImmutableSet.of(IMPORT_KEYWOD, PACKAGE_KEYWORD);
	
	private final File source;
	
	private SourceFile(File sourceFile) {
		try {
			if (!isSourceFile(sourceFile)) {
				throw new IllegalArgumentException(sourceFile + " does not look like Java source");
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		this.source = sourceFile;
	}
	
	public static SourceFile fromFilepath(String absPath) {
		return new SourceFile(new File(absPath));
	}
	
	public static SourceFile fromClasspath(String path) {
		try {
			
			return new SourceFile(new File(Resources.getResource(path).toURI()));
			
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("can not find a file at: " + path);
		}
	}
	
	public static SourceFile fromFile(File file) {
		if (file == null) {
			throw new IllegalArgumentException("can't create a SourceFile from a null file");
		}
		return fromFilepath(file.getAbsolutePath());
	}
	
	public File physicalFile() {
		return new File(source.getAbsolutePath());
	}
	
	private static boolean isSourceFile(File sourceFile) throws IOException { // TODO moar checks needed this is not enough
		if (sourceFile == null) {
			return false;
		}
		
		final String name = sourceFile.getName();
		final String extention = Tokenizer.delimiter(".").tokenize(name).lastToken();
		
		if (!extention.equals(EXTENTION)) {
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

	@Override
	public int hashCode() {
		return source.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof SourceFile)) {
			return false;
		}
		
		final SourceFile other = (SourceFile) obj;
		
		return other.physicalFile().equals(this.source);
	}
}
