package net.ayld.facade.dependency.resolver;

import java.io.IOException;
import java.util.Set;
import java.util.jar.JarFile;

import net.ayld.facade.model.ClassName;

/** 
 * Finds the jar a given dependency belongs to.
 * */
public interface DependencyBundleResolver { // this name really sux ...

	public Set<JarFile> resolve(ClassName className, Set<JarFile> bundles) throws IOException;
}
