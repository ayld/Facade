package net.ayld.facade.model;

import java.util.regex.Pattern;

public final class ClassName {

	/** 
	 * This regex only checks plausibility it doesn't check validity. For a validity check a comparison against Java's reserved words must be made.
	 * Also checks whether the name contains only Unicode chars.
	 * 
	 * More info here:
	 *   http://docs.oracle.com/javase/specs/#3.8
	 * 
	 * */
	private final static String CLASS_NAME_VALIDATION_REGEX = "([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*";
	
	private final String qualifiedClassName;

	public ClassName(String qualifiedClassName) {
		if (!isClassName(qualifiedClassName)) {
			throw new IllegalArgumentException("invalid fully qualified class name: " + qualifiedClassName + ", expected: " + CLASS_NAME_VALIDATION_REGEX);
		}
		this.qualifiedClassName = qualifiedClassName;
	}

	private static boolean isClassName(String qualifiedClassName) {
		return Pattern.matches(CLASS_NAME_VALIDATION_REGEX, qualifiedClassName);
	}
	
	@Override
	public String toString() {
		return qualifiedClassName;
	}
}
