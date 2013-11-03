package net.ayld.facade.dependency.resolver.impl;

import java.io.IOException;
import java.util.Set;

import net.ayld.facade.component.ListenableComponent;
import net.ayld.facade.dependency.resolver.DependencyResolver;
import net.ayld.facade.event.model.ClassDependencyResolutionEndEvent;
import net.ayld.facade.event.model.ClassDependencyResolutionStartEvent;
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

public class ManualBinaryParseClassDependencyResolver extends ListenableComponent implements DependencyResolver<ClassFile>{

	private static final String BINARY_ARRAY_ID_PREFIX = "[";
	private static final String BINARY_TYPE_PREFIX = "L";
	private static final String BINARY_ARRAY_ID_SUFFIX = ";";
	
	private static final String ARRAY_ID_PREFIX_REGEX = "\\" + BINARY_ARRAY_ID_PREFIX + "+";
	private static final String TYPE_PREFIX_REGEX = BINARY_TYPE_PREFIX;
	
	@Override
	public Set<ClassName> resolve(ClassFile classFile) throws IOException {
		eventBus.post(new ClassDependencyResolutionStartEvent("resolving: " + classFile.physicalFile().getAbsolutePath(), this.getClass()));
		
		final JavaClass javaClass = new ClassParser(classFile.toString()).parse();
		
		final DependencyVisitor dependencyVisitor = new DependencyVisitor(javaClass);
		final DescendingVisitor classWalker = new DescendingVisitor(javaClass, dependencyVisitor);
		
		classWalker.visit();
		
		eventBus.post(new ClassDependencyResolutionEndEvent("resolved: " + dependencyVisitor.getFoundDependencies(), this.getClass()));
		
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
			
			// handle array dependencies
			// if this is an array dependency remove identifiers
			if (dependency.startsWith(BINARY_ARRAY_ID_PREFIX)) {
				dependency = dependency.replaceAll(ARRAY_ID_PREFIX_REGEX, "");
				dependency = dependency.replaceAll(BINARY_ARRAY_ID_SUFFIX, "");
			}
			
			// handle binary type notations
			if (dependency.startsWith(BINARY_TYPE_PREFIX)) {
				dependency = dependency.replaceAll(TYPE_PREFIX_REGEX, "");
			}
			
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
