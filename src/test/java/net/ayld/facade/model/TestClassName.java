package net.ayld.facade.model;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class TestClassName {

	private final static Set<Character> INVALID_CLASSNAME_CHARS = ImmutableSet.of(
			'!', '@', '#', '%', '^', '&', '*', '(', ')', '-', '+',
			'{', '}', '[', ']', '"', '\'', '\\', '/', '?', ' ', ',', '|'
	);
	
	@Test
	public void testInvalid() {
		blowUp(".this.should.blow.up");
		blowUp("come.on.baby.light.my.fire.");
		blowUp("you..light.me.up.like.a.dynamite");
		blowUp("the.meaning.is.42");
		for (char invalid : INVALID_CLASSNAME_CHARS) {
			blowUp("fire.works" + invalid);
		}
	}
	
	@Test
	public void testValid() {
		dontBlowUp("as.valid.as.it.gets.yo");
		dontBlowUp("ThisIsStillValidIfITsInTheDefaultPackage");
		dontBlowUp("also.valid.as_hell");
		dontBlowUp("the.meaning.is42");
	}
	
	public void dontBlowUp(String validClassName) {
		// no need for try/catch here, but I really want to add the 'funny' comments so...
		try {
			new ClassName(validClassName);
		} catch (Exception e) {
			// awwwww
			e.printStackTrace();
			Assert.fail();
		}
		// yay !
	}
	
	private static void blowUp(String invalidClassName) {
		try {
			new ClassName(invalidClassName);
		} catch (IllegalArgumentException e) {
			
			// yay !
			return;
		}
		// awwwww
		System.out.println(invalidClassName + ", is valid, but should not be !");
		Assert.fail();
	}
}
