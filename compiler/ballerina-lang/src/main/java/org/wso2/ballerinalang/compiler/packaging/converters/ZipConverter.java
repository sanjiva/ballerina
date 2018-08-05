package org.wso2.ballerinalang.compiler.packaging.converters;

import org.ballerinalang.compiler.BLangCompilerException;
import org.ballerinalang.model.elements.PackageID;
import org.wso2.ballerinalang.compiler.util.Name;
import org.wso2.ballerinalang.compiler.util.Names;
import org.wso2.ballerinalang.compiler.util.ProjectDirConstants;
import org.wso2.ballerinalang.programfile.ProgramFileConstants;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provide functions to convert a patten to a stream of zip paths.
 */
public class ZipConverter extends PathConverter {

    public ZipConverter(Path archivePath) {
        super(resolveIntoArchive(archivePath));
    }

    @Override
    public Path combine(Path path, String pathPart) {
        return resolveIntoArchive(path.resolve(pathPart));
    }

    private static Path resolveIntoArchive(Path newPath) {
        String pathPart = newPath.toString();
        if ((pathPart.endsWith(".zip") || pathPart.endsWith(".jar")) && Files.isRegularFile(newPath)) {
            return pathWithinZip(newPath.toUri());
        } else {
            return newPath;
        }
    }

    private static Path pathWithinZip(URI pathToZip) {
        try {
            URI pathInZip = new URI("jar:" + pathToZip.getScheme(),
                    pathToZip.getUserInfo(), pathToZip.getHost(), pathToZip.getPort(),
                    pathToZip.getPath() + "!/",
                    pathToZip.getQuery(), pathToZip.getFragment());
            initFS(pathInZip);
            return Paths.get(pathInZip);
        } catch (URISyntaxException ignore) {
        }
        return Paths.get(pathToZip);
    }

    private static void initFS(URI uri) {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        try {
            FileSystems.newFileSystem(uri, env);
        } catch (FileSystemAlreadyExistsException ignore) {
        } catch (IOException e) {
            throw new BLangCompilerException("Error loading balo " + uri.getPath(), e);
        }
    }

    @Override
    public Stream<Path> latest(Path path, PackageID packageID) {
        if (Files.isDirectory(path)) {
            try {
                List<Path> pathList = new ArrayList<>();
                if (packageID != null) {
                    String pkgName = packageID.getName().getValue();
                    pathList = Files.list(path)
                                    .map(SortablePath::new)
                                    .filter(SortablePath::valid)
                                    .filter(sortablePath -> validBaloPath(pkgName, sortablePath))
                                    .sorted(Comparator.reverseOrder())
                                    .limit(1)
                                    .map(SortablePath::getPath)
                                    .collect(Collectors.toList());
                    if (packageID.version.value.isEmpty() && !packageID.orgName.equals(Names.BUILTIN_ORG)
                            && !packageID.orgName.equals(Names.ANON_ORG) && pathList.size() > 0) {
                        packageID.version = new Name(pathList.get(0).toFile().getName());
                    }
                }
                return pathList.stream();
            } catch (IOException ignore) {
            }
        }
        return Stream.of();
    }

    /**
     * Validates the package with compiler version.
     *
     * @param pkgName      package name
     * @param sortablePath sortable path
     * @return if the package is compatible with the compiler version
     */
    private boolean validBaloPath(String pkgName, SortablePath sortablePath) {
        Path zipPath = resolveIntoArchive(sortablePath.getPath()
                                                      .resolve(pkgName + ProjectDirConstants.BLANG_COMPILED_PKG_EXT));
        return filterByBaloVersion(zipPath.resolve(ProjectDirConstants.USER_REPO_OBJ_DIRNAME)
                                          .resolve(pkgName + ProjectDirConstants.BLANG_COMPILED_PKG_BINARY_EXT));
    }

    /**
     * Filter by balo version.
     *
     * @param path package path
     * @return if the balo version of the package is compatible with compiler version
     */
    private boolean filterByBaloVersion(Path path) {
        try (InputStream stream = Files.newInputStream(path)) {
            byte[] data = new byte[6];
            if (stream.read(data) != -1) {
                short version = data[data.length - 1];
                return (version >= ProgramFileConstants.MIN_SUPPORTED_VERSION) &&
                        (version <= ProgramFileConstants.MAX_SUPPORTED_VERSION);
            }
        } catch (IOException ignore) {
        }
        return false;
    }
}
