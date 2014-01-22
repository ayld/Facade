package net.ayld.facade.util;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Set;

/**
 * Created by ayld on 1/10/14 at 12:19 PM.
 */
public class Directories {

    private String name = "";
    private String endsWith = "";
    private boolean recursive;

    private final File in;

    private Directories(File in) {
        this.in = in;
    }

    public static Directories in(String path) {
        final File in = new File(path);

        if (!in.exists() || !in.isDirectory()) {
             throw new IllegalArgumentException(path + " does not exist or is not a directory");
        }

        return new Directories(in);
    }

    public Directories named(String name) {
        this.name = name;
        return this;
    }

    public Directories nameEndsWith(String end) {
        this.endsWith = end;
        return this;
    }

    public void recursive() {
        this.recursive = true;
    }

    public Set<File> list() throws IOException{  // this is a tad messed up really ...
        final Set<File> result = Sets.newHashSet();
        final String root = in.getAbsolutePath();

        Files.walkFileTree(Paths.get(root), Collections.<FileVisitOption>emptySet(), recursive ? Integer.MAX_VALUE : 2, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                final File currentDir = dir.toFile();
                if (!currentDir.canRead()) {
                    return FileVisitResult.CONTINUE;
                }
                if (currentDir.getAbsolutePath().equals(root)) {
                    return FileVisitResult.CONTINUE;
                }

                final String dirName = currentDir.getName();

                if (queryByName()) {
                    if (dirName.equals(name)) {
                        result.add(currentDir);
                    }
                }
                else if (!queryByEndOfName()) {
                    result.add(currentDir);
                }


                if (queryByEndOfName()) {
                    if (dirName.endsWith(endsWith)) {
                        result.add(currentDir);
                    }
                }
                else if (!queryByName()) {
                    result.add(currentDir);
                }

                return FileVisitResult.CONTINUE;
            }
        });
        return result;
    }

    private boolean queryByEndOfName() {
        return !Strings.isNullOrEmpty(endsWith);
    }

    private boolean queryByName() {
        return !Strings.isNullOrEmpty(name);
    }
}
