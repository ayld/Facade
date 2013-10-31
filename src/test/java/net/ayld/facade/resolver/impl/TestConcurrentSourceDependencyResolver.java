package net.ayld.facade.resolver.impl;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import net.ayld.facade.dependency.resolver.DependencyResolver;
import net.ayld.facade.model.ClassName;
import net.ayld.facade.model.SourceFile;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:META-INF/test-contexts/testConcurrentSourceDependencyResolver.xml"})
public class TestConcurrentSourceDependencyResolver {
	
	private static final String JAVA_IMPORT_KEYWOD = "import";
	
	@Autowired
	private DependencyResolver<SourceFile> sourceDependencyResolver;
	
	@Test
	public void resolve() throws IOException {
		final Set<URL> validSourceUrls = ImmutableSet.of(
				Resources.getResource("test-classes/ValidCoffee.java"),
				Resources.getResource("test-classes/AnotherValidCoffee.java")
		);

		final Set<SourceFile> sources = Sets.newHashSetWithExpectedSize(validSourceUrls.size());
		final Set<String> dependencies = new HashSet<>();
		for (URL validSourceUrl : validSourceUrls) {
			
			// get dependencies in a way different than the resolver
			final String content = Resources.toString(validSourceUrl, Charsets.UTF_8);
			final String[] lines = content.split("\\\n");
			
			for (String line : lines) {
				if (line.startsWith(JAVA_IMPORT_KEYWOD)) {
					
					final String dependency = line.split(" ")[1].replaceAll(";", "").replaceAll("\r", ""); // not very pretty ...
					dependencies.add(dependency);
				}
			}
			sources.add(SourceFile.fromFilepath(validSourceUrl.getFile()));
		}
		
		
		// get dependencies through the resolver
		final Set<ClassName> resolvedDependencies = sourceDependencyResolver.resolve(sources);
		
		// result sets should match
		Assert.assertEquals(dependencies, toStringSet(resolvedDependencies));
	}
	
	private Set<String> toStringSet(Set<ClassName> toConvert) {
		final Set<String> result = Sets.newHashSet();
		
		for (ClassName name : toConvert) {
			result.add(name.toString());
		}
		
		return result;
	}
}
