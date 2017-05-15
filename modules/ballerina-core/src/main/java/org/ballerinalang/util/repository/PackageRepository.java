/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.ballerinalang.util.repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@code PackageRepository} represents a repository of ballerina packages.
 *
 * @since 0.8.0
 */
public abstract class PackageRepository {
    protected BuiltinPackageRepository[] builtinPackageRepositories;

    public abstract PackageSource loadPackage(Path packageDirPath);

    public abstract PackageSource loadFile(Path filePath);

    protected PackageSource loadPackageFromDirectory(Path packageDirPath, Path baseDirPath) {
        Map<String, InputStream> fileStreamMap;
        try {
            fileStreamMap = Files.list(baseDirPath.resolve(packageDirPath))
                    .filter(filePath -> filePath.toString().endsWith(".bal"))
                    .collect(Collectors.toMap(filePath -> filePath.getFileName().toString(),
                            this::getInputStream));
        } catch (NoSuchFileException e) {
            throw new RuntimeException("cannot resolve package: " +
                    packageDirPath.toString().replace(File.separator, "."), e);
        } catch (IOException e) {
            throw new RuntimeException("error while resolving package: " +
                    packageDirPath.toString().replace(File.separator, "."), e);
        }

        return new PackageSource(packageDirPath, fileStreamMap, this);
    }

    protected PackageSource loadFileFromDirectory(Path filePath, Path baseDirPath) {
        InputStream inputStream = getInputStream(baseDirPath.resolve(filePath));
        Map<String, InputStream> fileStreamMap = new HashMap<>(1);
        fileStreamMap.put(filePath.getFileName().toString(), inputStream);
        return new PackageSource(Paths.get("."), fileStreamMap, this);
    }

    protected PackageSource loadPackageFromBuiltinRepositories(Path packageDirPath) {
        for (BuiltinPackageRepository pkgRepository : builtinPackageRepositories) {
            PackageSource packageSource = pkgRepository.loadPackage(packageDirPath);
            if (packageSource != null && !packageSource.getSourceFileStreamMap().isEmpty()) {
                return packageSource;
            }
        }

        return null;
    }

    private InputStream getInputStream(Path filePath) {
        try {
            return Files.newInputStream(filePath, StandardOpenOption.READ, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            throw new RuntimeException("error reading from file: " + filePath +
                    " reason: " + e.getMessage(), e);
        }
    }

    /**
     * This class contains data that are required to load packages.
     *
     * @since 0.8.0
     */
    public class PackageSource {
        private Path packagePath;
        private Map<String, InputStream> sourceFileStreamMap;
        private PackageRepository packageRepository;

        public PackageSource(Path packagePath,
                             Map<String, InputStream> sourceFileStreamMap,
                             PackageRepository packageRepository) {
            this.packagePath = packagePath;
            this.sourceFileStreamMap = sourceFileStreamMap;
            this.packageRepository = packageRepository;
        }

        public PackageRepository getPackageRepository() {
            return packageRepository;
        }

        public Path getPackagePath() {
            return packagePath;
        }

        public Map<String, InputStream> getSourceFileStreamMap() {
            return sourceFileStreamMap;
        }
    }
}
