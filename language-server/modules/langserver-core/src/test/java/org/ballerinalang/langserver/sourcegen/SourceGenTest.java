/*
 * Copyright (c) 2018, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ballerinalang.langserver.sourcegen;

import com.google.gson.JsonObject;
import org.ballerinalang.langserver.SourceGen;
import org.ballerinalang.langserver.compiler.DocumentServiceKeys;
import org.ballerinalang.langserver.compiler.LSServiceOperationContext;
import org.ballerinalang.langserver.compiler.format.TextDocumentFormatUtil;
import org.ballerinalang.langserver.compiler.workspace.WorkspaceDocumentManager;
import org.ballerinalang.langserver.compiler.workspace.WorkspaceDocumentManagerImpl;
import org.ballerinalang.langserver.completion.util.FileUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

/**
 * Source generation test suit.
 */
public class SourceGenTest {
    private Path examples = FileUtils.RES_DIR.resolve("sourceGen");
    private List<File> ballerinaFiles;

    @BeforeClass
    public void loadExampleFiles() throws IOException {
        this.ballerinaFiles = getExampleFiles();
    }

    @Test(description = "Source gen test suit", dataProvider = "exampleFiles")
    public void sourceGenTests(File file) throws IOException, InvocationTargetException, IllegalAccessException {
        LSServiceOperationContext formatContext = new LSServiceOperationContext();
        Path filePath = Paths.get(file.getPath());
        formatContext.put(DocumentServiceKeys.FILE_URI_KEY, filePath.toUri().toString());

        WorkspaceDocumentManager documentManager = WorkspaceDocumentManagerImpl.getInstance();
        byte[] encoded1 = Files.readAllBytes(filePath);
        String expected = new String(encoded1);
        documentManager.openFile(filePath, expected);
        JsonObject ast = TextDocumentFormatUtil.getAST(filePath.toUri().toString(), documentManager,
                formatContext);
        SourceGen sourceGen = new SourceGen(0);
        sourceGen.build(ast.getAsJsonObject("model"), null, "CompilationUnit");
        String actual = sourceGen.getSourceOf(ast.getAsJsonObject("model"), false, false);
        Assert.assertEquals(actual, expected, "Generated source didn't match the expected");
    }

    @DataProvider
    public Object[] exampleFiles() {
        return this.ballerinaFiles.toArray();
    }

    private List<File> getExampleFiles() throws IOException {
        List<File> files = new ArrayList<>();
        FileVisitor fileVisitor = new FileVisitor(files);
        Files.walkFileTree(examples, fileVisitor);
        return files;
    }

    /**
     * File visitor for file walker.
     */
    static class FileVisitor extends SimpleFileVisitor<Path> {
        private List<File> files;
        private String[] ignoredFiles = {"identify_patterns.bal", "identify_trends.bal",
                "join_multiple_streams.bal", "table_queries.bal", "temporal_aggregations_and_windows.bal",
                "table.bal", "csv_io.bal", "grpc_bidirectional_streaming_client.bal"};

        FileVisitor(List<File> ballerinaFiles) {
            this.files = ballerinaFiles;
        }

        @Override
        public FileVisitResult visitFile(Path filePath,
                                         BasicFileAttributes attr) {
            if (attr.isRegularFile()) {
                File file = new File(filePath.toString());
                if (file.getName().endsWith(".bal") && !isIgnoredFile(file.getName())) {
                    this.files.add(file);
                }
            }

            return CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            if (attrs.isSymbolicLink()) {
                return SKIP_SUBTREE;
            }
            return CONTINUE;
        }

        private boolean isIgnoredFile(String fileName) {
            boolean isIgnored = false;
            for (String s : ignoredFiles) {
                if (fileName.endsWith(s)) {
                    isIgnored = true;
                    break;
                }
            }
            return isIgnored;
        }
    }
}
