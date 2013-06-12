package net.ayld.facade.dependency.matcher;

import net.ayld.facade.model.ClassFile;
import net.ayld.facade.model.ClassName;

/** 
 * Contains methods for {@link ClassName} vs. {@link ClassFile} matching.
 * 
 * Should be used when you need to know if a file on the file system is actually the file that matches a fully qualified class name.
 * 
 * */
public interface DependencyMatcherStrategy {
	
	/** 
	 * Checks if a given {@link ClassName} matches a given {@link ClassFile} on the file system.
	 * 
	 * @param className the class name to check against
	 * @param classFilepath the file to check against
	 * 
	 * @return true if the class name given corresponds to the .class file on the file system
	 * */
	public boolean matches(ClassName className, ClassFile classFilepath);
}
