/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.test.nativeimpl.functions.compression;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.BServiceUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BBlob;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.exceptions.BLangRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Test Native functions in ballerina.compression.
 * @since 0.964
 */
public class CompressionTest {

    private static final Logger log = LoggerFactory.getLogger(CompressionTest.class);
    private CompileResult compileResult;

    @BeforeClass
    public void setup() {
        compileResult = BCompileUtil.compile("test-src/nativeimpl/functions/compression.bal");
    }

    @Test(description = "test unzipping/decompressing a compressed file with src and destination directory path")
    public void testUnzipFile() throws IOException, URISyntaxException {
        String resourceToRead = getAbsoluteFilePath("datafiles/compression/hello.zip");
        BString dirPath = new BString(resourceToRead);
        BString folderToExtract = new BString("");
        String destDirPath = getAbsoluteFilePath("datafiles/compression/");
        BString destDir = new BString(destDirPath);
        BValue[] inputArg = {dirPath, destDir, folderToExtract};
        BRunUtil.invoke(compileResult, "testUnzipFile", inputArg);

        File file = new File(destDirPath + File.separator + "hello.txt");
        Assert.assertEquals(file.exists() && !file.isDirectory(), true);
    }

    @Test(description = "test unzipping/decompressing a compressed file without src directory path",
            expectedExceptions = BLangRuntimeException.class)
    public void testUnzipFileWithoutSrcDirectory() throws IOException, URISyntaxException {
        BString dirPath = new BString(null);
        String destDirPath = getAbsoluteFilePath("datafiles/compression/");
        BString destDir = new BString(destDirPath);
        BString folderToExtract = new BString("");
        BValue[] inputArg = {dirPath, destDir, folderToExtract};
        BValue[] returns = BRunUtil.invoke(compileResult, "testUnzipFile", inputArg);
        Assert.assertNotNull(returns);
        Assert.assertEquals(returns.length, 1);
        Assert.assertNotNull(returns[0]);
        Assert.assertEquals(returns[0].stringValue(), "testUnzipFile");

    }

    @Test(description = "test unzipping/decompressing a compressed file without destination directory path",
            expectedExceptions = BLangRuntimeException.class)
    public void testUnzipFileWithoutDestDirectory() throws IOException, URISyntaxException {
        String resourceToRead = getAbsoluteFilePath("datafiles/compression/hello.zip");
        BString dirPath = new BString(resourceToRead);
        BString destDir = new BString(null);
        BString folderToExtract = new BString("");
        BValue[] inputArg = {dirPath, destDir, folderToExtract};
        BValue[] returns = BRunUtil.invoke(compileResult, "testUnzipFile", inputArg);
        Assert.assertNotNull(returns);
        Assert.assertEquals(returns.length, 1);
        Assert.assertNotNull(returns[0]);
        Assert.assertEquals(returns[0].stringValue(), "testUnzipFile");

    }

    @Test(description = "test zipping/compressing a file")
    public void testZipFile() throws IOException, URISyntaxException {
        String resourceToRead = getAbsoluteFilePath("datafiles/compression/");
        BString dirPath = new BString(resourceToRead);
        String destDirPath = getAbsoluteFilePath("datafiles/compression/");
        BString destDir = new BString(destDirPath + File.separator + "compression.zip");
        BValue[] inputArg = {dirPath, destDir};
        BRunUtil.invoke(compileResult, "testZipFile", inputArg);

        File f1 = new File(destDirPath + File.separator + "compression.zip");
        Assert.assertEquals(f1.exists() && !f1.isDirectory(), true);

        ArrayList<String> filesInsideZip = getFilesInsideZip(destDirPath + File.separator
                + "compression.zip");

        Assert.assertEquals(filesInsideZip.size() > 0, true);

    }

    @Test(description = "test zipping/compressing a file without src directory path",
            expectedExceptions = BLangRuntimeException.class)
    public void testZipFileWithoutSrcDirectory() throws IOException, URISyntaxException {
        BString dirPath = new BString(null);
        String destDirPath = getAbsoluteFilePath("datafiles/compression/");
        BString destDir = new BString(destDirPath + File.separator + "compression.zip");
        BValue[] inputArg = {dirPath, destDir};
        BValue[] returns = BRunUtil.invoke(compileResult, "testZipFile", inputArg);
        Assert.assertNotNull(returns);
        Assert.assertEquals(returns.length, 1);
        Assert.assertNotNull(returns[0]);
        Assert.assertEquals(returns[0].stringValue(), "testZipFile");
    }

    @Test(description = "test zipping/compressing a file without destination directory path",
            expectedExceptions = BLangRuntimeException.class)
    public void testZipFileWithoutDestinationDirectory() throws IOException, URISyntaxException {
        String resourceToRead = getAbsoluteFilePath("datafiles/compression/");
        BString dirPath = new BString(resourceToRead);
        BString destDir = new BString(null);
        BValue[] inputArg = {dirPath, destDir};
        BValue[] returns = BRunUtil.invoke(compileResult, "testZipFile", inputArg);
        Assert.assertNotNull(returns);
        Assert.assertEquals(returns.length, 1);
        Assert.assertNotNull(returns[0]);
        Assert.assertEquals(returns[0].stringValue(), "testZipFile");
    }

