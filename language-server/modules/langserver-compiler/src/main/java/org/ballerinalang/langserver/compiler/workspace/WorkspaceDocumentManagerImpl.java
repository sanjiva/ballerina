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
package org.ballerinalang.langserver.compiler.workspace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

/**
 * An in-memory document manager that keeps dirty files in-memory and will match the collection of files currently open
 * in tool's workspace.
 */
public class WorkspaceDocumentManagerImpl implements WorkspaceDocumentManager {

    private static final Logger logger = LoggerFactory.getLogger(WorkspaceDocumentManagerImpl.class);

    private volatile Map<Path, WorkspaceDocument> documentList = new ConcurrentHashMap<>();

    private static WorkspaceDocumentManagerImpl instance = new WorkspaceDocumentManagerImpl();

    protected WorkspaceDocumentManagerImpl() {
    }
    
    public static WorkspaceDocumentManagerImpl getInstance() {
        return instance;
    }

    @Override
    public boolean isFileOpen(Path filePath) {
        return filePath != null && documentList.containsKey(filePath);
    }

    @Override
    public void openFile(Path filePath, String content) {
        if (!isFileOpen(filePath)) {
            documentList.put(filePath, new WorkspaceDocument(filePath, content));
        } else {
            logger.warn("File " + filePath.toString() + " already opened in document manager.");
        }
    }

    @Override
    public void updateFile(Path filePath, String updatedContent) {
        if (isFileOpen(filePath)) {
            documentList.get(filePath).setContent(updatedContent);
        } else {
            logger.error("File " + filePath.toString() + " is not opened in document manager.");
        }
    }

    @Override
    public void closeFile(Path filePath) {
        if (isFileOpen(filePath)) {
            documentList.remove(filePath);
        } else {
            logger.error("File " + filePath.toString() + " is not opened in document manager.");
        }
    }

    @Override
    public String getFileContent(Path filePath) {
        return (isFileOpen(filePath) && documentList.get(filePath) != null) ?
                documentList.get(filePath).getContent() : readFromFileSystem(filePath);
    }

    @Override
    public Optional<Lock> lockFile(Path filePath) {
        if (filePath == null) {
            return Optional.empty();
        }
        WorkspaceDocument document = documentList.get(filePath);
        if (document == null) {
            document = new WorkspaceDocument(filePath, "");
            documentList.put(filePath, document);
        }
        Lock lock = document.getLock();
        lock.lock();
        return Optional.of(lock);
    }

    @Override
    public Set<Path> getAllFilePaths() {
        return documentList.keySet();
    }

    private String readFromFileSystem(Path filePath) {
        if (!Files.exists(filePath)) {
            return null;
        }
        try {
            byte[] encoded = Files.readAllBytes(filePath);
            return new String(encoded, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("Error in reading file '" + filePath + "': " + e.getMessage(), e);
        }
    }
}
