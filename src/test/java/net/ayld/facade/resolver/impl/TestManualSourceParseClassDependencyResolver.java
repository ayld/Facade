package net.ayld.facade.resolver.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import net.ayld.facade.dependency.resolver.ClassDependencyResolver;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:META-INF/test-contexts/testManualSourceParseClassDependencyResolverContext.xml"})
public class TestManualSourceParseClassDependencyResolver {
	
	private static final String JAVA_IMPORT_KEYWOD = "import";
	
	@Autowired
	private ClassDependencyResolver classDependencyResolver;
	
	@Test
	public void testResolve() throws IOException, URISyntaxException {
		final URL validSourceUrl = Resources.getResource("test-classes/ValidCoffee.java");
		
		// get dependencies in a way different than the resolver
		final String content = Resources.toString(validSourceUrl, Charsets.UTF_8);
		final String[] lines = content.split("\\\n");
		
		final Set<String> dependencies = new HashSet<>();
		for (String line : lines) {
			if (line.startsWith(JAVA_IMPORT_KEYWOD)) {
				
				final String dependency = line.split(" ")[1].replaceAll(";", ""); // not very pretty ...
				dependencies.add(dependency);
			}
		}
		
		// get dependencies through the resolver
		final Set<String> resolvedDependencies = classDependencyResolver.resolve(new File(validSourceUrl.toURI()));
		
		// result sets should match
		Assert.assertEquals(dependencies, resolvedDependencies);
	}
	
	@Test
	public void testValidate() throws URISyntaxException, IOException{
		final URL validSourceUrl = Resources.getResource("test-classes/InvalidCoffee.java");
		
		try {
			classDependencyResolver.resolve(new File(validSourceUrl.toURI())); // should blow up
		} catch (IllegalArgumentException e) {
			
			// party :)
			
			return;
		}
		Assert.fail(); // no party :(
	}
}
