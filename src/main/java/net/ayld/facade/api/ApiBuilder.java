package net.ayld.facade.api;

import java.io.File;
import java.util.List;

import net.ayld.facade.bundle.impl.ManualJarMaker;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;

public final class ApiBuilder { // XXX magic strings
	
	private final ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/context.xml");

	private File name;
	private final List<Object> componentListeners = Lists.newLinkedList();
	
	private ApiBuilder() {}

	public ApiBuilder(File name) {
		this.name = name;
	}

	public static ApiBuilder outputJar(File name) {
		if (name == null) {
			throw new IllegalArgumentException("given name is null");
		}
		if (name.isDirectory()) {
			throw new IllegalArgumentException("given name is a directory");
		}
		
		return new ApiBuilder(name);
	}
	
	public ApiBuilder addListener(Object listener) {
		componentListeners.add(listener);
		return this;
	} 
	
	public static Facade buildWithDefaultConfig() {
		return new ApiBuilder().build();
	}
	
	public Facade build() {
		if (name != null) {
			
			final ManualJarMaker jarMakerBean = (ManualJarMaker) context.getBean("jarMaker");
			
			jarMakerBean.setZippedJarName(name.getAbsolutePath());
		}
		if (componentListeners.size() > 1) {
			
			final EventBus eventBus = (EventBus) context.getBean("resolverStatusUpdateEventBus");
			
			for (Object listener : componentListeners) {
				eventBus.register(listener);
			}
		}
		return (Facade) context.getBean("facadeApi");
	}
}
