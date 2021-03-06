package net.ayld.facade.model;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

public class TestClassFile {
	
	@Test
	public void valid() {
		ClassFile.fromClasspath("test-classes/ClassName.class");
	}
	
	@Test
	public void dependencies() {
		final Set<ClassName> dependencies = ClassFile.fromClasspath("test-classes/ClassName.class").dependencies();
		
		Assert.assertTrue(dependencies != null);
		Assert.assertTrue(dependencies.size() == 6);
		
		// I should not be able to change the state
		try {
			dependencies.add(new ClassName("a.name.that.should.Fail"));
		} catch (UnsupportedOperationException e) {
			// yay !
			return;
		}
		Assert.fail(); // awww
	}
}
