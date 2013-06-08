package net.ayld.facade.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:META-INF/test-contexts/testFilesContext.xml"})
public class TestFiles {
	
	private static final String JAVA_CLASS_FILE_EXTENSION = "class";
	private static final String SUB_DIR_TEST_NAME = "sub";

	@Autowired
	private String workDir;
	
	@Before
	public void prepare() throws URISyntaxException, IOException {
		final File work = new File(workDir);
		delete(work);
		
		final Set<File> testFiles = ImmutableSet.of(
				new File(Resources.getResource("test-classes/ClassName.class").toURI()),
				new File(Resources.getResource("test-classes/CoreRenderer.class").toURI()),
				new File(Resources.getResource("test-classes/PrimePartialViewContext.class").toURI()),
				new File(Resources.getResource("test-classes/ValidCoffee.java").toURI())
		);
		
		if (!work.mkdirs()) { // recreate work dir
			throw new IOException("unnable to create directory: " + work);
		} 
		
		final File subWork = new File(Joiner.on(File.separator).join(work.getAbsolutePath(), SUB_DIR_TEST_NAME));
		if (!subWork.mkdirs()) {
			throw new IOException("unnable to create directory: " + work);
		}
		
		for (File testFile : testFiles) {
			com.google.common.io.Files.copy(testFile, new File(Joiner.on(File.separator).join(workDir, testFile.getName())));
			com.google.common.io.Files.copy(testFile, new File(Joiner.on(File.separator).join(subWork.getAbsolutePath(), testFile.getName())));
		}
	}
	
	@Test
	public void withExtensionNonRecursive() throws IOException {
		final List<File> found = Files.in(workDir).nonRecursive().withExtension(JAVA_CLASS_FILE_EXTENSION).list();
		
		System.out.println(found);
		
		assertTrue(found != null && !found.isEmpty());
		assertTrue(found.size() == 3);
	}
	
	@Test
	public void withExtensionRecursive() throws IOException {
		final List<File> found = Files.in(workDir).withExtension(JAVA_CLASS_FILE_EXTENSION).list();
		
		assertTrue(found != null && !found.isEmpty());
		assertTrue(found.size() == 6);
	}
	
	@Test
	public void inclusiveRecursive() throws IOException {
		final List<File> found = Files.in(workDir).withExtension(JAVA_CLASS_FILE_EXTENSION).named("ValidCoffee").inclusive();
		
		assertTrue(found != null && !found.isEmpty());
		assertTrue(found.size() == 8);
	}
	
	@Test
	public void inclusiveNonRecursive() throws IOException {
		final List<File> found = Files.in(workDir).nonRecursive().withExtension(JAVA_CLASS_FILE_EXTENSION).named("ValidCoffee").inclusive();

		assertTrue(found != null && !found.isEmpty());
		assertTrue(found.size() == 4);
	}
	
	@Test
	public void exclusiveRecursive() throws IOException {
		final List<File> found = Files.in(workDir).withExtension(JAVA_CLASS_FILE_EXTENSION).named("ValidCoffee").exclusive();
		
		assertTrue(found != null && !found.isEmpty());
		assertTrue(found.size() == 2);
	}
	
	@Test
	public void exclusiveNonRecursive() throws IOException {
		final List<File> found = Files.in(workDir).nonRecursive().withExtension(JAVA_CLASS_FILE_EXTENSION).named("ValidCoffee").exclusive();
		
		assertTrue(found != null && !found.isEmpty());
		assertTrue(found.size() == 1);
	}
	
	private static void delete(File file) throws IOException {
		if (file == null || !file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			for (File sub : file.listFiles()) {
				delete(sub);
			}
		}
		if (!file.delete()) {
			throw new IOException("failed to delete: " + file);
		}
	}
}
