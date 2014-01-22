package net.ayld.facade.util;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Created by ayld on 1/10/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:META-INF/test-contexts/testDirectoriesContext.xml"})
public class TestDirectories {

    private final static Set<String> TEST_DIR_NAMES = ImmutableSet.of(
            "test.jar", "test1.jar", "test2.jar", "testDir", "testDir1" , "test1Dir"
    );

    @Autowired
    private String workDir;

    @PostConstruct
    public void init() {
        workDir = workDir + File.separator + "/testDirs";

        new File(workDir).mkdirs();

        final Joiner joiner = Joiner.on(File.separator);
        for (String testDirName : TEST_DIR_NAMES) {
            new File(joiner.join(workDir, testDirName)).mkdirs();
        }
    }

    @Test
    public void list() throws IOException {
        final Set<File> foundDirs = Directories.in(workDir).list();

        Assert.assertTrue(foundDirs.size() >= TEST_DIR_NAMES.size());

        for (File found : foundDirs) {
            final String dirNameNoPath = Tokenizer.delimiter(File.separator).tokenize(found.getAbsolutePath()).lastToken();

            Assert.assertTrue(found + " not present in test dirs, allowed are: " + TEST_DIR_NAMES, TEST_DIR_NAMES.contains(dirNameNoPath));
        }
    }

    @Test
    public void named() throws IOException {
        final String searchFor = "testDir";
        final Set<File> found = Directories.in(workDir).named(searchFor).list();

        Assert.assertTrue(found != null);
        Assert.assertTrue(found.size() == 1);

        final File foundDir = found.iterator().next();
        Assert.assertTrue(foundDir.isDirectory());
        Assert.assertTrue(foundDir.getAbsolutePath().endsWith(searchFor));
    }

    @Test
    public void endsWith() throws IOException {
        final String searchFor = "Dir";
        final Set<File> found = Directories.in(workDir).nameEndsWith(searchFor).list();

        Assert.assertTrue(found != null);
        Assert.assertTrue(found.size() == 2);

        for (File foundDir : found) {
            Assert.assertTrue(foundDir.isDirectory());
            Assert.assertTrue(foundDir.getAbsolutePath().endsWith(searchFor));
        }
    }

    @Test
    public void recursive() throws IOException {
        makeSubDirs();
        Assert.assertTrue(Directories.in(workDir).recursive().list().size() == TEST_DIR_NAMES.size() * 2);
    }

    @Test
    public void nonRecursive() throws IOException {
        makeSubDirs();
        Assert.assertTrue(Directories.in(workDir).list().size() == TEST_DIR_NAMES.size());
    }

    private void makeSubDirs() {
        final Joiner pathJoiner = Joiner.on(File.separator);
        for (String name : TEST_DIR_NAMES) {
            new File(pathJoiner.join(workDir, name, "subDir")).mkdirs();
        }
    }
}
