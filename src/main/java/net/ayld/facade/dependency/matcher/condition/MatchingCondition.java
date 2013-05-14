package net.ayld.facade.dependency.matcher.condition;

import net.ayld.facade.model.ClassFile;
import net.ayld.facade.model.ClassName;

public interface MatchingCondition {
	
	public boolean satisfied(ClassName className, ClassFile classFile);
}
