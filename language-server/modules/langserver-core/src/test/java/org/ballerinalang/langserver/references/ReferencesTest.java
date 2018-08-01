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
package org.ballerinalang.langserver.references;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ballerinalang.langserver.common.util.CommonUtil;
import org.ballerinalang.langserver.completion.util.FileUtils;
import org.eclipse.lsp4j.Position;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Test suit for testing find all references.
 */
public class ReferencesTest {
    private static final String METHOD = "textDocument/references";
    private Path balPath1 = FileUtils.RES_DIR.resolve("references").resolve("project").resolve("references1.bal");
    private Path balPath2 = FileUtils.RES_DIR.resolve("references").resolve("project").resolve("pkg1")
            .resolve("references2.bal");
    private String balFileContent1;
    private String balFileContent2;

    @BeforeClass
    public void loadLangServer() throws IOException {
        byte[] encoded1 = Files.readAllBytes(balPath1);
        balFileContent1 = new String(encoded1);
        byte[] encoded2 = Files.readAllBytes(balPath2);
        balFileContent2 = new String(encoded2);
    }

    @Test(description = "Test references for function in the same file",
            dataProvider = "referencesForFunctionInSameFile")
    public void referencesForFunctionInSameFile(ReferencesTestDTO referencesTestDTO, Position position)
            throws InterruptedException, IOException {
        String expected = getExpectedValue(referencesTestDTO.getExpectedFileName());
        String actual = CommonUtil.getLanguageServerResponseMessageAsString(position,
                referencesTestDTO.getBallerinaFilePath(), referencesTestDTO.getBallerinaFileContent(), METHOD);
        Assert.assertEquals(actual, expected,
                "Did not match the definition content for " + referencesTestDTO.getExpectedFileName()
                        + " and position line:" + position.getLine() + " character:" + position.getCharacter());
    }


    @Test(description = "Test references for function in the same file",
            dataProvider = "referencesForFunctionInDifferentPkg", enabled = false)
    public void referencesForFunctionInDifferentPkg(ReferencesTestDTO referencesTestDTO, Position position)
            throws InterruptedException, IOException {
        String expected = getExpectedValue(referencesTestDTO.getExpectedFileName());
        String actual = CommonUtil.getLanguageServerResponseMessageAsString(position,
                referencesTestDTO.getBallerinaFilePath(), referencesTestDTO.getBallerinaFileContent(), METHOD);
        Assert.assertEquals(actual, expected,
                "Did not match the definition content for " + referencesTestDTO.getExpectedFileName()
                        + " and position line:" + position.getLine() + " character:" + position.getCharacter());
    }

    @Test(description = "Test references for record in the same file",
            dataProvider = "referencesForRecordInSameFile")
    public void referencesForRecordInSameFile(ReferencesTestDTO referencesTestDTO, Position position)
            throws InterruptedException, IOException {
        String expected = getExpectedValue(referencesTestDTO.getExpectedFileName());
        String actual = CommonUtil.getLanguageServerResponseMessageAsString(position,
                referencesTestDTO.getBallerinaFilePath(), referencesTestDTO.getBallerinaFileContent(), METHOD);
        Assert.assertEquals(actual, expected,
                "Did not match the definition content for " + referencesTestDTO.getExpectedFileName()
                        + " and position line:" + position.getLine() + " character:" + position.getCharacter());
    }

    @Test(description = "Test references for readonly var in the same file",
            dataProvider = "referencesForReadOnlyVarInSameFile")
    public void referencesForReadOnlyVarInSameFile(ReferencesTestDTO referencesTestDTO, Position position)
            throws InterruptedException, IOException {
        String expected = getExpectedValue(referencesTestDTO.getExpectedFileName());
        String actual = CommonUtil.getLanguageServerResponseMessageAsString(position,
                referencesTestDTO.getBallerinaFilePath(), referencesTestDTO.getBallerinaFileContent(), METHOD);
        Assert.assertEquals(actual, expected,
                "Did not match the definition content for " + referencesTestDTO.getExpectedFileName()
                        + " and position line:" + position.getLine() + " character:" + position.getCharacter());
    }

    @DataProvider
    public Object[][] referencesForFunctionInSameFile() {
        return new Object[][]{
                {new ReferencesTestDTO("functionReferencesInSameFile.json", balPath1.toString(), balFileContent1),
                        new Position(15, 12)}
        };
    }

    @DataProvider
    public Object[][] referencesForFunctionInDifferentPkg() {
        return new Object[][]{
                {new ReferencesTestDTO("functionReferencesInDifferentPkg.json", balPath1.toString(),
                        balFileContent1), new Position(16, 33)}
        };
    }

    @DataProvider
    public Object[][] referencesForRecordInSameFile() {
        return new Object[][]{
                {new ReferencesTestDTO("recordReferencesInSameFile.json", balPath1.toString(),
                        balFileContent1), new Position(14, 33)},
                {new ReferencesTestDTO("recordReferencesInSameFile.json", balPath1.toString(),
                        balFileContent1), new Position(18, 8)},
                {new ReferencesTestDTO("recordVarReferencesInSameFile.json", balPath1.toString(),
                        balFileContent1), new Position(23, 26)},
                {new ReferencesTestDTO("recordVarReferencesInSameFile.json", balPath1.toString(),
                        balFileContent1), new Position(24, 15)}
        };
    }

    @DataProvider
    public Object[][] referencesForReadOnlyVarInSameFile() {
        return new Object[][]{
                {new ReferencesTestDTO("readOnlyVarInSameFile.json", balPath2.toString(),
                        balFileContent2), new Position(3, 15)}
        };
    }

    /**
     * Get the expected value from the expected file.
     *
     * @param expectedFile json file which contains expected content.
     * @return string content read from the json file.
     */
    private String getExpectedValue(String expectedFile) throws IOException {
        Path expectedFilePath = FileUtils.RES_DIR.resolve("references").resolve("expected").resolve(expectedFile);
        byte[] expectedByte = Files.readAllBytes(expectedFilePath);
        String balPath1URI = balPath1.toUri().toString();
        String balPath2URI = balPath2.toUri().toString();
        String expectedContent = new String(expectedByte);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(expectedContent).getAsJsonObject();

        JsonArray jsonArray = jsonObject.getAsJsonArray("result");
        for (JsonElement jsonElement : jsonArray) {
            JsonObject location = jsonElement.getAsJsonObject();
            String uri = location.get("uri").toString();
            if (uri.contains("references1.bal")) {
                location.addProperty("uri", balPath1URI);
            } else {
                location.addProperty("uri", balPath2URI);
            }
        }
        return jsonObject.toString();
    }
}
