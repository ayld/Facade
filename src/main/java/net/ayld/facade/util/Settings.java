package net.ayld.facade.util;

import org.springframework.context.ApplicationContext;

public enum Settings {
	DEFAULT_OUT_DIR("workDir"),
	DEFAULT_FACADE_JAR_NAME("defaultOutJarName");
	
	private final ApplicationContext context;
	private final String name;
	
	private Settings(String name) {
		this.context = Contexts.SPRING.instance();
		this.name = name;
	}
	
	public String getValue() {
		return String.valueOf(context.getBean(name));
	}
}
