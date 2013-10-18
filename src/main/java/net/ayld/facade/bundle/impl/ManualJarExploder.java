package net.ayld.facade.bundle.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.ayld.facade.bundle.JarExploder;
import net.ayld.facade.component.ListenableComponent;
import net.ayld.facade.event.model.JarExtractionStartEvent;
import net.ayld.facade.exception.ExtractionException;
import net.ayld.facade.model.ExplodedJar;
import net.ayld.facade.util.Tokenizer;
import net.ayld.facade.util.annotation.ThreadSafe;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@ThreadSafe
public class ManualJarExploder extends ListenableComponent implements JarExploder{

	private String workDir;
	
	@Override
	public Set<ExplodedJar> explode(Set<JarFile> jars) throws IOException {
		final Set<ExplodedJar> result = Sets.newHashSet();
		
		for (JarFile jar : jars) {
			result.add(explode(jar));
		}
		
		return ImmutableSet.copyOf(result);
	}
	
	@Override
	public ExplodedJar explode(JarFile jar) throws IOException {// XXX huge jumbo method
		eventBus.post(new JarExtractionStartEvent("extracting: " + jar.getName() + ", to: " + workDir, this.getClass()));
		
		final String jarName = Tokenizer.delimiter(File.separator).tokenize(jar.getName()).lastToken();
		final String jarPath = Joiner.on(File.separator).join(workDir, jarName);
		
		final File jarDir = new File(jarPath);
		for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();) {
			
			final JarEntry entry = entries.nextElement();
			final String entryFilename = Joiner.on(File.separator).join(jarDir.getAbsolutePath(), entry.getName());
			
			if (entry.isDirectory()) {
				final File dirEntry = new File(entryFilename);
				
				if (!dirEntry.exists() && !dirEntry.mkdirs()) {
					throw new ExtractionException("could not create directory: " + dirEntry + ", contained in jar: " + jarName);
				}
			}
			else {
				final File fileEntry = new File(entryFilename);
				
				if (!fileEntry.getParentFile().exists() && !fileEntry.getParentFile().mkdirs()) {
					throw new ExtractionException("could not create: " + fileEntry + ", contained in jar: " + jarName);
				}
				
				explode(jar, entry, fileEntry);
			}
		}
		return new ExplodedJar(jarPath, jar);
	}

	private static void explode(JarFile source, JarEntry content, File destination) throws IOException, FileNotFoundException {
		InputStream jarInputStream = null;
		FileOutputStream classOutputStream = null;
		try {
			
			jarInputStream = source.getInputStream(content);
			classOutputStream = new FileOutputStream(destination);
			
			while (jarInputStream.available() > 0) {
				classOutputStream.write(jarInputStream.read());
			}
			
		} finally {
			if (jarInputStream != null) {
				jarInputStream.close();
			}
			if (classOutputStream != null) {
				classOutputStream.close();
			}
		}
	}

	@Required
	public void setWorkDir(String workDir) {
		
		final File dir = new File(workDir);
		
		if (!dir.mkdirs()) {
			// TODO log warning
		}
		
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("path " + workDir + ", is not a directory");
		}
		
		if (workDir.endsWith(File.separator)) {
			workDir = workDir.replaceFirst(File.separator + "$", ""); // XXX remove last / if present
		}
		
		this.workDir = workDir;
	}
}
