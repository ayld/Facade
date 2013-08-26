package net.ayld.facade.api;

import java.io.File;

import net.ayld.facade.bundle.impl.ManualJarMaker;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class ApiBuilder { // XXX magic strings
	
	private final static ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/context.xml");

	private ApiBuilder() {}
	
	public static ApiBuilder outputJar(File name) {
		if (name == null) {
			throw new IllegalArgumentException("given name is null");
		}
		if (name.isDirectory()) {
			throw new IllegalArgumentException("given name is a directory");
		}
		
		((ManualJarMaker) context.getBean("jarMaker")).setZippedJarName(name.getAbsolutePath());
		
		return new ApiBuilder();
	}
	
	public static Facade buildDefault() {
		return (Facade) context.getBean("facadeApi");
	}
	
	public Facade build() {
		return (Facade) context.getBean("facadeApi");
	}
}
