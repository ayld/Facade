package net.ayld.facade.test.util;

import org.springframework.beans.factory.FactoryBean;

public class WorkDirPathLocatorBeanFactory implements FactoryBean<String> {

	@Override
	public String getObject() throws Exception {
		return String.valueOf(java.io.File.createTempFile("probe", "tmp").getParent());
	}

	@Override
	public Class<?> getObjectType() {
		return String.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
