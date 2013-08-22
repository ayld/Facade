package net.ayld.facade.dependency.matcher.condition.impl;

import net.ayld.facade.dependency.matcher.condition.MatchingCondition;
import net.ayld.facade.model.ClassFile;
import net.ayld.facade.model.ClassName;

public class BinaryClassNameVsDependencyQualifiedName implements MatchingCondition{

	@Override
	public boolean satisfied(ClassName className, ClassFile classFile) {
		return className.equals(classFile.qualifiedName());
	}
}
