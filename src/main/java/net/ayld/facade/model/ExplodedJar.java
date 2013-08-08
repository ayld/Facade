package net.ayld.facade.model;

import java.io.File;
import java.util.jar.JarFile;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.hash.Funnel;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.hash.PrimitiveSink;

/** 
 * Represents an extracted (exploded) .jar file.
 * */
public class ExplodedJar {
	
	final String extractedPath;
	final JarFile archive;

	public ExplodedJar(String extractedPath, JarFile archive) {
		if (archive == null) {
			throw new IllegalArgumentException("null archive");
		}
		if (Strings.isNullOrEmpty(extractedPath)) {
			throw new IllegalArgumentException("extracted path is null or empty");
		}
		if (!isPath(extractedPath)) {
			throw new IllegalArgumentException("Directory at: " + extractedPath + ", does not exist or is not a directory");
		}
		this.extractedPath = extractedPath;
		this.archive = archive;
	}

	private static boolean isPath(String toCheck) {
		final File result = new File(toCheck);
		
		return result.exists() && result.isDirectory();
	}
	
	public JarFile getArchive() {
		return archive;
	}

	public String getExtractedPath() {
		return extractedPath;
	}

	@Override
	public int hashCode() {
		final HashFunction hf = Hashing.md5();
		
		return hf.newHasher()
				.putString(extractedPath)
				.putObject(archive, new Funnel<JarFile>() {

					@Override
					public void funnel(JarFile from, PrimitiveSink into) {
						into
							.putString(from.getName())
							.putString(Optional.fromNullable(from.getComment()).or(""));
					}
					private static final long serialVersionUID = 3109141395123855989L;

		}).hash().asInt();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof ExplodedJar)) {
			return false;
		}
		
		final ExplodedJar other = (ExplodedJar) obj;
		
		return 
				other.getArchive().equals(this.archive)
				&& 
				other.getExtractedPath().equals(this.extractedPath);
	}
}
