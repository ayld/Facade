package net.ayld.facade.api.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import net.ayld.facade.api.ApiBuilder;
import net.ayld.facade.util.Files;
import net.ayld.facade.util.Tokenizer;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.io.Resources;

public class TestSpringFacadeApiNoContext {
	
	private final String workDir = "/tmp/facade-out";
	
	private File srcDir;
	private File libDir;
	
	@Before
	public void init() throws URISyntaxException, IOException { // XXX ... this is just plain messy 
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
	
	@Test
	public void compress() throws IOException {
		ApiBuilder.build().compressDependencies(srcDir, libDir);
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
