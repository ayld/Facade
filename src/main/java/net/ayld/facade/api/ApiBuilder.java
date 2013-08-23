package net.ayld.facade.api;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class ApiBuilder {
	
	private final static ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/context.xml");

	public static Facade build() {
		return (Facade) context.getBean("facadeApi");
	}
}
