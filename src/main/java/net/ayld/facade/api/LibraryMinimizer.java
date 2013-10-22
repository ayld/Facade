package net.ayld.facade.api;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarFile;

import net.ayld.facade.bundle.JarExploder;
import net.ayld.facade.bundle.JarMaker;
import net.ayld.facade.dependency.matcher.DependencyMatcherStrategy;
import net.ayld.facade.dependency.resolver.DependencyResolver;
import net.ayld.facade.model.ClassFile;
import net.ayld.facade.model.ClassName;
import net.ayld.facade.model.SourceFile;
import net.ayld.facade.util.Components;
import net.ayld.facade.util.Files;
import net.ayld.facade.util.Settings;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public final class LibraryMinimizer {
	private static final String JAVA_API_ROOT_PACKAGE = "java";
	
	private final DependencyResolver<ClassFile> classDependencyResolver = Components.CLASS_DEPENDENCY_RESOLVER.<DependencyResolver<ClassFile>>getInstance();
	private final DependencyResolver<SourceFile> sourceDependencyResolver = Components.SOURCE_DEPENDENCY_RESOLVER.<DependencyResolver<SourceFile>>getInstance();
	private final DependencyMatcherStrategy dependencyMatcherStrategy = Components.DEPENDENCY_MATCHER_STRATEGY.<DependencyMatcherStrategy>getInstance();
	
	private final JarExploder jarExploder = Components.JAR_EXPLODER.<JarExploder>getInstance();
	private final JarMaker jarMaker = Components.JAR_MAKER.<JarMaker>getInstance();
	
	private String workDir = Settings.DEFAULT_OUT_DIR.getValue();
	private File outJar = new File(
		Joiner
			.on(File.separator)
			.join(workDir, Settings.DEFAULT_FACADE_JAR_NAME.getValue())
	);
	
	private File libDir;
	private final File sourceDir;

	private LibraryMinimizer(File sourceDir) {
		final File outJarDir = new File(outJar.getParent());
		
		if (!outJarDir.exists() && !outJarDir.mkdirs()) {
			throw new IllegalStateException("unable to create parent dir for output jar: " + outJar.getParent());
		}
		
		this.sourceDir = sourceDir;
	}

	public static LibraryMinimizer forSourcesAt(String srcDir) {
		final File sourceDir = new File(srcDir);
		
		if (!sourceDir.exists() || !sourceDir.isDirectory()) {
			throw new IllegalArgumentException("directory at: " + srcDir + " does not exist or is not a directory");
		}
		
		return new LibraryMinimizer(sourceDir);
	}
	
	public LibraryMinimizer withLibs(String libDir) {
		final File lib = new File(libDir);
		
		if (!sourceDir.exists() || !sourceDir.isDirectory()) {
			throw new IllegalArgumentException("directory at: " + libDir + " does not exist or is not a directory");
		}
		
		this.libDir = lib;
		
		return this;
	}
	
	public LibraryMinimizer output(String outDir) {
		final File out = new File(outDir);
		
		if (!out.exists() && !out.mkdirs()) {
			throw new IllegalStateException("unable to create dir for output jar: " + outDir);
		}
		
		this.outJar = new File(Joiner.on(File.separator).join(out.getAbsolutePath(), Settings.DEFAULT_FACADE_JAR_NAME.getValue()));
		
		return this;
	}
	
	public JarFile getFile() throws IOException {
		final String libDirPath = libDir.getAbsolutePath();
		extractLibJars(libDirPath);
		
		final Set<SourceFile> sources = Sets.newHashSet();
		for (File sourceFile : Files.in(sourceDir.getAbsolutePath()).withExtension(SourceFile.EXTENTION).list()) {
			sources.add(SourceFile.fromFile(sourceFile));
		}
		
		final Set<ClassFile> sourceDependenciesAsFiles = dependenciesAsFiles(sourceDependencyResolver.resolve(sources));
		final Set<ClassFile> libDependenciesAsFiles = getDependenciesOfDependencies(sourceDependenciesAsFiles);
		
		final Set<File> dependenciesForPackaging = Sets.newHashSetWithExpectedSize(sourceDependenciesAsFiles.size() + libDependenciesAsFiles.size());
		
		for (ClassFile sourceDep : sourceDependenciesAsFiles) {
			dependenciesForPackaging.add(sourceDep.physicalFile());
		}
		for (ClassFile libDep : libDependenciesAsFiles) {
			dependenciesForPackaging.add(libDep.physicalFile());
		}
		
		return jarMaker.zip(dependenciesForPackaging);
	}
	
	private Set<ClassFile> getDependenciesOfDependencies(Set<ClassFile> deps) throws IOException {
		removeJavaApiDeps(deps);
		
		deps.addAll(dependenciesAsFiles(classDependencyResolver.resolve(deps)));
		
		final int sizeBeforeResolve = deps.size();
		if (deps.size() == sizeBeforeResolve) {
			return deps;
		}
		
		return getDependenciesOfDependencies(deps);
	}
	
	private void removeJavaApiDeps(Set<ClassFile> deps) {
		for (Iterator<ClassFile> iterator = deps.iterator(); iterator.hasNext();) {
			
			 final ClassFile dep = iterator.next();
			 
			 if (dep.qualifiedName().toString().startsWith(JAVA_API_ROOT_PACKAGE)) {
				 iterator.remove();
			 }
		}
	}
	
	private Set<ClassFile> dependenciesAsFiles(Set<ClassName> dependencyNames) throws IOException {
		
		final Set<File> libClasses = ImmutableSet.copyOf(
				Files.in(workDir).withExtension(ClassFile.EXTENSION).list()
	    );
		
		final Set<ClassFile> result = Sets.newHashSetWithExpectedSize(dependencyNames.size());
		for (ClassName dependencyName : dependencyNames) {
			for (File libClass : libClasses) {
				
				final ClassFile libClassFile = ClassFile.fromFile(libClass);
				
				if (dependencyMatcherStrategy.matches(dependencyName, libClassFile)) {
					result.add(libClassFile);
				}
			}
		}
		return result;
	}
	
	private void extractLibJars(String libDir) throws IOException {
		final Set<JarFile> libJars = Sets.newHashSet();
		
		for (File jarFile : Files.in(libDir).withExtension(JarMaker.JAR_FILE_EXTENSION).list()) {
			libJars.add(new JarFile(jarFile));
		}
		
		jarExploder.explode(libJars);
	}
}
