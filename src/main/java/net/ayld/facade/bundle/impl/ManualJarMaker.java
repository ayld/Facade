package net.ayld.facade.bundle.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import net.ayld.facade.bundle.JarMaker;

public class ManualJarMaker implements JarMaker{

	private String facadeJarName = "facade.jar";
	
	@Override
	public JarFile zip(Set<File> classFiles) throws IOException {
		makeOutputDir();
		
		final JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(new File(facadeJarName)));
		for (File classFile : classFiles) {
			
			jarOut.putNextEntry(new JarEntry(classFile.getName()));
			
			final FileInputStream in = new FileInputStream(classFile);
			
			int len;
			byte[] buf = new byte[1024];
            while ((len = in.read(buf)) > 0) {
                jarOut.write(buf, 0, len);
            }
            
            jarOut.closeEntry();
            in.close();
		}
		jarOut.close();
		
		return new JarFile(facadeJarName);
	}

	private void makeOutputDir() {
		// ignoring result because it is ok for the parent to exist
		new File(new File(facadeJarName).getParent()).mkdirs();
	}

	public void setZippedJarName(String zippedJarName) {
		this.facadeJarName = zippedJarName;
	}
}
