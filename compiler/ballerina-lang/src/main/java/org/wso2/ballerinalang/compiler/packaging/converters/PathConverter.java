package org.wso2.ballerinalang.compiler.packaging.converters;

import com.sun.nio.zipfs.ZipFileSystem;
import org.ballerinalang.model.elements.PackageID;
import org.ballerinalang.repository.PackageSourceEntry;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

/**
 * Provide functions need to covert a patten to steam of sources.
 */
public class PathConverter implements Converter<Path> {

    private final Path root;

    public PathConverter(Path root) {
        this.root = root;
    }

    private static boolean isBal(Path path, BasicFileAttributes attributes) {
        Path fileName = path.getFileName();
        return attributes.isRegularFile() && fileName != null && fileName.toString().endsWith(".bal");
    }

    @Override
    public Path combine(Path path, String pathPart) {
        return path.resolve(pathPart);
    }

    @Override
    public Stream<Path> expand(Path path) {
        if (Files.isDirectory(path)) {
            try {
                return Files.list(path);
            } catch (IOException ignore) {
            }
        }
        return Stream.of();
    }

    @Override
    public Stream<Path> expandBal(Path path) {
        if (Files.isDirectory(path)) {
            try {
                return Files.find(path, Integer.MAX_VALUE, PathConverter::isBal);
            } catch (IOException ignore) {
            }
        }
        return Stream.of();
    }

    @Override
    public Path start() {
        return root;
    }

    @Override
    public Stream<PackageSourceEntry> finalize(Path path, PackageID id) {
        if (Files.isRegularFile(path)) {
            return Stream.of(new FileSystemSourceEntry(path, id));
        } else {
            return Stream.of();
        }
    }

    @Override
    public String toString() {
        FileSystem fs = root.getFileSystem();
        if (fs instanceof ZipFileSystem) {
            return fs.toString();
        } else {
            return root.toString();
        }
    }
}
