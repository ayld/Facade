package net.ayld.facade.dependency.resolver;

import java.io.IOException;
import java.util.Set;
import java.util.jar.JarFile;

/** 
 * Finds the jar a given dependency belongs to.
 * */
public interface DependencyBundleResolver { // this name really sux ...

	public Set<JarFile> resolve(String qualifiedClassName, Set<JarFile> bundles) throws IOException;
}
