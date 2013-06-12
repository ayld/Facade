package net.ayld.facade.model;

import java.io.File;
import java.util.jar.JarFile;

/** 
 * Represents an extracted (exploded) .jar file.
 * */
public class ExplodedJar {
	
	final String extractedPath;
	final JarFile archive;

	public ExplodedJar(String extractedPath, JarFile archive) {
		if (!isPath(extractedPath)) {
			throw new IllegalArgumentException("Directory at: " + extractedPath + ", does not exist or is not a directory");
		}
		this.extractedPath = extractedPath;
		this.archive = archive;
	}

	private static boolean isPath(String toCheck) {
		final File result = new File(toCheck);
		
		return result.exists() && result.isDirectory();
	}
	
	public JarFile getArchive() {
		return archive;
	}

	public String getExtractedPath() {
		return extractedPath;
	}
}
