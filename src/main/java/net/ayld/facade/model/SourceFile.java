package net.ayld.facade.model;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import net.ayld.facade.dependency.resolver.DependencyResolver;
import net.ayld.facade.util.Components;
import net.ayld.facade.util.Tokenizer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

/** 
 * Represents a Java source file.
 * 
 * Upon creation this class parses the Java source it is being created from and validates it's correctness.
 * Throws {@link IllegalArgumentException} if the source file fails validation.
 * */
public class SourceFile {
	
	public static final String EXTENTION = "java";
	public static final String PACKAGE_KEYWORD = "package";
	public static final String IMPORT_KEYWOD = "import";
	public static final String WILDCARD_IMPORT_SUFFIX = "*";
	public static final String CLASS_KEYWORD = "class";
	public static final String PUBLIC_KEYWORD = "public";
	
	private static Set<String> VALID_SOURCE_FILE_FIRST_WORDS = ImmutableSet.of(IMPORT_KEYWOD, PACKAGE_KEYWORD);
	
	private Set<ClassName> dependencies;
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
	
	/** 
	 * Creates a {@link SourceFile} from the given absolute file path.
	 * The path should point to a valid .java source file.
	 * The given file will be validated on creation and a {@link IllegalArgumentException} will be thrown if the validation fails.
	 * 
	 * @param absPath absolute path to a .java source file
	 * 
	 * @return a {@link SourceFile}
	 * */
	public static SourceFile fromFilepath(String absPath) {
		return new SourceFile(new File(absPath));
	}
	
	/** 
	 * Creates a {@link SourceFile} from the given relative path. The given path should be part of your classpath
	 * and point to a valid .java source file. The given file will be validated on creation and 
	 * a {@link IllegalArgumentException} will be thrown if the validation fails.
	 * 
	 * @param path relative path to a .java source file on the classpath
	 * 
	 * @return a {@link SourceFile} 
	 * */
	public static SourceFile fromClasspath(String path) {
		try {
			
			return new SourceFile(new File(Resources.getResource(path).toURI()));
			
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("can not find a file at: " + path);
		}
	}
	
	/** 
	 * Creates a {@link SourceFile} from the given {@link File}.
	 * The given file should be a valid .java source file, otherwise an {@link IllegalArgumentException} will be thrown.
	 * 
	 * @param file a .java source file
	 * 
	 * @return a {@link SourceFile}
	 * */
	public static SourceFile fromFile(File file) {
		if (file == null) {
			throw new IllegalArgumentException("can't create a SourceFile from a null file");
		}
		return fromFilepath(file.getAbsolutePath());
	}
	
	/** 
	 * Returns the dependencies of this {@link SourceFile}
	 * */
	public Set<ClassName> dependencies() {
		final DependencyResolver<SourceFile> sourceDependencyResolver = Components.SOURCE_DEPENDENCY_RESOLVER.getInstance();
		if (dependencies == null) {
			try {
				
				dependencies = sourceDependencyResolver.resolve(this);
				
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
		return ImmutableSet.copyOf(dependencies);
	}
	
	public File physicalFile() {
		return new File(source.getAbsolutePath());
	}
	
	private static boolean isSourceFile(File sourceFile) throws IOException { // TODO moar checks needed this is not enough
		if (sourceFile == null) {
			return false;
		}
		
		final String name = sourceFile.getName();
		final String extension = Tokenizer.delimiter(".").tokenize(name).lastToken();
		
		if (!extension.equals(EXTENTION)) {
			return false;
		}
		
		final String sourceFileContent = Resources.toString(sourceFile.toURI().toURL(), Charsets.UTF_8);
		
		final String firstLine = Tokenizer.delimiter("\n").tokenize(sourceFileContent).firstToken();
		final String firstWord = Tokenizer.delimiter(" ").tokenize(firstLine).firstToken();

        return VALID_SOURCE_FILE_FIRST_WORDS.contains(firstWord);
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
