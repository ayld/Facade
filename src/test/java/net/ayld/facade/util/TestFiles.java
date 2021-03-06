package net.ayld.facade.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:META-INF/test-contexts/testFilesContext.xml"})
public class TestFiles {
	
	private static final String JAVA_CLASS_FILE_EXTENSION = "class";
	private static final String SUB_DIR_TEST_NAME = "sub";

	@Autowired
	private String filesWorkDir;
	
	@Before
	public void prepare() throws URISyntaxException, IOException {
		final File work = new File(filesWorkDir);
		delete(work);
		
		if (!work.mkdirs()) { // recreate work dir
			throw new IOException("unable to create directory: " + work);
		}

        final Joiner pathJoiner = Joiner.on(File.separator);

        final File subWork = new File(pathJoiner.join(work.getAbsolutePath(), SUB_DIR_TEST_NAME));
		if (!subWork.mkdirs()) {
			throw new IOException("unable to create directory: " + subWork);
		}
		
		// create a 'class' named dir in an attempt to confuse the util into thinking its a file
		final File classDir = new File(pathJoiner.join(subWork.getAbsolutePath(), "class"));
		if (!classDir.mkdirs()) {
			throw new IOException("unable to create directory: " + classDir);
		}
		
		// create a file named 'class' with no extension in the workDir
		final File fakeClass = new File(pathJoiner.join(classDir.getAbsolutePath(), "class"));
		if (!fakeClass.createNewFile()) {
			throw new IOException("unable to create file: " + fakeClass);
		}
		
		final Set<File> testFiles = ImmutableSet.of(
				new File(Resources.getResource("test-classes/ClassName.class").toURI()),
				new File(Resources.getResource("test-classes/CoreRenderer.class").toURI()),
				new File(Resources.getResource("test-classes/PrimePartialViewContext.class").toURI()),
				new File(Resources.getResource("test-classes/ValidCoffee.java").toURI())
		);
		
		for (File testFile : testFiles) {
			com.google.common.io.Files.copy(testFile, new File(pathJoiner.join(filesWorkDir, testFile.getName())));
			com.google.common.io.Files.copy(testFile, new File(pathJoiner.join(subWork.getAbsolutePath(), testFile.getName())));
		}
	}
	
	@Test
	public void withExtensionNonRecursive() throws IOException {
		final List<File> found = Files.in(filesWorkDir).nonRecursive().withExtension(JAVA_CLASS_FILE_EXTENSION).list();
		
		assertTrue(found != null && !found.isEmpty());
		assertThatFoundFilesHaveExtensions(found);
		assertTrue(found.size() == 3);
		assertThatFoundAreFiles(found);
	}
	
	@Test
	public void withExtensionRecursive() throws IOException {
		final List<File> found = Files.in(filesWorkDir).withExtension(JAVA_CLASS_FILE_EXTENSION).list();
		
		assertTrue(found != null && !found.isEmpty());
		assertThatFoundFilesHaveExtensions(found);
		assertTrue(found.size() == 6);
		assertThatFoundAreFiles(found);
	}
	
	@Test
	public void inclusiveRecursive() throws IOException {
		final List<File> found = Files.in(filesWorkDir).withExtension(JAVA_CLASS_FILE_EXTENSION).named("ValidCoffee").inclusive();
		
		assertTrue(found != null && !found.isEmpty());
		assertThatFoundFilesHaveExtensions(found);
		assertTrue(found.size() == 8);
		assertThatFoundAreFiles(found);
	}
	
	@Test
	public void inclusiveNonRecursive() throws IOException {
		final List<File> found = Files.in(filesWorkDir).nonRecursive().withExtension(JAVA_CLASS_FILE_EXTENSION).named("ValidCoffee").inclusive();

		assertTrue(found != null && !found.isEmpty());
		assertThatFoundFilesHaveExtensions(found);
		assertTrue(found.size() == 4);
		assertThatFoundAreFiles(found);
	}
	
	@Test
	public void exclusiveRecursive() throws IOException {
		final List<File> found = Files.in(filesWorkDir).withExtension(JAVA_CLASS_FILE_EXTENSION).named("ValidCoffee").exclusive();
		
		assertTrue(found != null && !found.isEmpty());
		assertThatFoundFilesHaveExtensions(found);
		assertTrue(found.size() == 2);
		assertThatFoundAreFiles(found);
	}
	
	@Test
	public void exclusiveNonRecursive() throws IOException {
		final List<File> found = Files.in(filesWorkDir).nonRecursive().withExtension(JAVA_CLASS_FILE_EXTENSION).named("ValidCoffee").exclusive();
		
		assertTrue(found != null && !found.isEmpty());
		assertThatFoundFilesHaveExtensions(found);
		assertTrue(found.size() == 1);
		assertThatFoundAreFiles(found);
	}
	
	@Test
	public void all() throws IOException {
		final List<File> found = Files.in(filesWorkDir).all();
		
		assertTrue(found != null && !found.isEmpty());
		assertTrue(found.size() == 9);
		assertThatFoundAreFiles(found);
	}
	
	@Test
	public void allNonRecursive() throws IOException {
		final List<File> found = Files.in(filesWorkDir).nonRecursive().all();
		
		assertTrue(found != null && !found.isEmpty());
		assertThatFoundFilesHaveExtensions(found);
		assertTrue(found.size() == 4);
		assertThatFoundAreFiles(found);
	}
	
	@Test
	public void single() throws IOException {
		final File found = Files.in(filesWorkDir).nonRecursive().withExtension(JAVA_CLASS_FILE_EXTENSION).named("ValidCoffee").single();
		
		assertTrue(found != null);
		assertThatFoundAreFiles(Lists.newArrayList(found));
	}

    @Test
    public void delete() throws IOException {
        final String tmpDir = String.valueOf(File.createTempFile("probe", "tmp").getParent());

        final Joiner pathJoiner = Joiner.on(File.separator);

        final String testDir = pathJoiner.join(tmpDir, "testDelete");

        new File(pathJoiner.join(testDir, "dir1", "subDir")).mkdirs();

        Assert.assertTrue(Directories.in(testDir).recursive().list().size() == 2);

        Files.deleteRecursive(new File(testDir));

        try {
            Directories.in(testDir);
        } catch (IllegalArgumentException e) {
            // success
            return;
        }
        Assert.fail();
    }

	private void assertThatFoundFilesHaveExtensions(List<File> found) {
		for (File f : found) {
			if (Tokenizer.delimiter(".").tokenize(f.getAbsolutePath()).tokens().size() != 2) {
				Assert.fail("found file: " + f.getAbsolutePath() + ", doesn't have an extension");
			}
		}
	}
	
	private void assertThatFoundAreFiles(List<File> found) {
		for (File f : found) {
			if (!f.isFile()) {
				Assert.fail("found 'file': " + f.getAbsolutePath() + ", is not a file");
			}
		}
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
			throw new IOException("failed to deleteRecursive: " + file);
		}
	}
}
