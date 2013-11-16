package net.ayld.facade.api;

import com.google.common.collect.ImmutableSet;
import net.ayld.facade.dependency.resolver.DependencyResolver;
import net.ayld.facade.model.ClassFile;
import net.ayld.facade.model.ClassName;
import net.ayld.facade.model.SourceFile;
import net.ayld.facade.util.Components;

import java.io.IOException;
import java.util.Set;

public final class Dependencies {
	
	private final DependencyResolver<ClassFile> classDependencyResolver = Components.CLASS_DEPENDENCY_RESOLVER.getInstance();
	private final DependencyResolver<SourceFile> sourceDependencyResolver = Components.SOURCE_DEPENDENCY_RESOLVER.getInstance();
	
	private final boolean sourceResolution;
	private final Set<?> of;
	
	private Dependencies(Set<?> of, boolean sourceResolution) {
		this.sourceResolution = sourceResolution;
		this.of = of;
	}

	public static Dependencies ofSources(Set<SourceFile> sources) {
		return new Dependencies(sources, true);
	}
	
	public static Dependencies ofClasses(Set<ClassFile> classes) {
		return new Dependencies(classes, false);
	}
	
	public static Dependencies ofSource(SourceFile source) {
		return new Dependencies(ImmutableSet.of(source), true);
	}
	
	public static Dependencies ofClass(ClassFile clazz) {
		return new Dependencies(ImmutableSet.of(clazz), false);
	}
	
	@SuppressWarnings("unchecked")
	public Set<ClassName> set() {
		try {
			if (sourceResolution) {
				return sourceDependencyResolver.resolve((Set<SourceFile>) of);
			}
			else {
				return classDependencyResolver.resolve((Set<ClassFile>) of);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
