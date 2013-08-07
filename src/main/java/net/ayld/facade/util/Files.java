package net.ayld.facade.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.ayld.facade.util.annotation.NotThreadSafe;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/** 
 * OK since I'm currently too lazy to properly document this class, I'm just going to give usage examples.
 * 
 * Given the following file system tree:
 * 
 *  /root
 *    - anExe.exe
 *    - aDll.dll
 *    - another.dll
 *    /sub
 *      - subExe.exe
 *      - anExe.exe
 *      - subDll.dll
 *      
 *  All {@link Files} methods are recursive by default.
 *  To make them non-recursive just call {@link Files#nonRecursive()} anywhere, like so:
 *  
 *      Files.in("/root").withExtention("dll").nonRecursuve().list();
 *      
 *  So to:
 *  
 *  - list all DLLs in the /root folder recursively:
 *  
 *      <code>Files.in("/root").withExtention("dll").list();</code>
 *      
 *      Returns: ["/root/aDll.dll", "/root/another.dll", "/root/sub/subDll.dll"]
 *      
 *  - list all files named 'anExe' in the /root folder recursively:
 *  
 *      <code>Files.in("/root").named("anExe").list();</code>
 *      
 *      Returns: ["/root/anExe.exe", "/root/sub/anExe.exe"]
 *      
 *  - to list all DLLs in /root AND all files named 'anExe'
 *  
 *      <code>Files.in("/root").withExtention("dll").named("anExe").inclusive();</code>
 *      
 *      Returns: ["/root/anExe.exe", "/root/sub/anExe.exe", "/root/aDll.dll", "/root/another.dll", "/root/sub/subDll.dll"]
 *      
 *      Note: <code>Files.in("/root").withExtention("dll").named("anExe").exclusive();</code>
 *      
 *      Will return nothing since there are no files named 'anExe' with a extension .dll
 *      
 *  - to list all files in /root which are named 'subExe' and have the .exe extension:
 *      
 *      <code>Files.in("/root").withExtention("exe").named("subExe").exclusive();</code>
 *      
 *      Returns: ["/root/anExe.exe", "/root/sub/anExe.exe"]
 *      
 *      
 *  This should be enough for you :)
 *  
 * */
@NotThreadSafe
public final class Files { // XXX this is actually rather procedural ...
	
	private static final String FILE_EXTENSION_DELIMITER = ".";
	
	private final File dir;
	private boolean recursive = true;
	private List<File> result = Lists.newLinkedList();
	
	private String requiredName;
	private String requiredExtension;

	private Files(File dir) {
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
	
	public File single() {
		result = exclusive();
		
		if (result.isEmpty()) {
			throw new IllegalStateException("no files with name: " + requiredName + " and extension: " + requiredExtension + " found in " + dir.getAbsolutePath());
		}
		if (result.size() > 1) {
			throw new IllegalStateException("can not return singular file with given query, currently found: " + result);
		}
		return result.iterator().next();
	}
	
	public List<File> exclusive() {
		final Iterator<File> resultIter = result.iterator();
		
		if (!Strings.isNullOrEmpty(requiredName)) {
			
			while (resultIter.hasNext()) {
				final File file = resultIter.next();
				final String name = file.getName();
				final String nameNoExtension = Tokenizer.delimiter(FILE_EXTENSION_DELIMITER)
						                                .tokenize(name)
						                                .firstToken();
				
				if (!nameNoExtension.equalsIgnoreCase(requiredName)) {
					resultIter.remove();
				}
			}
		}
		if (!Strings.isNullOrEmpty(requiredExtension)) {
			
			while (resultIter.hasNext()) {
				final File file = resultIter.next();
				
				final String extension = Tokenizer.delimiter(FILE_EXTENSION_DELIMITER)
						.tokenize(file.getAbsolutePath())
						.lastToken();
				
				if (!extension.equalsIgnoreCase(requiredExtension)) {
					resultIter.remove();
				}
			}
		}
		return ImmutableList.copyOf(result);
	}
	
	public List<File> inclusive() {
		return ImmutableList.copyOf(result);
	}
	
	public List<File> all() throws IOException {
		if (!Strings.isNullOrEmpty(requiredName) || !Strings.isNullOrEmpty(requiredExtension)) {
			return inclusive();
		}
		
		final int recursionDepth = recursive ? Integer.MAX_VALUE : 1;
		
		java.nio.file.Files.walkFileTree(Paths.get(dir.getAbsolutePath()), Collections.<FileVisitOption>emptySet(), recursionDepth, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				final File file = path.toFile();
				
				if (file.isFile()) {
					result.add(file);
				}
				
				return FileVisitResult.CONTINUE;
			}
		});
		return ImmutableList.copyOf(result);
	}
	
	public Files named(final String name) throws IOException {
		requiredName = name;
		
		final List<File> result = Lists.newLinkedList();
		
		final int recursionDepth = recursive ? Integer.MAX_VALUE : 1;
		
		java.nio.file.Files.walkFileTree(Paths.get(dir.getAbsolutePath()), Collections.<FileVisitOption>emptySet(), recursionDepth, new SimpleFileVisitor<Path>() {

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
		this.result.addAll(ImmutableList.copyOf(result));
		
		return this;
	}
	
	public Files withExtension(final String ext) throws IOException {
		requiredExtension = ext;
		
		final List<File> result = Lists.newLinkedList();
		
		final int recursionDepth = recursive ? Integer.MAX_VALUE : 1;
		
		java.nio.file.Files.walkFileTree(Paths.get(dir.getAbsolutePath()), Collections.<FileVisitOption>emptySet(), recursionDepth, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				
				if (path.getFileName().toString().endsWith(ext)) {
					result.add(path.toFile());
				}
				
				return FileVisitResult.CONTINUE;
			}
		});
		this.result.addAll(ImmutableList.copyOf(result));
		
		return this;
	}
}