    @Test(description = "test unzipping/decompressing a byte array")
    public void testUnzipBytes() throws IOException, URISyntaxException {
        String dirPath = getAbsoluteFilePath("datafiles/compression/");
        byte[] fileContentAsByteArray = Files.readAllBytes(new File(dirPath +
                File.separator + "test.zip").toPath());
        BString destDir = new BString(dirPath);
        BString folderToExtract = new BString("");
        BBlob contentAsByteArray = new BBlob(fileContentAsByteArray);
        BValue[] inputArg = {contentAsByteArray, destDir, folderToExtract};
        BRunUtil.invoke(compileResult, "testUnzipBytes", inputArg);

        File f1 = new File(dirPath + File.separator + "test/");
        Assert.assertEquals(f1.exists() && f1.isDirectory(), true);

        File f2 = new File(dirPath + File.separator + "test/test.txt");
        Assert.assertEquals(f2.exists() && !f2.isDirectory(), true);

    }

    @Test(description = "test unzipping/decompressing a byte array without the content as a blob/byte array",
            expectedExceptions = BLangRuntimeException.class)
    public void testUnzipBytesWithoutBytes() throws IOException, URISyntaxException {
        String dirPath = getAbsoluteFilePath("datafiles/compression/");
        byte[] fileContentAsByteArray = null;
        BString folderToExtract = new BString("");
        BString destDir = new BString(dirPath);
        BBlob contentAsByteArray = new BBlob(fileContentAsByteArray);
        BValue[] inputArg = {contentAsByteArray, destDir, folderToExtract};
        BValue[] returns = BRunUtil.invoke(compileResult, "testUnzipBytes", inputArg);
        Assert.assertNotNull(returns);
        Assert.assertEquals(returns.length, 1);
        Assert.assertNotNull(returns[0]);
        Assert.assertEquals(returns[0].stringValue(), "testUnzipBytes");

    }

    @Test(description = "test unzipping/decompressing a byte array without the destination directory path",
            expectedExceptions = BLangRuntimeException.class)
    public void testUnzipBytesWithoutDestDirectory() throws IOException, URISyntaxException {
        String dirPath = getAbsoluteFilePath("datafiles/compression/");
        byte[] fileContentAsByteArray = Files.readAllBytes(new File(dirPath +
                File.separator + "test.zip").toPath());
        BString destDir = new BString(null);
        BString folderToExtract = new BString("");
        BBlob contentAsByteArray = new BBlob(fileContentAsByteArray);
        BValue[] inputArg = {contentAsByteArray, destDir, folderToExtract};
        BValue[] returns = BRunUtil.invoke(compileResult, "testUnzipBytes", inputArg);
        Assert.assertNotNull(returns);
        Assert.assertEquals(returns.length, 1);
        Assert.assertNotNull(returns[0]);
        Assert.assertEquals(returns[0].stringValue(), "testUnzipBytes");

    }

    @Test(description = "test zipping/compressing a file to a byte array when a destination directory is given")
    public void testZipToBytesWithDestDir() throws IOException, URISyntaxException {
        String dirPath = getAbsoluteFilePath("datafiles/compression/");
        BString dirPathValue = new BString(dirPath + File.separator + "test");
        BValue[] inputArg = {dirPathValue};
        BValue[] returns = BRunUtil.invoke(compileResult, "testZipToBytes", inputArg);

        // Write the byte array to a file
        FileOutputStream fos = new FileOutputStream(dirPath + File.separator + "temp.zip");
        BBlob readBytes = (BBlob) returns[0];
        fos.write(readBytes.blobValue());
        fos.close();
        File file = new File(dirPath + File.separator + "temp.zip");
        // Check if file is written
        Assert.assertEquals(file.exists() && !file.isDirectory(), true);

    }

    @Test(description = "test zipping/compressing a file to a byte array when the destination directory is not given",
            expectedExceptions = BLangRuntimeException.class)
    public void testZipToBytesWithoutDestDir() throws IOException, URISyntaxException {
        BString dirPathValue = new BString(null);
        BValue[] inputArg = {dirPathValue};
        BValue[] returns = BRunUtil.invoke(compileResult, "testZipToBytes", inputArg);
        Assert.assertNotNull(returns);
        Assert.assertEquals(returns.length, 1);
        Assert.assertNotNull(returns[0]);
        Assert.assertEquals(returns[0].stringValue(), "testZipToBytes");

    }

    /**
     * Will identify the absolute path from the relative.
     *
     * @param relativePath the relative file path location.
     * @return the absolute path.
     */
    private String getAbsoluteFilePath(String relativePath) throws URISyntaxException {
        URL fileResource = BServiceUtil.class.getClassLoader().getResource(relativePath);
        String pathValue = "";
        if (null != fileResource) {
            Path path = Paths.get(fileResource.toURI());
            pathValue = path.toAbsolutePath().toString();
        }
        return pathValue;
    }

    /**
     * Get files inside the zip file
     *
     * @param zipFilePath path of the zip file
     * @return list of files available inside the zipped file
     */
    private ArrayList<String> getFilesInsideZip(String zipFilePath) {
        ArrayList<String> filesContained = new ArrayList<>();
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zipFilePath);
        } catch (IOException e) {
            log.debug("I/O Exception when reading the zip file from path ", e);
            log.error("I/O Exception when reading the zip file from path " + e.getMessage());
        }

        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            filesContained.add(name);
        }

        try {
            zipFile.close();
        } catch (IOException e) {
            log.debug("I/O Exception when closing the zip file stream ", e);
            log.error("I/O Exception when closing the zip file stream " + e.getMessage());
        }
        return filesContained;
    }
}
