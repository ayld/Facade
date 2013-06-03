package net.ayld.facade.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import net.ayld.facade.util.annotation.ThreadSafe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@ThreadSafe
public final class Files {
	
	private final File dir;
	private boolean recursive = true;

	public Files(File dir) {
		this.dir = dir;
	}

	public static Files in(String dir) {
		final File directory = new File(dir);
		
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException(dir + " is not a directory");
		}
		
		return new Files(directory);
	}
	
	public void nonRecursive() {
		this.recursive = false;
	}
	
	public List<File> withExtention(String ext) throws IOException {
		return recursive ? withExtensionRecursive(ext) : withExtensionNonRecursive(ext);
	}
	
	private List<File> withExtensionNonRecursive(final String ext) {
		final File[] result = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				final String filename = Tokenizer.delimiter(File.separator).tokenize(pathname.getAbsolutePath()).lastToken();
				
				if (filename.endsWith(ext)) {
					return true;
				}
				return false;
			}
		});
		return ImmutableList.copyOf(result);
	}
	
	private List<File> withExtensionRecursive(final String ext) throws IOException {
		final List<File> result = Lists.newLinkedList();
		
		java.nio.file.Files.walkFileTree(Paths.get(dir.getAbsolutePath()), new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				
				if (path.getFileName().endsWith(ext)) {
					result.add(path.toFile());
				}
				
				return FileVisitResult.CONTINUE;
			}
		});
		
		return ImmutableList.copyOf(result);
	}
}
