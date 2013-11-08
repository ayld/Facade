package net.ayld.facade.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import com.google.common.base.Strings;

public enum Contexts {
	SPRING(new ClassPathXmlApplicationContext("META-INF/context.xml"));
	
	private static final String SPRING_PROFILES_ENV_VAR_NAME = "spring.profiles.active";
	
	private final ApplicationContext context;
	
	private Contexts(ApplicationContext context) {
		this.context = context;
		
		final String activeProfiles = System.getProperty(SPRING_PROFILES_ENV_VAR_NAME);
		
		final boolean concurrencyEnabled;
		if (!Strings.isNullOrEmpty(activeProfiles)) {
			concurrencyEnabled = activeProfiles.contains(Settings.CONCURRENT_PROFILE_NAME.getValue());
		}
		else {
			concurrencyEnabled = Boolean.valueOf(Settings.CONCURRENCY_ENABLED.getValue());
		}
		
		if (concurrencyEnabled) {
			final ConfigurableEnvironment env = (ConfigurableEnvironment) this.context.getEnvironment();
			env.setActiveProfiles(Settings.CONCURRENT_PROFILE_NAME.getValue());
		}
	}

	public ApplicationContext instance() {
		return context;
	} 
}
