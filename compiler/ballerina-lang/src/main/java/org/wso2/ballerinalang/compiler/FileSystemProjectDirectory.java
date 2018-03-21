/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.ballerinalang.compiler;

import org.ballerinalang.compiler.BLangCompilerException;
import org.wso2.ballerinalang.compiler.packaging.repo.ProjectSourceRepo;
import org.wso2.ballerinalang.compiler.packaging.repo.Repo;
import org.wso2.ballerinalang.compiler.util.ProjectDirConstants;
import org.wso2.ballerinalang.compiler.util.ProjectDirs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File system based project directory implementation.
 *
 * @since 0.965.0
 */
public class FileSystemProjectDirectory extends FileSystemProgramDirectory {
    private final Path projectDirPath;
    private List<String> packageNames;
    private boolean scanned = false;

    public FileSystemProjectDirectory(Path projectDirPath) {
        super(projectDirPath);
        // TODO This path expect absolute path. This is validated by the SourceDirectoryManager
        this.projectDirPath = projectDirPath;
    }

    @Override
    public boolean canHandle(Path dirPath) {
        Path absDotBallerinaDirPath = dirPath.resolve(ProjectDirConstants.DOT_BALLERINA_DIR_NAME);
        return Files.exists(absDotBallerinaDirPath, LinkOption.NOFOLLOW_LINKS);
    }

    @Override
    public Path getPath() {
        return this.projectDirPath;
    }

    @Override
    public List<String> getSourceFileNames() {
        return super.getSourceFileNames();
    }

    @Override
    public List<String> getSourcePackageNames() {
        if (scanned) {
            return this.packageNames;
        }

        try {
            this.packageNames = Files.list(projectDirPath)
                    .filter(path -> Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))
                    .map(ProjectDirs::getLastComp)
                    .filter(dirName -> !isSpecialDirectory(dirName))
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (SecurityException | AccessDeniedException e) {
            throw new BLangCompilerException("permission denied: " + projectDirPath.toString());
        } catch (IOException e) {
            throw new BLangCompilerException("error reading directory: " + projectDirPath.toString());
        }
        this.scanned = true;
        return this.packageNames;
    }

    private boolean isSpecialDirectory(Path dirName) {
        // TODO Improve this logic in an extensible manner.
        return dirName.toString().equals(ProjectDirConstants.DOT_BALLERINA_DIR_NAME) ||
                dirName.toString().equals(ProjectDirConstants.TARGET_DIR_NAME);
    }

    @Override
    public InputStream getManifestContent() {
        return null;
    }

    @Override
    public InputStream getLockFileContent() {
        return null;
    }

    @Override
    public void saveCompiledProgram(InputStream source, String fileName) {
        Path targetFilePath = ensureAndGetTargetDirPath().resolve(fileName);
        try {
            Files.copy(source, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (DirectoryNotEmptyException e) {
            throw new BLangCompilerException("A directory exists with the same name as the file name '" +
                    targetFilePath.toString() + "'");
        } catch (IOException e) {
            // TODO Improve error messages.
            throw new BLangCompilerException("failed to write the compiled program to '" +
                    targetFilePath.toString() + "'");
        }
    }

    @Override
    public void saveCompiledPackage(InputStream source, String fileName) {
    }

    @Override
    public Repo getPackageRepository() {
        return new ProjectSourceRepo(projectDirPath);
    }


    // private methods

    private void createDirectory(Path targetPath) {
        try {
            Files.createDirectory(targetPath);
        } catch (IOException e) {
            throw new BLangCompilerException("failed create directory '" + targetPath.toString() + "'");
        }
    }

    private Path ensureAndGetTargetDirPath() {
        Path targetPath = this.projectDirPath.resolve(ProjectDirConstants.TARGET_DIR_NAME);
        if (!Files.exists(targetPath)) {
            createDirectory(targetPath);
        }

        return targetPath;
    }
}
