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
public final class Files { // XXX copy code
	
	private static final String FILE_EXTENSION_DELIMITER = ".";
	
	private final File dir;
	private boolean recursive = true;
	private List<File> result = Lists.newLinkedList();

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
	
	public Files nonRecursive() {
		this.recursive = false;
		return this;
	}
	
	public List<File> list() {
		return inclusive();
	}
	
	public List<File> inclusive() {
		return ImmutableList.copyOf(result);
	}
	
	public Files named(String name) {
		try {
			
			result.addAll(recursive ? namedRecursive(name) : namedNonRecursive(name));
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	public Files withExtention(String ext) {
		try {
			
			result.addAll(recursive ? withExtensionRecursive(ext) : withExtensionNonRecursive(ext));
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	private List<File> namedNonRecursive(final String name) {
		final File[] result = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				final String filename = Tokenizer.delimiter(File.separator).tokenize(pathname.getAbsolutePath()).lastToken();
				final String filenameNoExtension = Tokenizer.delimiter(FILE_EXTENSION_DELIMITER).tokenize(filename).firstToken();
				
				if (filenameNoExtension.equalsIgnoreCase(name)) {
					return true;
				}
				return false;
			}
		});
		return ImmutableList.copyOf(result);
	}
	
	private List<File> namedRecursive(final String name) throws IOException {
		final List<File> result = Lists.newLinkedList();
		
		java.nio.file.Files.walkFileTree(Paths.get(dir.getAbsolutePath()), new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				final String filename = path.getFileName().toString();
				final String filenameNoExtension = Tokenizer.delimiter(FILE_EXTENSION_DELIMITER).tokenize(filename).firstToken();
				
				if (filenameNoExtension.equalsIgnoreCase(name)) {
					result.add(path.toFile());
				}
				
				return FileVisitResult.CONTINUE;
			}
		});
		return ImmutableList.copyOf(result);
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
				
				if (path.getFileName().toString().endsWith(ext)) {
					result.add(path.toFile());
				}
				
				return FileVisitResult.CONTINUE;
			}
		});
		return ImmutableList.copyOf(result);
	}
}
