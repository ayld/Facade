package net.ayld.facade.dependency.matcher.condition.impl;

import com.google.common.io.Files;

import net.ayld.facade.dependency.matcher.condition.MatchingCondition;
import net.ayld.facade.model.ClassFile;
import net.ayld.facade.model.ClassName;

public class ClassShortNameVsClassFilename implements MatchingCondition{

	@Override
	public boolean satisfied(ClassName className, ClassFile classFile) {
		final String shortName = className.shortName();
		final String classFilenameNoExtention = Files.getNameWithoutExtension(classFile.physicalFile().getName());
		
		return shortName.toLowerCase().equals(classFilenameNoExtention.toLowerCase());
	}
}
