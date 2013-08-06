package net.ayld.facade.dependency.resolver;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import net.ayld.facade.model.ClassName;

/** 
 * Resolves dependencies of Java source files.
 * */
public interface SourceDependencyResolver {
	
	/** 
	 * Resolves the dependencies of a given source file.
	 * */
	public Set<ClassName> resolve(File sourceFile) throws IOException;
}
