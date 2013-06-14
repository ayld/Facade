package net.ayld.facade.ui.console.command.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import net.ayld.facade.bundle.JarExploder;
import net.ayld.facade.ui.console.command.Command;
import net.ayld.facade.util.Files;
import net.ayld.facade.util.Tokenizer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Joiner;
import com.google.common.io.Resources;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:META-INF/test-contexts/testOptimizeContext.xml"})
public class TestOptimize {

	@Autowired
	private String workDir;
	
	@Autowired
	private JarExploder jarExploder;
	
	@Autowired
	private Command optimize;
	
	private File srcDir;
	private File libDir;
	
	@Before
	public void init() throws URISyntaxException, IOException {
		final File srcDir = new File(Resources.getResource("test-classes/test-src-dir").toURI());
		String newFilepath = Joiner.on(File.separator).join(workDir, srcDir.getName());
		
		this.srcDir = new File(newFilepath);
		delete(this.srcDir);
		
		final List<File> sources = Files.in(srcDir.getAbsolutePath()).all();
		for (File source : sources) {
			final String relativeSourcePath = Tokenizer.delimiter("test-src-dir/").tokenize(source.getPath()).lastToken();
			final File newSource = new File(Joiner.on(File.separator).join(this.srcDir, relativeSourcePath));

			if (!new File(newSource.getParent()).mkdirs()) {
				throw new IOException("can not create dirs for " + newSource);
			}
			
			com.google.common.io.Files.copy(source, newSource);
		}
		
		
		final File libDir = new File(Resources.getResource("test-classes/test-lib-dir").toURI());
		newFilepath = Joiner.on(File.separator).join(workDir, libDir.getName());
		
		this.libDir = new File(newFilepath);
		delete(this.libDir);
		
		for (File jar : Files.in(libDir.getAbsolutePath()).all()) {
			final String relativeJarPath = Tokenizer.delimiter("test-lib-dir/").tokenize(jar.getPath()).lastToken();
			final File newJar = new File(Joiner.on(File.separator).join(this.libDir, relativeJarPath));
			
			new File(newJar.getParent()).mkdirs(); // result ignored because the directory might already exist which is ok
			
			com.google.common.io.Files.copy(jar, newJar);
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
			throw new IOException("failed to delete: " + file);
		}
	}
	
	@Test
	public void optimize() {
		optimize.execute(srcDir.getAbsolutePath(), libDir.getAbsolutePath());
	}
}
