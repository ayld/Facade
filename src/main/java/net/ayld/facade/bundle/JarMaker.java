package net.ayld.facade.bundle;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.jar.JarFile;

public interface JarMaker {
	
	public JarFile zip(Set<File> classFiles) throws IOException;
}
