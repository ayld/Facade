package net.ayld.facade.model;

import java.net.URISyntaxException;

import org.junit.Test;

public class TestClassFile {
	
	@Test
	public void testValid() throws URISyntaxException {
		ClassFile.fromClasspath("test-classes/ClassName.class");
	}
}
