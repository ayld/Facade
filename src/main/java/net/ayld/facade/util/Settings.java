package net.ayld.facade.util;

import java.io.IOException;
import java.util.Properties;

import com.google.common.io.Resources;

public enum Settings {
	DEFAULT_OUT_DIR("jar.compaction.dir"),
	EXPLICIT_OUT_DIR("jar.mandatory.extraction.dir"),
	DEFAULT_FACADE_JAR_NAME("jar.facade.name"),
	CONCURRENCY_ENABLED("cuncurrent.components.enabled"),
	CONCURRENT_PROFILE_NAME("concurrent.beans.profile.name");
	
	private final Properties config;
	private final String name;
	
	private Settings(String name) {
		this.name = name;
		this.config = new Properties();
		
		final String configLocation = "config.properties";
		try {
			
			this.config.load(Resources.getResource(configLocation).openStream());
			
		} catch (IOException e) {
			throw new IllegalStateException("could not load properties file: " + configLocation);
		}
	}
	
	public String getValue() {
		return config.getProperty(name);
	}
}
