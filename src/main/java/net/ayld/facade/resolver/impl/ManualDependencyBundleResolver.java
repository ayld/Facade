package net.ayld.facade.resolver.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;

import net.ayld.facade.bundle.JarExploder;
import net.ayld.facade.model.ExplodedJar;
import net.ayld.facade.resolver.DependencyBundleResolver;
import net.ayld.facade.util.Tokenizer;
import net.ayld.facade.util.annotation.ThreadSafe;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@ThreadSafe
public class ManualDependencyBundleResolver implements DependencyBundleResolver{

	private JarExploder jarExploder;
	
	@Override
	public Set<JarFile> resolve(String qualifiedClassName, Set<JarFile> bundles) throws IOException {
		// TODO check that the class name is properly formatted (is a fully qualified class name)
		final String shortClassName = Tokenizer.delimiter(".").tokenize(qualifiedClassName).lastToken();
		
		final Set<JarFile> result = Sets.newHashSet();
		for (ExplodedJar explodedJar : jarExploder.explode(bundles)) {
			
			final File extractedJarDir = new File(explodedJar.getExtractedPath());
			
			if (containsDependency(extractedJarDir, shortClassName, new HashSet<Boolean>())) {
				result.add(explodedJar.getArchive());
			}
		}
		return ImmutableSet.copyOf(result);
	}
	
	private static boolean containsDependency(File dir, String filename, Set<Boolean> results) {

        final File root = new File(dir.getAbsolutePath());
        final File[] children = root.listFiles();

        for (File child : children) {
        	
            if (child.isDirectory()) {
            	results.add(containsDependency(child, filename, results));
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
	
	@Required
	public void setJarExploder(JarExploder jarExploder) {
		this.jarExploder = jarExploder;
	}
}
