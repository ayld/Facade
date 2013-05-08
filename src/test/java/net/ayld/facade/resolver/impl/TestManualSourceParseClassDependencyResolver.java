package net.ayld.facade.resolver.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import net.ayld.facade.resolver.ClassDependencyResolver;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.io.Resources;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:META-INF/test-context.xml"})
public class TestManualSourceParseClassDependencyResolver {
	
	@Autowired
	private ClassDependencyResolver classDependencyResolver;
	
	@Test
	public void testResolve() throws IOException, URISyntaxException {
		final URL validSourceUrl = Resources.getResource("test-classes/ValidCoffee.java");
		
		classDependencyResolver.resolve(new File(validSourceUrl.toURI()));
	}
	
	@Test
	public void testValidate() throws URISyntaxException, IOException{
		final URL validSourceUrl = Resources.getResource("test-classes/InvalidCoffee.java");
		
		try {
			classDependencyResolver.resolve(new File(validSourceUrl.toURI())); // should blow up
		} catch (IllegalArgumentException e) {
			
			// ok
			
			return;
		}
		Assert.fail(); // not ok
	}
}
