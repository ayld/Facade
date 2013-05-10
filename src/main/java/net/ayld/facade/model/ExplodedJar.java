package net.ayld.facade.model;

import java.util.jar.JarFile;

public class ExplodedJar {
	
	final String extractedPath;
	final JarFile archive;

	public ExplodedJar(String extractedPath, JarFile archive) {
		// TODO check whether path string is a path
		this.extractedPath = extractedPath;
		this.archive = archive;
	}

	public JarFile getArchive() {
		return archive;
	}

	public String getExtractedPath() {
		return extractedPath;
	}
}
