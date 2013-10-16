package net.ayld.facade.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public enum Contexts {
	SPRING(new ClassPathXmlApplicationContext("META-INF/context.xml"));
	
	private final ApplicationContext context;
	
	private Contexts(ApplicationContext context) {
		this.context = context;
	}

	public ApplicationContext instance() {
		return context;
	} 
}
