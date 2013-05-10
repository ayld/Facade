package net.ayld.facade.resolver.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.ayld.facade.exception.ExtractionException;
import net.ayld.facade.model.ExplodedJar;
import net.ayld.facade.resolver.DependencyBundleResolver;
import net.ayld.facade.util.Tokenizer;
import net.ayld.facade.util.annotation.ThreadSafe;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@ThreadSafe
public class ManualDependencyBundleResolver implements DependencyBundleResolver{

	private String workDir;

	@Override
	public Set<JarFile> resolve(String className, Set<JarFile> bundles) throws IOException {
		// TODO check that the class name is properly formatted
		final String shortClassName = Tokenizer.delimiter(".").tokenize(className).lastToken();
		
		final Set<JarFile> result = Sets.newHashSet();
		for (ExplodedJar explodedJar : extractJars(bundles)) {
			
			final File extractedDir = new File(explodedJar.getExtractedPath());
			
			if (containsFile(extractedDir, shortClassName, new HashSet<Boolean>())) {
				result.add(explodedJar.getArchive());
			}
		}
		return ImmutableSet.copyOf(result);
	}
	
	private static boolean containsFile(File dir, String filename, Set<Boolean> results) {

        final File root = new File(dir.getAbsolutePath());
        final File[] children = root.listFiles();

        for (File child : children) {
        	
            if (child.isDirectory()) {
            	results.add(containsFile(child, filename, results));
            }
            else {
            	final String childName = Tokenizer.delimiter(".").tokenize(child.getName()).firstToken();
            	
            	if (childName.toLowerCase().equals(filename.toLowerCase())) {
            		return true;
            	}
            }
        }
        return results.contains(Boolean.TRUE);
    }
	
	private Set<ExplodedJar> extractJars(Set<JarFile> bundles) throws IOException {
		final Set<ExplodedJar> result = Sets.newHashSet();
		
		for (JarFile bundle : bundles) {
			result.add(extractJar(bundle));
		}
		
		return ImmutableSet.copyOf(result);
	}
	
	private ExplodedJar extractJar(JarFile jar) throws IOException { // TODO split to smaller methods
		
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
					throw new ExtractionException("could not create directory: " + fileEntry + ", contained in jar: " + jarName);
				}
				
				// I really hate java's streams ... really ...
				InputStream jarInputStream = null;
				FileOutputStream classOutputStream = null;
				try {
					
					jarInputStream = jar.getInputStream(entry);
					classOutputStream = new FileOutputStream(fileEntry);
					
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
		}
		return new ExplodedJar(jarPath, jar);
	}
	
	@Required
	public void setWorkDir(String workDir) {
		
		final File dir = new File(workDir);
		
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("path " + workDir + ", is not a directory");
		}
		
		if (workDir.endsWith(File.separator)) {
			workDir = workDir.replaceFirst(File.separator + "$", ""); // XXX remove last / if present
		}
		
		this.workDir = workDir;
	}
}
