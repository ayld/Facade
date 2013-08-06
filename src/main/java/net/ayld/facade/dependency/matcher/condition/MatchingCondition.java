package net.ayld.facade.dependency.matcher.condition;

import net.ayld.facade.dependency.matcher.DependencyMatcherStrategy;
import net.ayld.facade.model.ClassFile;
import net.ayld.facade.model.ClassName;

/** 
 * Represents a matching condition.
 * 
 * Matching conditions are governed by a {@link DependencyMatcherStrategy}.
 * 
 * Each condition 'gives it's opinion' on whether a class file matches a class name.
 * The {@link DependencyMatcherStrategy} decides whether the gathered matcher 'opinions' are sufficient.
 *
 * The idea is to have various types of conditions each trying to verify whether a class name matches a class file in different ways.
 * 
 * */
public interface MatchingCondition {
	
	/** 
	 * Checks whether this matching condition is satisfied for the given {@link ClassName}, {@link ClassFile} combination.
	 * 
	 * @param className class name to match
	 * @param classFile class file to match
	 * 
	 * @return true if this condition 'thinks' that the given {@link ClassName} matches the given {@link ClassFile},
	 *         false otherwise
	 * */
	public boolean satisfied(ClassName className, ClassFile classFile);
}
