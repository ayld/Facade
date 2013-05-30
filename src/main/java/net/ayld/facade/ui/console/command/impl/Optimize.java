package net.ayld.facade.ui.console.command.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

import net.ayld.facade.dependency.resolver.ClassDependencyResolver;
import net.ayld.facade.dependency.resolver.DependencyBundleResolver;
import net.ayld.facade.ui.console.command.Command;
import net.ayld.facade.util.annotation.NotThreadSafe;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;

@NotThreadSafe
public class Optimize extends AbstractCommand implements Command{

	private static final int ALLOWED_ARG_COUNT = 2;
	private static final String JAVA_SOURCE_FILE_EXTENTION = "java";
	private static final String JAR_BUNDLE_EXTENTION = "jar";
	
	private DependencyBundleResolver bundleResolver;
	private ClassDependencyResolver dependencyResolver;
	
	public Optimize() {
		supportNames("opt", "optimize", "trim");
	}

	@Override
	protected void internalExecute(String... args) {
		validateArgs(args);
		
		final String srcDir = args[0];
		final String libDir = args[1];
		
		final Set<String> sourceDependencies = findDependencies(srcDir);
		
		final Map<String, Set<JarFile>> dependenciesToBundles = new MapMaker()
																	.initialCapacity(sourceDependencies.size())
																	.concurrencyLevel(1)
																	.makeMap();
		for (String dependency : sourceDependencies) {
			try {
				
				final Set<JarFile> resolved = bundleResolver.resolve(dependency, findBundles(libDir));
				dependenciesToBundles.put(dependency, resolved);
				
			} catch (IOException e) {
				// XXX wrapping because otherwise I have to change super method signature
				//     further thought on whether this is correct is needed
				throw new RuntimeException(e);
			}
			
		}
	}

	private Set<JarFile> findBundles(String libDir) {
		final Set<JarFile> result = Sets.newHashSet();
		for (File jar : findFilesWithExtention(new File(libDir), JAR_BUNDLE_EXTENTION)) {
			try {
				
				result.add(new JarFile(jar));
				
			} catch (IOException e) {
				// XXX wrapping because otherwise I have to change super method signature
				//     further thought on whether this is correct is needed
				throw new RuntimeException(e);
			}
		}
		return result;
	}
	
	private Set<String> findDependencies(String srcDir) {
		Set<String> result = Collections.emptySet();
		for (File source : findFilesWithExtention(new File(srcDir), JAVA_SOURCE_FILE_EXTENTION)) {
			try {
				
				result = dependencyResolver.resolve(source);
				
			} catch (IOException e) {
				// XXX wrapping because otherwise I have to change super method signature
				//     further thought on whether this is correct is needed
				throw new RuntimeException(e);
			}
		}
		return result;
	}
	
	private Set<File> findFilesWithExtention(File dir, final String ext) {
		final File[] sources = dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				
				if (name.endsWith(ext)) {
					return true;
				}
				
				return false;
			}
		});
		final Set<File> result = Sets.newHashSet();
		for (File source : sources) {
			result.add(source);
		}
		return ImmutableSet.copyOf(result);
	}
	
	private static void validateArgs(String... args) {
		
		if (args.length != ALLOWED_ARG_COUNT) {
			throw new IllegalArgumentException("icorrect number of arguments, expected " + ALLOWED_ARG_COUNT);
		}
		
		for (String arg : args) {
			
			if (Strings.isNullOrEmpty(arg)) {
				throw new IllegalArgumentException("empty strings not allowed as arguments");
			}
			
			if (!new File(arg).isDirectory()) {
				throw new IllegalArgumentException("argument: " + arg + ", is not a directory");
			}
		}
	}

	@Required
	public void setBundleResolver(DependencyBundleResolver bundleResolver) {
		this.bundleResolver = bundleResolver;
	}

	@Required
	public void setDependencyResolver(ClassDependencyResolver dependencyResolver) {
		this.dependencyResolver = dependencyResolver;
	}
}
