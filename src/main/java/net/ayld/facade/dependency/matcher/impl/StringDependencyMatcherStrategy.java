package net.ayld.facade.dependency.matcher.impl;

import java.io.File;

import net.ayld.facade.dependency.matcher.DependencyMatcherStrategy;
import net.ayld.facade.model.ClassFile;
import net.ayld.facade.model.ClassName;
import net.ayld.facade.util.Tokenizer;

public class StringDependencyMatcherStrategy implements DependencyMatcherStrategy{

	@Override
	public boolean matches(ClassName className, ClassFile classFile) {
		final String shortClassName = Tokenizer.delimiter(".").tokenize(className.toString()).lastToken();
		
		final String classFilename = Tokenizer.delimiter(File.separator).tokenize(classFile.toString()).lastToken();
		final String classFilenameNoExtention = Tokenizer.delimiter(".").tokenize(classFilename).firstToken(); 
		
		return shortClassName.toLowerCase().equals(classFilenameNoExtention.toLowerCase());
	}

	
}
