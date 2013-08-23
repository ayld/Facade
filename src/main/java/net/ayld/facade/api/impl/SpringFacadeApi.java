package net.ayld.facade.api.impl;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.jar.JarFile;

import net.ayld.facade.api.Facade;
import net.ayld.facade.bundle.JarExploder;
import net.ayld.facade.bundle.JarMaker;
import net.ayld.facade.dependency.matcher.DependencyMatcherStrategy;
import net.ayld.facade.dependency.resolver.ClassDependencyResolver;
import net.ayld.facade.dependency.resolver.SourceDependencyResolver;
import net.ayld.facade.model.ClassFile;
import net.ayld.facade.model.ClassName;
import net.ayld.facade.model.SourceFile;
import net.ayld.facade.util.Files;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public final class SpringFacadeApi implements Facade {
	
	private static final String JAR_FILE_EXTENSION = "jar";
	private static final String JAVA_API_ROOT_PACKAGE = "java";

	private ClassDependencyResolver classDependencyResolver;
	private SourceDependencyResolver sourceDependencyResolver;
	private DependencyMatcherStrategy dependencyMatcherStrategy;
	
	private JarExploder jarExploder;
	private JarMaker jarMaker;
	
	private String libExtractDir;

	@Override
	public JarFile compressDependencies(File sourceDir, File libDir) throws IOException {
		if (sourceDir == null || !sourceDir.isDirectory()) {
			throw new IllegalArgumentException("sourceDir: " + sourceDir + ", is either null or not a directory");
		}
		if (libDir == null || !libDir.isDirectory()) {
			throw new IllegalArgumentException("libDir: " + libDir + ", is either null or not a directory");
		}
		
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

		final int sizeBeforeResolve = deps.size();
		
		deps.addAll(dependenciesAsFiles(classDependencyResolver.resolve(deps)));
		
		if (deps.size() == sizeBeforeResolve) {
			return deps;
		}
		
		return getDependenciesOfDependencies(deps);
	}

	private void removeJavaApiDeps(Set<ClassFile> deps) {
		for (ClassFile dep : deps) {
			if (dep.qualifiedName().toString().startsWith(JAVA_API_ROOT_PACKAGE)) {
				deps.remove(dep);
			}
		}
	}

	private Set<ClassFile> dependenciesAsFiles(Set<ClassName> dependencyNames) throws IOException {
		
		final Set<File> libClasses = Sets.newHashSet();
		for (File allLibClasses : Files.in(libExtractDir).withExtension(ClassFile.EXTENSION).list()) {
			libClasses.add(allLibClasses);
		}
		
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
		
		for (File jarFile : Files.in(libDir).withExtension(JAR_FILE_EXTENSION).list()) {
			libJars.add(new JarFile(jarFile));
		}
		
		jarExploder.explode(libJars);
	}

	@Required
	public void setDependencyMatcherStrategy(DependencyMatcherStrategy dependencyMatcherStrategy) {
		this.dependencyMatcherStrategy = dependencyMatcherStrategy;
	}

	@Required
	public void setJarExploder(JarExploder jarExploder) {
		this.jarExploder = jarExploder;
	}

	@Required
	public void setJarMaker(JarMaker jarMaker) {
		this.jarMaker = jarMaker;
	}

	@Required
	public void setLibExtractDir(String libExtractDir) {
		if (Strings.isNullOrEmpty(libExtractDir) || !new File(libExtractDir).isDirectory()) {
			throw new IllegalArgumentException("libExtractDir: " + libExtractDir + " does not exist or is not a directory");
		}
		this.libExtractDir = libExtractDir;
	}

	@Required
	public void setClassDependencyResolver(ClassDependencyResolver classDependencyResolver) {
		this.classDependencyResolver = classDependencyResolver;
	}

	@Required
	public void setSourceDependencyResolver(SourceDependencyResolver sourceDependencyResolver) {
		this.sourceDependencyResolver = sourceDependencyResolver;
	}
}
