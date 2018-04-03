package org.wso2.ballerinalang.compiler.packaging.converters;

import org.ballerinalang.model.elements.PackageID;
import org.ballerinalang.repository.PackageSourceEntry;
import org.ballerinalang.spi.EmbeddedExecutor;
import org.ballerinalang.util.EmbeddedExecutorProvider;
import org.wso2.ballerinalang.compiler.packaging.Patten;
import org.wso2.ballerinalang.util.HomeRepoUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Provide functions need to covert a patten to steam of by paths, by downloading them as url .
 */
public class URIConverter implements Converter<URI> {

    private final URI base;

    public URIConverter(URI base) {
        this.base = base;
    }

    /**
     * Create the dir path provided.
     *
     * @param dirPath destination dir path
     */
    public void createDirectory(Path dirPath) {
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new RuntimeException("Error occured when creating the directory path " + dirPath);
            }
        }
    }

    @Override
    public URI start() {
        return base;
    }

    @Override
    public URI combine(URI s, String p) {
        return s.resolve(p + '/');
    }

    @Override
    public Stream<URI> expand(URI u) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<URI> expandBal(URI u) {
        throw new UnsupportedOperationException();

    }

    public Stream<PackageSourceEntry> finalize(URI u, PackageID packageID) {
        String orgName = packageID.getOrgName().getValue();
        String pkgName = packageID.getName().getValue();
        Path destDirPath = HomeRepoUtils.createAndGetHomeReposPath().resolve(Paths.get("repo", orgName, pkgName));
        createDirectory(destDirPath);
        try {
            String fullPkgPath = orgName + "/" + pkgName;
            EmbeddedExecutor executor = EmbeddedExecutorProvider.getInstance().getExecutor();
            executor.execute("packaging.pull/ballerina.pull.balx", u.toString(), destDirPath.toString(),
                             fullPkgPath, File.separator);
            // TODO Simplify using ZipRepo
            Patten pattern = new Patten(Patten.WILDCARD_DIR,
                                        Patten.path(pkgName + ".zip"),
                                        Patten.path("src"), Patten.WILDCARD_SOURCE);
            return pattern.convertToSources(new ZipConverter(destDirPath), packageID);
        } catch (Exception ignore) {
        }
        return Stream.of();
    }

    @Override
    public String toString() {
        return base.toString();
    }

}
