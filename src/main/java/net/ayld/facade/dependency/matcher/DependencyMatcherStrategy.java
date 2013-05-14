package net.ayld.facade.dependency.matcher;

import net.ayld.facade.model.ClassFile;
import net.ayld.facade.model.ClassName;

public interface DependencyMatcherStrategy {
	
	public boolean matches(ClassName className, ClassFile classFilepath);
}
