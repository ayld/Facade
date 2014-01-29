package net.ayld.facade.api;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.ayld.facade.bundle.JarExploder;
import net.ayld.facade.bundle.JarMaker;
import net.ayld.facade.dependency.matcher.DependencyMatcherStrategy;
import net.ayld.facade.dependency.resolver.DependencyResolver;
import net.ayld.facade.model.ClassFile;
import net.ayld.facade.model.ClassName;
import net.ayld.facade.model.SourceFile;
import net.ayld.facade.util.Components;
import net.ayld.facade.util.Directories;
import net.ayld.facade.util.Files;
import net.ayld.facade.util.Settings;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarFile;

public final class Minimizer {
	private static final String JAVA_API_ROOT_PACKAGE = "java";
	
	private final DependencyMatcherStrategy dependencyMatcherStrategy = Components.DEPENDENCY_MATCHER_STRATEGY.getInstance();
	private final DependencyResolver<ClassFile> classDependencyResolver = Components.CLASS_DEPENDENCY_RESOLVER.getInstance();
	private final DependencyResolver<SourceFile> sourceDependencyResolver = Components.SOURCE_DEPENDENCY_RESOLVER.getInstance();

    private final JarMaker jarMaker = Components.JAR_MAKER.getInstance();
    private final JarExploder libJarExploder = Components.LIB_JAR_EXPLODER.getInstance();
	private final JarExploder explicitJarExploder = Components.EXPLICIT_JAR_EXPLODER.getInstance(); // don't use if you're under 18
	
	private String workDir = Settings.DEFAULT_OUT_DIR.getValue();
	private String explicitOutDir = Settings.EXPLICIT_OUT_DIR.getValue();
	private File outJar = new File(
		Joiner
			.on(File.separator)
			.join(workDir, Settings.DEFAULT_FACADE_JAR_NAME.getValue())
	);
	
	private File libDir;
	private final File sourceDir;

	private Set<JarFile> forceIncludeJars = Sets.newHashSet();
	private Set<ClassName> forceIncludeClasses = Sets.newHashSet();
	
	private Minimizer(File sourceDir) {
		final File outJarDir = new File(outJar.getParent());
		
		if (!outJarDir.exists() && !outJarDir.mkdirs()) {
			throw new IllegalStateException("unable to create parent dir for output jar: " + outJar.getParent());
		}
		
		this.sourceDir = sourceDir;
	}

	public static Minimizer sources(String srcDir) {
		final File sourceDir = new File(srcDir);
		
		if (!sourceDir.exists() || !sourceDir.isDirectory()) {
			throw new IllegalArgumentException("directory at: " + srcDir + " does not exist or is not a directory");
		}
		
		return new Minimizer(sourceDir);
	}
	
	public Minimizer libs(String libDir) {
		final File lib = new File(libDir);
		
		if (!sourceDir.exists() || !sourceDir.isDirectory()) {
			throw new IllegalArgumentException("directory at: " + libDir + " does not exist or is not a directory");
		}
		
		this.libDir = lib;
		
		return this;
	}
	
	public Minimizer output(String outDir) {
		final File out = new File(outDir);
		
		if (!out.exists() && !out.mkdirs()) {
			throw new IllegalStateException("unable to create dir for output jar: " + outDir);
		}
		
		this.outJar = new File(Joiner.on(File.separator).join(out.getAbsolutePath(), Settings.DEFAULT_FACADE_JAR_NAME.getValue()));
		
		return this;
	}
	
	public Minimizer forceInclude(JarFile... jars) {
		this.forceIncludeJars.addAll(Arrays.asList(jars));
		
		return this;
	}
	
	public Minimizer forceInclude(ClassName... classes) {
		this.forceIncludeClasses.addAll(Arrays.asList(classes));
		
		return this;
	}
	
	public JarFile getJar() throws IOException {
		final String libDirPath = libDir.getAbsolutePath();
		extractLibJars(libDirPath);
		
		final Set<SourceFile> sources = Sets.newHashSet();
		for (File sourceFile : Files.in(sourceDir.getAbsolutePath()).withExtension(SourceFile.EXTENSION).list()) {
			sources.add(SourceFile.fromFile(sourceFile));
		}
		
		final Set<ClassName> sourceDependencies = sourceDependencyResolver.resolve(sources);
		
		final Set<File> libClasses = ImmutableSet.copyOf(
				Files.in(workDir).withExtension(ClassFile.EXTENSION).list()
		);
		final Set<ClassFile> foundDependencies = findInLib(sourceDependencies, libClasses);
		
		addDependenciesOfDependencies(foundDependencies, libClasses);
		foundDependencies.addAll(forceIncludeDependenciesAsFiles(this.forceIncludeJars, this.forceIncludeClasses, libClasses));
		
		final Set<File> dependenciesForPackaging = Sets.newHashSetWithExpectedSize(foundDependencies.size());
		for (ClassFile dep : foundDependencies) {
			dependenciesForPackaging.add(dep.physicalFile());
		}

        final JarFile result = jarMaker.zip(dependenciesForPackaging);

        cleanWorkDir();

        return result;
	}

    private void cleanWorkDir() throws IOException {
        final Set<File> dirtyDirs = Directories.in(workDir).nameEndsWith(JarMaker.JAR_FILE_EXTENSION).list(); // dirty ho ho ho ;)
        for (File dirty : dirtyDirs) {
            Files.deleteRecursive(dirty);
        }
    }

    private Set<ClassFile> forceIncludeDependenciesAsFiles(Set<JarFile> explicitIncludeJars, Set<ClassName> explicitIncludeClasses, final Set<File> libClasses) throws IOException {
		final Set<ClassFile> result = Sets.newHashSet();
		
		for (ClassName includeClass : explicitIncludeClasses) {
			
			final Set<ClassFile> foundInLib = findInLib(ImmutableSet.of(includeClass), libClasses);
			if (foundInLib.size() < 1) {
				throw new IllegalStateException("can't find user defined class: " + includeClass + ", in: " + libDir.getAbsolutePath());
			}
			result.addAll(foundInLib);
		}
		
		explicitJarExploder.explode(explicitIncludeJars);
		
		for (File extracted : Files.in(explicitOutDir).withExtension(ClassFile.EXTENSION).list()) {
			result.add(ClassFile.fromFile(extracted));
		}
		
		return result;
	}
	
	private Set<ClassFile> addDependenciesOfDependencies(Set<ClassFile> deps, final Set<File> libClasses) throws IOException {
		removeJavaApiDeps(deps);
		
		deps.addAll(findInLib(classDependencyResolver.resolve(deps), libClasses));
		
		final int sizeBeforeResolve = deps.size();
		if (deps.size() == sizeBeforeResolve) {
			return deps;
		}
		
		return addDependenciesOfDependencies(deps, libClasses);
	}
	
	private void removeJavaApiDeps(Set<ClassFile> deps) {
		for (Iterator<ClassFile> iterator = deps.iterator(); iterator.hasNext();) {
			
			 final ClassFile dep = iterator.next();
			 
			 if (dep.qualifiedName().toString().startsWith(JAVA_API_ROOT_PACKAGE)) {
				 iterator.remove();
			 }
		}
	}
	
	private Set<ClassFile> findInLib(Set<ClassName> dependencyNames, Set<File> libClasses) throws IOException {
		
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
            try {
                libJars.add(new JarFile(jarFile));
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }

		libJarExploder.explode(libJars);
	}
}
