/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.test.service.grpc.tool;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.protobuf.cmd.GrpcCmd;
import org.ballerinalang.protobuf.cmd.OSDetector;
import org.ballerinalang.protobuf.utils.BalFileGenerationUtils;
import org.ballerinalang.test.util.TestUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Protobuf to bal generation function testcase.
 */
public class ClientStubGeneratorTestCase {

    private static final String PACKAGE_NAME = ".";
    private static String protoExeName = "protoc-" + OSDetector.getDetectedClassifier() + ".exe";
    private Path resourceDir;

    @BeforeClass
    private void setup() throws Exception {
        TestUtils.prepareBalo(this);
        resourceDir = Paths.get(new File(ClientStubGeneratorTestCase.class.getProtectionDomain().getCodeSource()
                .getLocation().toURI().getPath()).getAbsolutePath());
    }

    @Test
    public void testUnaryHelloWorld() throws IllegalAccessException,
            ClassNotFoundException, InstantiationException {
        Class<?> grpcCmd = Class.forName("org.ballerinalang.protobuf.cmd.GrpcCmd");
        GrpcCmd grpcCmd1 = (GrpcCmd) grpcCmd.newInstance();
        Path sourcePath = Paths.get("grpc", "tool");
        Path sourceRoot = resourceDir.resolve(sourcePath);
        Path protoPath = Paths.get("grpc", "tool", "helloWorld.proto");
        Path protoRoot = resourceDir.resolve(protoPath);
        grpcCmd1.setBalOutPath(sourceRoot.toAbsolutePath().toString());
        grpcCmd1.setProtoPath(protoRoot.toAbsolutePath().toString());
        grpcCmd1.execute();
        Path sourceFileRoot = resourceDir.resolve(Paths.get("grpc", "tool", "helloWorld_pb.bal"));
        CompileResult compileResult = BCompileUtil.compile(sourceFileRoot.toString());
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getStructInfo("helloWorldClient"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getStructInfo("helloWorldBlockingClient"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getStructInfo("helloWorldBlockingStub"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getFunctionInfo("helloWorldBlockingStub.hello"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getFunctionInfo("helloWorldStub.hello"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getFunctionInfo("helloWorldBlockingStub.bye"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getFunctionInfo("helloWorldStub.bye"), "Connector not found.");
    }

    @Test
    public void testClientStreamingHelloWorld() throws IllegalAccessException,
            ClassNotFoundException, InstantiationException {
        Class<?> grpcCmd = Class.forName("org.ballerinalang.protobuf.cmd.GrpcCmd");
        GrpcCmd grpcCmd1 = (GrpcCmd) grpcCmd.newInstance();
        Path sourcePath = Paths.get("grpc", "tool");
        Path sourceRoot = resourceDir.resolve(sourcePath);
        Path protoPath = Paths.get("grpc", "tool", "helloWorldClientStreaming.proto");
        Path protoRoot = resourceDir.resolve(protoPath);
        grpcCmd1.setBalOutPath(sourceRoot.toAbsolutePath().toString());
        grpcCmd1.setProtoPath(protoRoot.toAbsolutePath().toString());
        grpcCmd1.execute();
        Path sourceFileRoot = resourceDir.resolve(Paths.get("grpc", "tool", "helloWorldClientStreaming_pb.bal"));
        CompileResult compileResult = BCompileUtil.compile(sourceFileRoot.toString());
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getStructInfo("helloWorldClientStreamingClient"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getStructInfo("helloWorldClientStreamingStub"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                        .getFunctionInfo("helloWorldClientStreamingStub.LotsOfGreetings"),
                "Connector not found.");
    }

    @Test
    public void testServerStreamingHelloWorld() throws IllegalAccessException,
            ClassNotFoundException, InstantiationException {
        Class<?> grpcCmd = Class.forName("org.ballerinalang.protobuf.cmd.GrpcCmd");
        GrpcCmd grpcCmd1 = (GrpcCmd) grpcCmd.newInstance();
        Path sourcePath = Paths.get("grpc", "tool");
        Path sourceRoot = resourceDir.resolve(sourcePath);
        Path protoPath = Paths.get("grpc", "tool", "helloWorldServerStreaming.proto");
        Path protoRoot = resourceDir.resolve(protoPath);
        grpcCmd1.setBalOutPath(sourceRoot.toAbsolutePath().toString());
        grpcCmd1.setProtoPath(protoRoot.toAbsolutePath().toString());
        grpcCmd1.execute();
        Path sourceFileRoot = resourceDir.resolve(Paths.get("grpc", "tool", "helloWorldServerStreaming_pb.bal"));
        CompileResult compileResult = BCompileUtil.compile(sourceFileRoot.toString());
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getStructInfo("helloWorldServerStreamingClient"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getStructInfo("helloWorldServerStreamingStub"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                        .getFunctionInfo("helloWorldServerStreamingStub.lotsOfReplies"),
                "Connector not found.");
    }

    @Test
    public void testStandardDataTypes() throws IllegalAccessException,
            ClassNotFoundException, InstantiationException {
        Class<?> grpcCmd = Class.forName("org.ballerinalang.protobuf.cmd.GrpcCmd");
        GrpcCmd grpcCmd1 = (GrpcCmd) grpcCmd.newInstance();
        Path sourcePath = Paths.get("grpc", "tool");
        Path sourceRoot = resourceDir.resolve(sourcePath);
        Path protoPath = Paths.get("grpc", "tool", "helloWorldString.proto");
        Path protoRoot = resourceDir.resolve(protoPath);
        grpcCmd1.setBalOutPath(sourceRoot.toAbsolutePath().toString());
        grpcCmd1.setProtoPath(protoRoot.toAbsolutePath().toString());
        grpcCmd1.execute();
        Path sourceFileRoot = resourceDir.resolve(Paths.get("grpc", "tool", "helloWorld_pb.bal"));
        CompileResult compileResult = BCompileUtil.compile(sourceFileRoot.toString());
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getStructInfo("helloWorldClient"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getStructInfo("helloWorldBlockingClient"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getStructInfo("helloWorldBlockingStub"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getFunctionInfo("helloWorldBlockingStub.hello"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getFunctionInfo("helloWorldStub.hello"), "Connector not found.");
        Assert.assertNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                        .getFunctionInfo("helloWorldBlockingStub.bye"),
                "function should not exist in pb.bal file.");
        Assert.assertNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                        .getFunctionInfo("helloWorldStub.bye"),
                "function should not exist in pb.bal file.");
    }

    @Test
    public void testDifferentOutputPath() throws IllegalAccessException,
            ClassNotFoundException, InstantiationException {
        Class<?> grpcCmd = Class.forName("org.ballerinalang.protobuf.cmd.GrpcCmd");
        GrpcCmd grpcCmd1 = (GrpcCmd) grpcCmd.newInstance();
        Path sourcePath = Paths.get("grpc", "client");
        Path sourceRoot = resourceDir.resolve(sourcePath);
        Path protoPath = Paths.get("grpc", "tool", "helloWorld.proto");
        Path protoRoot = resourceDir.resolve(protoPath);
        grpcCmd1.setBalOutPath(sourceRoot.toAbsolutePath().toString());
        grpcCmd1.setProtoPath(protoRoot.toAbsolutePath().toString());
        grpcCmd1.execute();
        Path sourceFileRoot = resourceDir.resolve(Paths.get("grpc", "client", "helloWorld_pb.bal"));
        CompileResult compileResult = BCompileUtil.compile(sourceFileRoot.toString());
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getStructInfo("helloWorldClient"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getStructInfo("helloWorldBlockingClient"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getStructInfo("helloWorldBlockingStub"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getFunctionInfo("helloWorldBlockingStub.hello"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getFunctionInfo("helloWorldStub.hello"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getFunctionInfo("helloWorldBlockingStub.bye"), "Connector not found.");
        Assert.assertNotNull(compileResult.getProgFile().getPackageInfo(PACKAGE_NAME)
                .getFunctionInfo("helloWorldStub.bye"), "Connector not found.");
    }

    @AfterClass
    public void clean() {
        BalFileGenerationUtils.delete(new File(protoExeName));
    }

}
