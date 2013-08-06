package net.ayld.facade.dependency.resolver;

import java.io.IOException;
import java.util.Set;

import net.ayld.facade.model.ClassFile;
import net.ayld.facade.model.ClassName;

/** 
 * (hopefully...) Resolves dependencies of binary class files.
 * */
public interface ClassDependencyResolver {

	public Set<ClassName> resolve(ClassFile classFile) throws IOException;
}
