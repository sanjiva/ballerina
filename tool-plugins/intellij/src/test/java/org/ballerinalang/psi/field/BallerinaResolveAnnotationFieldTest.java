///*
// *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
// *
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// *
// *  http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// */
//
//package org.ballerinalang.psi.field;
//
//import org.ballerinalang.psi.BallerinaResolveTestBase;
//
//import java.io.IOException;
//
///**
// * Test annotation field resolving.
// */
//public class BallerinaResolveAnnotationFieldTest extends BallerinaResolveTestBase {
//
//    private final String annotation = "\npublic annotation A attach function {\n    string /*def*/F;\n}\n";
//
//    @Override
//    protected String getTestDataPath() {
//        return getTestDataPath("psi/resolve/field/annotation");
//    }
//
////    public void testFieldDefinitionInSameFile() {
////        doFileTest();
////    }
////
////    public void testFieldDefinitionInDifferentFile() throws IOException {
////        doFileTest(annotation);
////    }
////
////    public void testFieldDefinitionInDifferentPackage() throws IOException {
////        doFileTest(annotation, "org/test/test.bal");
////    }
//}
