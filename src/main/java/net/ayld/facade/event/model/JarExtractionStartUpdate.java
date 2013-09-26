package net.ayld.facade.event.model;

import java.io.File;

public class JarExtractionStartUpdate extends StatusUpdate{

	private final File on;
	private final File to;
	
	public JarExtractionStartUpdate(String message, File on, File to) {
		super(message);
		
		if (on == null || on.isDirectory()) {
			throw new IllegalArgumentException("extraction file: " + on.getAbsolutePath() + " null or is a directory");
		}
		if (to == null || !to.isDirectory()) {
			throw new IllegalArgumentException("extraction target: " + to.getAbsolutePath() + " null or not a directory");
		}
		
		this.on = on;
		this.to = to;
	}

	public File getOn() {
		return on;
	}

	public File getTo() {
		return to;
	}
}
