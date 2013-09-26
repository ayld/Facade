package net.ayld.facade.api.impl;

import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.jar.JarFile;

import net.ayld.facade.api.ApiBuilder;
import net.ayld.facade.event.model.ClassResolverUpdate;
import net.ayld.facade.event.model.JarExtractionStartUpdate;
import net.ayld.facade.event.model.SourceResolverUpdate;
import net.ayld.facade.util.Files;
import net.ayld.facade.util.Tokenizer;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
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
	public void addListeners() throws IOException {
		ApiBuilder.outputJar(new File("facade.jar"))
			.addListener(new Object() {
				
				@Subscribe
				public void receiveSourceResolverUpdates(SourceResolverUpdate u) {
					assertTrue(u != null);
					assertTrue(!Strings.isNullOrEmpty(u.getMessage()));
				}
				
				@Subscribe
				public void receiveClassResolverUpdates(ClassResolverUpdate u) {
					assertTrue(u != null);
					assertTrue(!Strings.isNullOrEmpty(u.getMessage()));
				}
				
				@Subscribe
				public void receiveJarExploderUpdates(JarExtractionStartUpdate u) {
					assertTrue(u != null);
					assertTrue(!Strings.isNullOrEmpty(u.getMessage()));
					assertTrue(u.getOn() != null);
					assertTrue(u.getTo() != null);
				}
			})
			.build()
			.compressDependencies(srcDir, libDir);
	}

	@Test
	public void compress() throws IOException { // TODO this is just a sanity check, a bit more asserts are needed
		final JarFile facadeJar = ApiBuilder.buildWithDefaultConfig().compressDependencies(srcDir, libDir);
		assertTrue(facadeJar != null);
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
