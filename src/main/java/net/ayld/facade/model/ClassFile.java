package net.ayld.facade.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.common.io.Resources;

public class ClassFile { // XXX magic numbers
	
	private static final String JAVA_CLASSFILE_EXTENTION = "class";
	
	// bytes in a set :D
	private static final Set<Byte> CAFEBABE = ImmutableSet.of(
			(byte) 0xCA,
			(byte) 0xFE,
			(byte) 0xBA,
			(byte) 0xBE
	);
	
	private final File classFile;

	private ClassFile(File classfile) { // XXX copy code
		try {
			if (!isClassfile(classfile)) {
				throw new IllegalArgumentException("file: " + classfile.getAbsolutePath() + ", not valid or file at path is not a class file");
			}
			
			this.classFile = classfile;
			
		} catch (URISyntaxException | IOException e) {
			throw new IllegalArgumentException("file: " + classfile.getAbsolutePath() + ", not valid or file at path is not a class file", e);
		}
	}
	
	public static ClassFile fromClasspath(String path) {
		try {
			
			return new ClassFile(new File(Resources.getResource(path).toURI()));
			
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("can not find a file at: " + path);
		}
	}
	
	public static ClassFile fromFilepath(String path) {
		return new ClassFile(new File(path));
	}
	
	public static boolean isClassfile(File classfile) throws URISyntaxException, IOException { // moar checks can be done ... not that they would matter ...
		if (!classfile.exists()) {
			return false;
		}
		
		if (!JAVA_CLASSFILE_EXTENTION.equals(Files.getFileExtension(classfile.getName()))) {
			return false;
		}
		
		InputStream classfileInputStream = null;
		byte[] firstFour = null;
		
		try {
			classfileInputStream = new FileInputStream(classfile);
			firstFour = new byte[4];
			
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

	public File physicalFile() {
		return new File(classFile.getAbsolutePath());
	}

	@Override
	public String toString() {
		return classFile.getAbsolutePath();
	}
}
