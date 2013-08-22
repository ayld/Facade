package net.ayld.facade.dependency.resolver.impl;

import java.io.IOException;
import java.util.Set;

import net.ayld.facade.dependency.resolver.ClassDependencyResolver;
import net.ayld.facade.model.ClassFile;
import net.ayld.facade.model.ClassName;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.DescendingVisitor;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class ManualBinaryParseClassDependencyResolver implements ClassDependencyResolver{

	@Override
	public Set<ClassName> resolve(ClassFile classFile) throws IOException {
		
		final JavaClass javaClass = new ClassParser(classFile.toString()).parse();
		
		final DependencyVisitor dependencyVisitor = new DependencyVisitor(javaClass);
		final DescendingVisitor classWalker = new DescendingVisitor(javaClass, dependencyVisitor);
		
		classWalker.visit();
		
		return dependencyVisitor.getFoundDependencies();
	}
	
	@Override
	public Set<ClassName> resolve(Set<ClassFile> classFiles) throws IOException {
		final Set<ClassName> result = Sets.newHashSet();
		
		for (ClassFile classFile : classFiles) {
			result.addAll(resolve(classFile));
		}
		
		return result;
	}

	private static class DependencyVisitor extends EmptyVisitor {

		private final JavaClass javaClass;
		private Set<ClassName> foundDependencies = Sets.newHashSet();
		
		private DependencyVisitor(JavaClass javaClass) {
			this.javaClass = javaClass;
		}

		@Override
		public void visitConstantClass(ConstantClass constantClass) {
			final ConstantPool cp = javaClass.getConstantPool();
			
			String dependency = constantClass.getBytes(cp);
			
			// because for some reason BCEL returns dependencies like this:
			//
			// com/something/Class
			//
			// which is odd
			// TODO check if there is a way to return dependencies dot delimited
			dependency = dependency.replaceAll("/", ".");
			
			foundDependencies.add(new ClassName(dependency));
		}

		private Set<ClassName> getFoundDependencies() {
			return ImmutableSet.copyOf(foundDependencies);
		}
	}
}
