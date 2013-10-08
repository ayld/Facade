package net.ayld.facade.api.impl;

import java.util.Set;

import junit.framework.Assert;
import net.ayld.facade.api.Dependencies;
import net.ayld.facade.model.ClassFile;
import net.ayld.facade.model.ClassName;
import net.ayld.facade.model.SourceFile;

import org.junit.Test;

public class DependenciesTest {

	@Test
	public void fromClass() {
		final Set<ClassName> dependencies = Dependencies
												.ofClass(ClassFile.fromClasspath("test-classes/primefaces-3.5.jar/org/primefaces/model/TreeTableModel.class"))
												.set();
		
		Assert.assertTrue(dependencies != null);
		Assert.assertTrue(!dependencies.isEmpty());
		Assert.assertTrue(dependencies.size() == 10);
	}
	
	@Test
	public void fromSource() {
		final Set<ClassName> dependencies = Dependencies
												.ofSource(SourceFile.fromClasspath("test-classes/ValidCoffee.java"))
												.set();
		
		Assert.assertTrue(dependencies != null);
		Assert.assertTrue(!dependencies.isEmpty());
		Assert.assertTrue(dependencies.size() == 3);
	}
}
