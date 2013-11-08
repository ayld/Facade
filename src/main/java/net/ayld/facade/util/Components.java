package net.ayld.facade.util;

import org.springframework.context.ApplicationContext;

public enum Components {
	JAR_MAKER("jarMaker"),
	LIB_JAR_EXPLODER("jarExploder"),
	EXPLICIT_JAR_EXPLODER("explicitJarExploder"),
	CLASS_DEPENDENCY_RESOLVER("classDependencyResolver"),
	SOURCE_DEPENDENCY_RESOLVER("sourceDependencyResolver"),
	DEPENDENCY_MATCHER_STRATEGY("unanimousMatcher"),
	EVENT_BUS("statusUpdateEventBus");
	
	private final ApplicationContext context;
	private final String name;

	private Components(String name) {
		this.context = Contexts.SPRING.instance();
		this.name = name;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getInstance() {
		return (T) context.getBean(name);
	}
}
