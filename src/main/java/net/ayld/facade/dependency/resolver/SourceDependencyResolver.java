package net.ayld.facade.dependency.resolver;

import java.io.IOException;
import java.util.Set;

import net.ayld.facade.model.ClassName;
import net.ayld.facade.model.SourceFile;

/** 
 * Resolves dependencies of Java source files.
 * */
public interface SourceDependencyResolver {
	
	/** 
	 * Resolves the dependencies of a given source file.
	 * */
	public Set<ClassName> resolve(SourceFile source) throws IOException;
	
	/**
	 * Resolves the dependencies of a set of given source files.
	 * */
	public Set<ClassName> resolve(Set<SourceFile> sources) throws IOException;
}
