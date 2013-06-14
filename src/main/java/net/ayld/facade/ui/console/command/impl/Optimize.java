package net.ayld.facade.ui.console.command.impl;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

import net.ayld.facade.bundle.JarExploder;
import net.ayld.facade.bundle.JarMaker;
import net.ayld.facade.dependency.resolver.ClassDependencyResolver;
import net.ayld.facade.dependency.resolver.DependencyBundleResolver;
import net.ayld.facade.model.ClassName;
import net.ayld.facade.model.ExplodedJar;
import net.ayld.facade.ui.console.command.Command;
import net.ayld.facade.util.Files;
import net.ayld.facade.util.annotation.NotThreadSafe;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;

@NotThreadSafe
public class Optimize extends AbstractCommand implements Command{

	private static final int ALLOWED_ARG_COUNT = 2;
	private static final String JAVA_SOURCE_FILE_EXTENTION = "java";
	private static final String JAVA_CLASS_FILE_EXTENTION = "class";
	private static final String JAR_BUNDLE_EXTENTION = "jar";
	
	private DependencyBundleResolver bundleResolver;
	private ClassDependencyResolver dependencyResolver;
	
	private JarExploder jarExploader;
	private JarMaker jarMaker;
	
	public Optimize() {
		supportNames("opt", "optimize", "trim");
	}

	@Override
	protected void internalExecute(String... args) {// XXX huge jumbo method doing 293878475 things ...
		validateArgs(args);
		
		final String srcDir = args[0];
		final String libDir = args[1];
		
		final Set<String> sourceDependencies = findDependencies(srcDir);
		
		final Map<String, Set<ExplodedJar>> dependenciesToBundles = new MapMaker()
																	.initialCapacity(sourceDependencies.size())
																	.concurrencyLevel(1)
																	.makeMap();
		for (String dependency : sourceDependencies) {
			try {
				
				final Set<JarFile> resolved = bundleResolver.resolve(new ClassName(dependency), findBundles(libDir));
				final Set<ExplodedJar> resolvedExploded = Sets.newHashSetWithExpectedSize(resolved.size());
				for (JarFile bundle : resolved) {
					
					resolvedExploded.add(jarExploader.explode(bundle));
				}
				dependenciesToBundles.put(dependency, resolvedExploded);
			} catch (IOException e) {
				// XXX wrapping because otherwise I have to change super method signature
				//     further thought on whether this is correct is needed
				throw new RuntimeException(e);
			}
		}
		
		final Set<File> dependenciesToPackage = Sets.newHashSet();
		for (String dependency : dependenciesToBundles.keySet()) {
			
			final Set<ExplodedJar> bundles = dependenciesToBundles.get(dependency);
			for (ExplodedJar bundle : bundles) {
				
				final String dependencyFilename = Joiner.on(".").join(dependency, JAVA_CLASS_FILE_EXTENTION);
				
				try {
					
					dependenciesToPackage.add(Files.in(bundle.getExtractedPath()).named(dependencyFilename).single());
					
				} catch (IOException e) {
					// XXX wrapping because otherwise I have to change super method signature
					//     further thought on whether this is correct is needed
					throw new RuntimeException(e);
				}
			}
		}
		
		try {
			
			jarMaker.zip(dependenciesToPackage);
			
		} catch (IOException e) {
			// XXX wrapping because otherwise I have to change super method signature
			//     further thought on whether this is correct is needed
			throw new RuntimeException(e);
		}
	}

	private Set<JarFile> findBundles(String libDir) {
		final Set<JarFile> result = Sets.newHashSet();
		try {
			for (File jar : Files.in(libDir).withExtension(JAR_BUNDLE_EXTENTION).list()) {
					
				result.add(new JarFile(jar));
					
			}
		} catch (IOException e) {
			// XXX wrapping because otherwise I have to change super method signature
			//     further thought on whether this is correct is needed
			throw new RuntimeException(e);
		}
		return result;
	}
	
	private Set<String> findDependencies(String srcDir) {
		Set<String> result = Collections.emptySet();
		try {
			for (File source : Files.in(srcDir).withExtension(JAVA_SOURCE_FILE_EXTENTION).list()) {
					
				result = dependencyResolver.resolve(source);
					
			}
		} catch (IOException e) {
			// XXX wrapping because otherwise I have to change super method signature
			//     further thought on whether this is correct is needed
			throw new RuntimeException(e);
		}
		return result;
	}
	
	private static void validateArgs(String... args) {
		
		if (args.length != ALLOWED_ARG_COUNT) {
			throw new IllegalArgumentException("icorrect number of arguments " + args.length + ", expected " + ALLOWED_ARG_COUNT);
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
	public void setJarExploader(JarExploder jarExploader) {
		this.jarExploader = jarExploader;
	}

	@Required
	public void setJarMaker(JarMaker jarMaker) {
		this.jarMaker = jarMaker;
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
