package net.ayld.facade.resolver.impl;

import java.io.IOException;
import java.util.Set;

import junit.framework.Assert;
import net.ayld.facade.dependency.resolver.ClassDependencyResolver;
import net.ayld.facade.model.ClassFile;
import net.ayld.facade.model.ClassName;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:META-INF/test-contexts/testManualBinaryParseClassDependencyResolver.xml"})
public class TestManualBinaryParseClassDependencyResolver {

	@Autowired
	private ClassDependencyResolver classDependencyResolver;
	
	@Test
	public void resolve() throws IOException {
		final Set<ClassName> resolved = classDependencyResolver.resolve(ClassFile.fromClasspath("test-classes/primefaces-3.5.jar/org/primefaces/model/TreeTableModel.class"));
		
		Assert.assertTrue(resolved != null);
		Assert.assertTrue(!resolved.isEmpty());
		Assert.assertTrue(resolved.size() == 10);
	}
}
