package net.ayld.facade.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

/** 
 * Meant to represent a compiled binary .class file on the file system.
 * 
 * This class tries to do everything it can to make sure it wraps a file that is actually a Java .class file.
 * It does this by reading the .class file bytes and checking whether they match the JVM .class file specifications.
 * 
 * More info here:
 *   http://en.wikipedia.org/wiki/Java_class_file
 * */
public class ClassFile { // XXX magic numbers
	
	public static final String EXTENSION = "class";
	
	// bytes in a set :D
	private static final Set<Byte> CAFEBABE = ImmutableSet.of(
			(byte) 0xCA,
			(byte) 0xFE,
			(byte) 0xBA,
			(byte) 0xBE
	);
	
	private final File classFile;
	private final byte[] binary;
	private final ClassName qualifiedName;

	private ClassFile(File classfile) { // XXX copy code
		try {
			if (!isClassfile(classfile)) {
				throw new IllegalArgumentException("file: " + classfile.getAbsolutePath() + ", not valid or is not a class file");
			}
			
			this.classFile = classfile;
			this.binary = Files.readAllBytes(classFile.toPath());
				
		} catch (URISyntaxException | IOException e) {
			throw new IllegalArgumentException("file: " + classfile.getAbsolutePath() + ", not valid or is not a class file", e);
		}
		
		this.qualifiedName = new ClassName(new ClassLoader() {
			
			public Class<?> defineClass(byte[] binary) {
				return defineClass(null, binary, 0, binary.length);
			}
			
		}.defineClass(binary).getName());
	}
	
	
	/** 
	 * Returns the qualified class name of this ClassFile.
	 * 
	 * @see {@link ClassName}
	 * */
	public ClassName qualifiedName() {
		return qualifiedName;
	}
	
	/** 
	 * Creates a {@link ClassFile} from a file on the classpath rather than the file system.
	 * Checks whether the given file is actually a class file.
	 * 
	 * @param classpath path to the .class resource
	 * 
	 * @return a new {@link ClassFile}
	 * 
	 * @throws IllegalArgumentException if the file is not found or the file is not a class file
	 * */
	public static ClassFile fromClasspath(String path) {
		try {
			
			return new ClassFile(new File(Resources.getResource(path).toURI()));
			
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("can not find a file at: " + path);
		}
	}
	
	/** 
	 * Creates a {@link ClassFile} from a {@link File}, checking whether the given file is actually a class file.
	 * 
	 * @param path path to a .class file on the file system
	 * 
	 * @return a new {@link ClassFile}
	 * 
	 * @throws IllegalArgumentException if the file is not found or the file is not a class file
	 * */
	public static ClassFile fromFilepath(String path) {
		return new ClassFile(new File(path));
	}
	
	public static ClassFile fromFile(File classFile) {
		if (classFile == null) {
			throw new IllegalArgumentException("null argument not allowed");
		}
		return fromFilepath(classFile.getAbsolutePath());
	}
	
	/** 
	 * Checks whether a file is a class file.
	 * 
	 * @param classfile a file on the file system
	 * 
	 * @return true if the given file is a Java class file,
	 *         false if not
	 * */
	public static boolean isClassfile(File classfile) throws URISyntaxException, IOException { // moar checks can be done ... not that they would matter ...
		if (!classfile.exists()) {
			return false;
		}
		
		if (!EXTENSION.equals(com.google.common.io.Files.getFileExtension(classfile.getName()))) {
			return false;
		}
		
		InputStream classfileInputStream = null;
		final byte[] firstFour = new byte[4];
		
		try {
			classfileInputStream = new FileInputStream(classfile);
			
			if (classfileInputStream.read(firstFour, 0, firstFour.length) != firstFour.length) {
				return false;
			}
			
		} finally {
			if (classfileInputStream != null) {
				classfileInputStream.close();
			}
		}

		// XXX manual set array wrap because:
		// ImmutableSet.of(firstFour) gives me ImmutableSet<byte[]> while I want ImmutableSet<Byte> ?!?!
		final Set<Byte> firstFourSet = Sets.newHashSet();
		for (byte b : firstFour) {
			firstFourSet.add(b);
		}
		
		if (!CAFEBABE.equals(ImmutableSet.copyOf(firstFourSet))) {
			return false;
		}
		
		return true;
	}

	/** 
	 * Returns the wrapped class file as a {@link File}.
	 * 
	 * @return the wrapped class file as a {@link File}.
	 * */
	public File physicalFile() {
		return new File(classFile.getAbsolutePath());
	}

	/** 
	 * Returns the path to the wrapped {@link File}.
	 * */
	@Override
	public String toString() {
		return classFile.getAbsolutePath();
	}

	// TODO hashCode() and equals() must be improved reflecting new fields
	
	@Override
	public int hashCode() {
		return classFile.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof ClassFile)) {
			return false;
		}
		
		final ClassFile other = (ClassFile) obj;
		
		return other.physicalFile().equals(this.physicalFile());
	}
}
