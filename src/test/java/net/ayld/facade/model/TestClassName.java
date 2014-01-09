package net.ayld.facade.model;

import java.util.Set;

import junit.framework.Assert;

import net.ayld.facade.bundle.impl.ManualJarExploder;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

public class TestClassName {
	
	private static final Logger LOG = LoggerFactory.getLogger(ManualJarExploder.class);

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
			LOG.error("awwwww",e);
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
		LOG.error(invalidClassName + ", is valid, but should not be !");
		Assert.fail();
	}
}
