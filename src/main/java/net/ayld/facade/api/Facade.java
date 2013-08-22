package net.ayld.facade.api;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

public interface Facade {
	
	JarFile compressDependencies(File sourceDir, File libDir) throws IOException; // XXX terrible name
}
