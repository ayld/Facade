package net.ayld.facade.bundle.impl;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.ayld.facade.bundle.JarMaker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:META-INF/test-contexts/testManualJarMakerContext.xml"})
public class TestManualJarMaker {
	
	private static final Set<String> CORRECT_ZIPPED_ENTRY_NAMES = ImmutableSet.of(
			"ClassName.class", "PrimePartialViewContext.class"
	);
	
	@Autowired
	private String workDir;
	
	@Autowired
	private JarMaker jarMaker;
	
	@Test
	public void zip() throws URISyntaxException, IOException {
		final Set<File> toZip = ImmutableSet.of(
				new File(Resources.getResource("test-classes/ClassName.class").toURI()),
				new File(Resources.getResource("test-classes/PrimePartialViewContext.class").toURI())
		);
		
		final JarFile jar = jarMaker.zip(toZip);
		
		assertTrue(jar != null);
		
		final Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			
			final JarEntry entry = entries.nextElement();
			
			assertTrue(entry != null);
			assertFalse(entry.isDirectory());
			assertTrue(CORRECT_ZIPPED_ENTRY_NAMES.contains(entry.getName()));
		}
		
		final File zipDir = new File(workDir);
		final Set<File> zipDirFiles = Sets.newHashSet(zipDir.listFiles());

		assertTrue(zipDirFiles.contains(new File(jar.getName())));
	}
}
