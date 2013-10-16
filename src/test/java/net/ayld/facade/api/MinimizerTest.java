package net.ayld.facade.api;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.jar.JarFile;

import junit.framework.Assert;
import net.ayld.facade.util.Settings;
import net.ayld.facade.util.Tokenizer;

import org.junit.Test;

import com.google.common.io.Resources;

public class MinimizerTest {
	
	@Test
	public void minimize() throws IOException {
		final JarFile outJar = LibMinimizer
			.forSourcesAt(toPath(Resources.getResource("test-classes/test-src-dir")))
			.withLibs(toPath(Resources.getResource("test-classes/test-lib-dir")))
			.getFile();
		
		Assert.assertTrue(outJar != null);
		
		final String outJarName = Tokenizer.delimiter(File.separator).tokenize(outJar.getName()).lastToken();
		
		Assert.assertTrue(outJarName.equals(Settings.DEFAULT_FACADE_JAR_NAME.getValue()));
	}
	
	public String toPath(URL uri) {
		return Tokenizer.delimiter(":").tokenize(uri.toString()).lastToken();
	}
}
