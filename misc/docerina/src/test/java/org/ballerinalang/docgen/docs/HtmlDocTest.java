/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.docgen.docs;


import org.ballerinalang.compiler.CompilerPhase;
import org.ballerinalang.docgen.Generator;
import org.ballerinalang.docgen.model.ActionDoc;
import org.ballerinalang.docgen.model.AnnotationDoc;
import org.ballerinalang.docgen.model.EndpointDoc;
import org.ballerinalang.docgen.model.FunctionDoc;
import org.ballerinalang.docgen.model.GlobalVariableDoc;
import org.ballerinalang.docgen.model.Link;
import org.ballerinalang.docgen.model.PackageName;
import org.ballerinalang.docgen.model.Page;
import org.ballerinalang.docgen.model.RecordDoc;
import org.ballerinalang.docgen.model.TypeDefinitionDoc;
import org.ballerinalang.langserver.compiler.LSCompiler;
import org.ballerinalang.langserver.compiler.common.modal.BallerinaFile;
import org.ballerinalang.langserver.compiler.workspace.WorkspaceDocumentManagerImpl;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * HTML document writer test.
 */
public class HtmlDocTest {

    @BeforeClass()
    public void setup() {
    }

    @Test(description = "Multiple packages should be shown even when one page is generated")
    public void testMultiPackage() throws Exception {
        List<Link> packages = new ArrayList<>();
        packages.add(new Link(new PackageName("a.b.c", ""), "", false));
        packages.add(new Link(new PackageName("x.y", ""), "", false));
        packages.add(new Link(new PackageName("x.y.z", ""), "", false));

        BLangPackage bLangPackage = createPackage("");
        Page page = Generator.generatePage(bLangPackage, packages, null, null);

        Assert.assertEquals(page.links.size() , 3);
        Assert.assertFalse(page.links.get(0).active);
//        Assert.assertTrue(page.links.get(1).active);
    }


    @Test(description = "Empty package should give an empty page")
    public void testEmptyPackage() throws Exception {
        BLangPackage bLangPackage = createPackage("");
        Page page = generatePage(bLangPackage);
        Assert.assertTrue(page.constructs.isEmpty());
    }

    @Test(description = "Functions in a package should be shown in the constructs")
    public void testFunctions() throws Exception {
        BLangPackage bLangPackage = createPackage("public function hello(string name) returns (string)" +
                "{return \"a\";}");
        Page page = generatePage(bLangPackage);
        Assert.assertEquals(page.constructs.size(), 1);
        Assert.assertEquals(page.constructs.get(0).name, "hello");
        Assert.assertTrue(page.constructs.get(0) instanceof FunctionDoc, "Invalid documentable type");
        FunctionDoc functionDoc = (FunctionDoc) page.constructs.get(0);
        Assert.assertEquals(functionDoc.parameters.get(0).toString(), "string name", "Invalid parameter string value");
        Assert.assertEquals(functionDoc.returnParams.get(0).toString(), "string", "Invalid return type");
        Assert.assertEquals(functionDoc.returnParams.get(0).href, "primitive-types.html#string", "Invalid link " +
                "to return type");
    }

    @Test(description = "Param links should be generated correctly")
    public void testComplexReturn() throws Exception {
        BLangPackage bLangPackage = createPackage("public function hello(string name) returns ((string[],int) | error)"
                + "{return (\"a\", 2);}");
        Page page = generatePage(bLangPackage);
        Assert.assertEquals(page.constructs.size(), 1);
        Assert.assertEquals(page.constructs.get(0).name, "hello");
        Assert.assertTrue(page.constructs.get(0) instanceof FunctionDoc, "Invalid documentable type");
        FunctionDoc functionDoc = (FunctionDoc) page.constructs.get(0);
        Assert.assertEquals(functionDoc.parameters.get(0).toString(), "string name", "Invalid parameter string value");
        Assert.assertEquals(functionDoc.returnParams.get(0).toString(), "(string[],int) | error", "Invalid return " +
                "type");
        Assert.assertEquals(functionDoc.returnParams.get(0).href, "primitive-types.html#string,primitive-types" + "" +
                ".html#int,builtin.html#error", "Invalid link to return type");
    }

    @Test(description = "Connectors in a package should be shown in the constructs")
    public void testConnectors() throws Exception {
        String source = "@Description {value:\"GitHub client\n" +
                        " connector\"}\n" +
                        "public type TestConnector object {\n" +
                        "    public {\n" +
                        "        \n" +
                        "string url;\n" +
                        "        string path;\n" +
                        "    }\n" +
                        "\n" +
                        "    @Description {value: \n" +
                        "\"Test Connector action testAction.\"}\n" +
                        "    @Return {value:\"whether successful or not\"}\n" +
                        "    public function testAction() returns boolean;\n" +
                        "\n" +
                        "    @Description \n" +
                        "{value: \"Test Connector action testSend.\"}\n" +
                        "    @Param {value: \"ep: endpoint url\"}\n" +
                        "    @Return {value:\"whether successful or not\"}\n" +
                        "    public function testSend(string \n" +
                        "ep) returns boolean;\n" +
                        "};";
        BLangPackage bLangPackage = createPackage(source);
        Page page = generatePage(bLangPackage);
        Assert.assertEquals(page.constructs.size(), 1);
        Assert.assertEquals(page.constructs.get(0).name, "TestConnector");
        Assert.assertEquals(page.constructs.get(0).icon, "fw-struct");
        Assert.assertTrue(page.constructs.get(0) instanceof EndpointDoc, "Invalid documentable type");
        EndpointDoc endpointDoc = (EndpointDoc) page.constructs.get(0);
        Assert.assertEquals(endpointDoc.fields.size(), 2);
        Assert.assertEquals(endpointDoc.fields.get(0).toString(), "string url");
        Assert.assertEquals(endpointDoc.children.size(), 2);
        Assert.assertTrue(endpointDoc.children.get(0) instanceof FunctionDoc, "Invalid documentable type");
        FunctionDoc functionDoc1 = (FunctionDoc) endpointDoc.children.get(0);
        Assert.assertEquals(functionDoc1.name, "testAction", "Invalid function name testAction");
        Assert.assertEquals(functionDoc1.icon, "fw-function", "testAction function is not detected as an action");
        Assert.assertEquals(functionDoc1.parameters.size(), 0);
        Assert.assertEquals(functionDoc1.returnParams.get(0).toString(), "boolean", "Invalid return type");
        Assert.assertEquals(functionDoc1.returnParams.get(0).description, "whether successful or not");

        FunctionDoc functionDoc2 = (FunctionDoc) endpointDoc.children.get(1);
        Assert.assertEquals(functionDoc2.name, "testSend", "Invalid function name testSend");
        Assert.assertEquals(functionDoc2.icon, "fw-function", "testSend function is not detected as an action");
        Assert.assertEquals(functionDoc2.parameters.size(), 1);
        Assert.assertEquals(functionDoc2.parameters.get(0).description, "endpoint url");
        Assert.assertEquals(functionDoc2.returnParams.get(0).toString(), "boolean", "Invalid return type");
        Assert.assertEquals(functionDoc2.returnParams.get(0).description, "whether successful or not");
    }

    @Test(description = "Connectors in a package should be shown in the constructs with new docerina syntax")
    public void testConnectorsWithNewSyntax() throws Exception {
        BLangPackage bLangPackage = createPackage("import ballerina/http;\n public type GitHubClientConfig {\n" + " "
                + "   " + "    " + "http:ClientEndpointConfig clientEndpointConfiguration = {};\n" + "};\n" + "\n" +
                "documentation { " + "GitHub client\n" + "    E{{}}\n" + "    F{{githubClientConfiguration}} - GitHub" +
                " client " + "configurations (Access token, Client endpoint configurations)\n" + "    " +
                "F{{githubConnector}} - " + "GitHub connector object\n" + "}\n" + "public type Client object {\n" + "" +
                "    public {\n" + "        " + "GitHubClientConfig githubClientConfiguration = {};\n" + "        " +
                "TestConnector githubConnector = " + "new;\n" + "    }\n" + "\n" + "    documentation { GitHub client" +
                " endpoint initialization function\n" + "        P{{githubClientConfig}} - GitHub client " +
                "configuration\n" + "    }\n" + "    public " + "function init (GitHubClientConfig " +
                "githubClientConfig);\n" + "\n" + "    documentation { Return the " + "GitHub client\n" + "        " +
                "R{{}} - GitHub client\n" + "    }\n" + "   " +
                "public function getCallerActions ()" + " returns TestConnector;" +
                "\n" + "\n" + "};\n" + "documentation {Test Connector\n F{{url}} url for " + "endpoint\n" +
                "F{{path}} path for endpoint\n" + "}\n" + "public type TestConnector object {\n" + "  " + "  public " +
                "{\n" + "        string url;\n" + "        string path;\n" + "    }\n" + "\n" + "    " +
                "documentation {Test " + "Connector action testAction R{{}} whether successful or not}\n" + "    " +
                "public function " + "testAction() returns boolean;\n" + "\n" + "    documentation {Test Connector "
                + "action testSend P{{ep}}" + " endpoint url R{{}} whether successful or not}\n" + "    public " +
                "function" + " testSend(string ep) " + "returns boolean;\n" + "};\n" +
                "public function TestConnector::testAction() returns boolean {return true;}\n" +
                "public function TestConnector::testSend(string ep) returns boolean {return true;}");
        Page page = generatePage(bLangPackage);
        Assert.assertEquals(page.constructs.size(), 2);
        Assert.assertEquals(page.constructs.get(0).name, "GitHubClientConfig");
        Assert.assertEquals(page.constructs.get(0).icon, "fw-record");
        Assert.assertTrue(page.constructs.get(0) instanceof RecordDoc, "Invalid documentable type");

        Assert.assertEquals(page.constructs.get(1).name, "Client");
        Assert.assertEquals(page.constructs.get(1).icon, "fw-endpoint");
        Assert.assertEquals(page.constructs.get(1).description, "<p>GitHub client</p>\n");
        Assert.assertTrue(page.constructs.get(1) instanceof EndpointDoc, "Invalid documentable type");

        EndpointDoc endpointDoc = (EndpointDoc) page.constructs.get(1);
        Assert.assertEquals(endpointDoc.fields.size(), 2);
        Assert.assertEquals(endpointDoc.fields.get(0).toString(), "string url");
        Assert.assertEquals(endpointDoc.children.size(), 2);
        Assert.assertTrue(endpointDoc.children.get(0) instanceof FunctionDoc, "Invalid documentable type");
        FunctionDoc functionDoc1 = (FunctionDoc) endpointDoc.children.get(0);
        Assert.assertEquals(functionDoc1.name, "testAction", "Invalid function name testAction");
        Assert.assertEquals(functionDoc1.icon, "fw-action", "testAction function is not detected as an action");
        Assert.assertEquals(functionDoc1.parameters.size(), 0);
        Assert.assertEquals(functionDoc1.returnParams.get(0).toString(), "boolean", "Invalid return type");
        Assert.assertEquals(functionDoc1.returnParams.get(0).description, "<p>whether successful or not</p>\n");

        FunctionDoc functionDoc2 = (FunctionDoc) endpointDoc.children.get(1);
        Assert.assertEquals(functionDoc2.name, "testSend", "Invalid function name testSend");
        Assert.assertEquals(functionDoc2.icon, "fw-action", "testSend function is not detected as an action");
        Assert.assertEquals(functionDoc2.parameters.size(), 1);
        Assert.assertEquals(functionDoc2.parameters.get(0).description, "<p>endpoint url</p>\n");
        Assert.assertEquals(functionDoc2.returnParams.get(0).toString(), "boolean", "Invalid return type");
        Assert.assertEquals(functionDoc2.returnParams.get(0).description, "<p>whether successful or not</p>\n");

    }

    @Test(description = "Objects in a package should be shown in the constructs")
    public void testObjects() throws Exception {

        String source = " @Description {value:\"Object Test\"}\n" +
                        "public type Test object {\n" +
                        "    public {\n" +
                        "        string url;\n" +
                        "   \n" +
                        "     string path;\n" +
                        "    }\n" +
                        "\n" +
                        "    @Description {value: \"Test Object function\n" +
                        " test1.\"}\n" +
                        "    @Return {value:\"whether successful or not\"}\n" +
                        "    public \n" +
                        "function test1() returns boolean;\n" +
                        "\n" +
                        "    @Description {value: \"Test Object \n" +
                        "function test2.\"}\n" +
                        "    @Param {value: \"ep: endpoint url\"}\n" +
                        "    @Return \n" +
                        "{value:\"whether successful or not\"}\n" +
                        "    public function test2(string ep) \n" +
                        "returns boolean;\n" +
                        "};";
        BLangPackage bLangPackage = createPackage(source);
        Page page = generatePage(bLangPackage);
        Assert.assertEquals(page.constructs.size(), 1);
        Assert.assertEquals(page.constructs.get(0).name, "Test");
        Assert.assertEquals(page.constructs.get(0).icon, "fw-struct");
        Assert.assertTrue(page.constructs.get(0) instanceof EndpointDoc, "Invalid documentable type");
        EndpointDoc endpointDoc = (EndpointDoc) page.constructs.get(0);
        Assert.assertEquals(endpointDoc.fields.size(), 2);
        Assert.assertEquals(endpointDoc.fields.get(0).toString(), "string url");
        Assert.assertEquals(endpointDoc.children.size(), 2);
        Assert.assertTrue(endpointDoc.children.get(0) instanceof FunctionDoc, "Invalid documentable type");
        FunctionDoc functionDoc1 = (FunctionDoc) endpointDoc.children.get(0);
        Assert.assertEquals(functionDoc1.name, "test1", "Invalid function name. Should be test1");
        Assert.assertEquals(functionDoc1.icon, "fw-function", "test1 function is not detected as a function");
        Assert.assertEquals(functionDoc1.parameters.size(), 0);
        Assert.assertEquals(functionDoc1.returnParams.get(0).toString(), "boolean", "Invalid return type");
        Assert.assertEquals(functionDoc1.returnParams.get(0).description, "whether successful or not");

        FunctionDoc functionDoc2 = (FunctionDoc) endpointDoc.children.get(1);
        Assert.assertEquals(functionDoc2.name, "test2", "Invalid function name test2");
        Assert.assertEquals(functionDoc2.parameters.size(), 1);
        Assert.assertEquals(functionDoc2.icon, "fw-function", "test2 function is not detected as a function");
        Assert.assertEquals(functionDoc2.parameters.get(0).description, "endpoint url");
        Assert.assertEquals(functionDoc2.returnParams.get(0).toString(), "boolean", "Invalid return type");
        Assert.assertEquals(functionDoc2.returnParams.get(0).description, "whether successful or not");
    }

    @Test(description = "Objects in a package should be shown in the constructs with new docerina syntax")
    public void testObjectsWithNewSyntax() throws Exception {
        BLangPackage bLangPackage = createPackage(
                "documentation {\n       Object Test\n\n         Description." +
                        "F{{url}} endpoint url\n" + "F{{path}} a valid path\n" + "}\n" + "public type Test object " +
                        "{\n" + "    public " +
                "{\n" + "        string url;\n" + "        string path;\n" + "    }\n" + "    private {\n" + "       " +
                " string idx;\n" + "    }\n" +
                "documentation {Initialized a new `Test` object\n" +
                "P{{abc}} This is abc\n" +
                "P{{path}} This is path\n" +
                "}\n  public new (string abc =" +
                " \"abc\", path = \"def\") {\n}\n " +
                "documentation {test1 function\n" + "    P{{x}} an integer\n" + " " +
                "   R{{}} is success?\n" + "    }\n" + "    public function test1(int x) returns boolean;\n" + "\n" +
                "    documentation {test1 function\n" + "    R{{}} returns the string or an error\n" + "    }\n" + " " +
                "   public function test2() returns string|error;\n" + "\n" + "    function test3();\n" + "};\n");
        Page page = generatePage(bLangPackage);
        Assert.assertEquals(page.constructs.size(), 1);
        Assert.assertEquals(page.constructs.get(0).name, "Test");
        Assert.assertEquals(page.constructs.get(0).icon, "fw-struct");
        Assert.assertEquals(page.constructs.get(0).description, "<p>Object Test</p>\n<p>Description.</p>\n");
        Assert.assertTrue(page.constructs.get(0) instanceof EndpointDoc, "Invalid documentable type");
        EndpointDoc endpointDoc = (EndpointDoc) page.constructs.get(0);
        Assert.assertEquals(endpointDoc.fields.size(), 2);
        Assert.assertEquals(endpointDoc.fields.get(0).description, "<p>endpoint url</p>\n");
        Assert.assertEquals(endpointDoc.fields.get(1).description, "<p>a valid path</p>\n");
        Assert.assertEquals(endpointDoc.children.size(), 3);
        Assert.assertTrue(endpointDoc.children.get(0) instanceof FunctionDoc, "Invalid documentable type");
        FunctionDoc functionDoc0 = (FunctionDoc) endpointDoc.children.get(0);
        Assert.assertEquals(functionDoc0.name, "new", "Invalid function name. Should be new");
        Assert.assertEquals(functionDoc0.icon, "fw-constructor", "new function is not detected as a constructor");
        Assert.assertEquals(functionDoc0.parameters.size(), 2);
        Assert.assertEquals(functionDoc0.parameters.get(0).description, "<p>This is abc</p>\n");
        Assert.assertEquals(functionDoc0.parameters.get(0).defaultValue, "abc");
        Assert.assertEquals(functionDoc0.parameters.get(1).description, "<p>This is path</p>\n");
        Assert.assertEquals(functionDoc0.parameters.get(1).defaultValue, "def");
        Assert.assertEquals(functionDoc0.returnParams.size(), 0);

        FunctionDoc functionDoc1 = (FunctionDoc) endpointDoc.children.get(1);
        Assert.assertEquals(functionDoc1.name, "test1", "Invalid function name. Should be test1");
        Assert.assertEquals(functionDoc1.icon, "fw-function", "test1 function is not detected as a function");
        Assert.assertEquals(functionDoc1.parameters.size(), 1);
        Assert.assertEquals(functionDoc1.parameters.get(0).description, "<p>an integer</p>\n");
        Assert.assertEquals(functionDoc1.returnParams.get(0).toString(), "boolean", "Invalid return type");
        Assert.assertEquals(functionDoc1.returnParams.get(0).description, "<p>is success?</p>\n");

        FunctionDoc functionDoc2 = (FunctionDoc) endpointDoc.children.get(2);
        Assert.assertEquals(functionDoc2.name, "test2", "Invalid function name test2");
        Assert.assertEquals(functionDoc2.parameters.size(), 0);
        Assert.assertEquals(functionDoc2.icon, "fw-function", "test2 function is not detected as a function");
        Assert.assertEquals(functionDoc2.returnParams.get(0).dataType, "string | error", "Invalid return type");
        Assert.assertEquals(functionDoc2.returnParams.get(0).description, "<p>returns the string or an error</p>\n");
    }

    @Test(description = "Annotation in a package should be shown in the constructs")
    public void testAnnotations() throws Exception {
        BLangPackage bLangPackage = createPackage(" " +
                                                  "public annotation ParameterInfo;" +
                                                  "public annotation ReturnInfo;");
        Page page = generatePage(bLangPackage);
        Assert.assertEquals(page.constructs.size(), 2);
        Assert.assertEquals(page.constructs.get(0).name, "ParameterInfo");
        Assert.assertEquals(page.constructs.get(1).name, "ReturnInfo");
    }
    
    @Test(description = "Annotation in a package should be shown in the constructs")
    public void testGlobalVariables() throws Exception {
        BLangPackage bLangPackage = createPackage("public int total = 98;" +
                                                  "public string content = \"Name\";");
        Page page = generatePage(bLangPackage);
        Assert.assertEquals(page.constructs.size(), 2);
        Assert.assertEquals(page.constructs.get(0).name, "total");
        Assert.assertEquals(page.constructs.get(1).name, "content");
    }

    @Test(description = "Structs in a package should be shown in the constructs")
    public void testStructs() throws Exception {
        BLangPackage bLangPackage = createPackage("public type Message {string message;\n" + "    error?" +
                                                  " cause;};");
        Page page = generatePage(bLangPackage);
        Assert.assertEquals(page.constructs.size(), 1);
        Assert.assertEquals(page.constructs.get(0).name, "Message");
    }

    @Test(description = "One function with a struct bindings in a package should be grouped together shown in the " +
            "constructs", enabled = false)
    public void testFunctionsWithStructBindings() throws Exception {
        BLangPackage bLangPackage = createPackage(" " +
                                                  "public function <Message m>hello(){} " +
                                                  "public struct Message { string message; int id;}");
        Page page = generatePage(bLangPackage);
        Assert.assertEquals(page.constructs.size(), 1);
        Assert.assertEquals(page.constructs.get(0).name, "Message");
        Assert.assertEquals(page.constructs.get(0).children.get(0).name, "hello");
    }

    @Test(description = "One function without a struct bindings in a package should not be grouped together with the" +
            "structs shown in the constructs", enabled = false)
    public void testFunctionsWithoutStructBindings() throws Exception {
        BLangPackage bLangPackage = createPackage(" " +
                                                  "public function hello(){} " +
                                                  "public struct Message { string message; int id;}");
        Page page = generatePage(bLangPackage);
        Assert.assertEquals(page.constructs.size(), 2);
        Assert.assertEquals(page.constructs.get(0).name, "Message");
        Assert.assertEquals(page.constructs.get(1).name, "hello");
    }

    @Test(description = "Functions with struct bindings in a package should be grouped together and functions" +
            "without struct bindings should be isolated as shown in the constructs", enabled = false)
    public void testFunctionsWithWithoutStructBindings() throws Exception {
        BLangPackage bLangPackage = createPackage(" " +
                                                  "public function <Message m>hello(){} " +
                                                  "public struct Message { string message; int id;} " +
                                                  "public function sayBye(){}");

        Page page = generatePage(bLangPackage);
        Assert.assertEquals(page.constructs.size(), 2);
        Assert.assertEquals(page.constructs.get(0).name, "Message");
        Assert.assertEquals(page.constructs.get(0).children.get(0).name, "hello");
        Assert.assertEquals(page.constructs.get(1).name, "sayBye");
    }

    @Test(description = "Function properties should be available via construct")
    public void testFunctionsPropertiesExtracted() throws Exception {
        BLangPackage bLangPackage = createPackage("" +
                "@Description { value:\"This function would say hello\"}" +
                "@Param { value:\"message: The message sent\" }" +
                "@Return { value:\"int representation of the message\" }" +
                "public function sayHello(string message) returns (int){return 1;}");

        FunctionDoc functionDoc = Generator.createDocForNode(bLangPackage.getFunctions().get(0));
        Assert.assertEquals(functionDoc.name, "sayHello", "Function name should be extracted");
        Assert.assertEquals(functionDoc.description, "This function would say hello", "Description of the " +
                "function should be extracted");
        Assert.assertEquals(functionDoc.parameters.get(0).name, "message", "Parameter name should be extracted");
        Assert.assertEquals(functionDoc.parameters.get(0).dataType, "string", "Parameter type should be extracted");
        Assert.assertEquals(functionDoc.parameters.get(0).description, "The message sent", "Description of the " +
                "parameter should be extracted");
        Assert.assertEquals(functionDoc.returnParams.get(0).dataType, "int", "Return parameter type " +
                "should be extracted");
        Assert.assertEquals(functionDoc.returnParams.get(0).description, "int representation of the message",
                "Description of the return parameter should be extracted");

    }

    @Test(description = "Function properties should be available via construct for new docerina syntax")
    public void testFunctionsPropertiesExtractedWithNewSyntax() throws Exception {
        BLangPackage bLangPackage = createPackage("documentation {This function would say hello\n"
                + "P{{message}} The message sent\n" + "P{{idx}} an index\n" + "R{{}} int representation of the " +
                "message\n" + "}\n" + "public function sayHello(string message, int idx) returns (int){return 1;}");

        FunctionDoc functionDoc = Generator.createDocForNode(bLangPackage.getFunctions().get(0));
        Assert.assertEquals(functionDoc.name, "sayHello", "Function name should be extracted");
        Assert.assertEquals(functionDoc.description, "<p>This function would say hello</p>\n", "Description of the " +
                "function should be extracted");
        Assert.assertEquals(functionDoc.parameters.get(0).name, "message", "Parameter name should be extracted");
        Assert.assertEquals(functionDoc.parameters.get(0).dataType, "string", "Parameter type should be extracted");
        Assert.assertEquals(functionDoc.parameters.get(0).description, "<p>The message sent</p>\n", "Description of " +
                "the parameter should be extracted");
        Assert.assertEquals(functionDoc.parameters.get(1).name, "idx", "Parameter name should be extracted");
        Assert.assertEquals(functionDoc.parameters.get(1).dataType, "int", "Parameter type should be extracted");
        Assert.assertEquals(functionDoc.parameters.get(1).description, "<p>an index</p>\n", "Description of" +
                " the parameter should be extracted");
        Assert.assertEquals(functionDoc.returnParams.get(0).dataType, "int", "Return parameter type " + "should be " +
                "extracted");
        Assert.assertEquals(functionDoc.returnParams.get(0).description, "<p>int representation of the message</p>\n",
                "Description of the return parameter should be extracted");

    }

    @Test(description = "Connector properties should be available via construct", enabled = false)
    public void testConnectorPropertiesExtracted() throws Exception {
        String source = " " +
                        "@Description { value:\"Http client connector for outbound HTTP requests\"}\n" +
                        "@Param { value:\"serviceUri: Url of the service\" }\n" +
                        "@Param { value:\"n: connector options\" }" +
                        "connector HttpClient (string serviceUri, int n) {" +
                        "@Description { value:\"The POST action implementation of the HTTP EndpointDoc\"}\n" +
                        "@Param { value:\"path: Resource path \" }\n" +
                        "@Param { value:\"req: An HTTP Request struct\" }\n" +
                        "@Return { value:\"The response message\" }\n" +
                        "@Return { value:\"Error occured during HTTP client invocation\" }\n" +
                        "action post(string path, string req) (string, int) { return \"value within filter\"; }}";
        BLangPackage bLangPackage = createPackage(source);

        EndpointDoc endpointDoc = Generator.createDocForNode(bLangPackage.getObjects().get(0), true);
        Assert.assertEquals(endpointDoc.name, "HttpClient", "Connector name should be extracted");
        Assert.assertEquals(endpointDoc.description, "Http client connector for outbound HTTP requests",
                "Description of the connector should be extracted");
        Assert.assertEquals(endpointDoc.fields.get(0).name, "serviceUri", "Parameter name should be extracted");
        Assert.assertEquals(endpointDoc.fields.get(0).dataType, "string", "Parameter type should be extracted");
        Assert.assertEquals(endpointDoc.fields.get(0).description, "Url of the service",
                "Description of the parameter type should be extracted");

        // For actions inside the connector
        ActionDoc actionDoc = (ActionDoc) endpointDoc.children.get(0);
        Assert.assertEquals(actionDoc.name, "post", "Action name should be extracted");
        Assert.assertEquals(actionDoc.description, "The POST action implementation of the HTTP EndpointDoc",
                "Description of the action should be extracted");
        Assert.assertEquals(actionDoc.parameters.get(0).name, "path", "Parameter name should be extracted");
        Assert.assertEquals(actionDoc.parameters.get(0).dataType, "string", "Parameter type should be extracted");
        Assert.assertEquals(actionDoc.parameters.get(0).description, "Resource path", "Description of the " +
                "parameter should be extracted");
        Assert.assertEquals(actionDoc.returnParams.get(1).dataType, "int", "Return parameter type should be extracted");
        Assert.assertEquals(actionDoc.returnParams.get(1).description, "Error occured during HTTP client invocation",
                "Description of the return parameter should be extracted");
    }

    @Test(description = "Struct properties should be available via construct", enabled = false)
    public void testStructPropertiesExtracted() throws Exception {
        BLangPackage bLangPackage = createPackage(" " +
                                                  "@Description { value:\"Message sent by the client\"}" +
                                                  "@Field {value:\"count: Number of retries\"}\n" +
                                                  "@Field {value:\"interval: Retry interval in millisecond\"}" +
                                                  "struct Message {int interval;int count;}");

        RecordDoc recordDoc = Generator.createDocForNode(bLangPackage.getRecords().get(0));
        Assert.assertEquals(recordDoc.name, "Message", "Struct name should be extracted");
        Assert.assertEquals(recordDoc.description, "Message sent by the client", "Description of the " +
                "struct should be extracted");

        // Struct fields
        Assert.assertEquals(recordDoc.fields.get(0).name, "interval", "Struct field name should be extracted");
        Assert.assertEquals(recordDoc.fields.get(0).dataType, "int", "Struct field type should be extracted");
        Assert.assertEquals(recordDoc.fields.get(0).description, "Retry interval in millisecond",
                "Description of the struct field should be extracted");
    }

    @Test(description = "Enum properties should be available via construct")
    public void testEnumPropertiesExtracted() throws Exception {
        String source = "" + "documentation{ Http operations }" + "public type HttpOperation \"FORWARD\" | \"GET\" | " +
                "" + "" + "\"POST\";";
        BLangPackage bLangPackage = createPackage(source);

        TypeDefinitionDoc typeDefinitionDoc = Generator.createDocForNode(bLangPackage.getTypeDefinitions().get(0));
        Assert.assertEquals(typeDefinitionDoc.name, "HttpOperation", "Type name should be extracted");
        Assert.assertEquals(typeDefinitionDoc.description, "<p>Http operations</p>\n", "Description of the " + "enum " +
                "should be " + "" + "extracted");
        // TODO order gets reversed - needs to fix
        Assert.assertEquals(typeDefinitionDoc.valueSet, "POST | GET | FORWARD", "values should be extracted");
    }

    @Test(description = "Global variables should be available via construct")
    public void testGlobalVariablePropertiesExtracted() throws Exception {
        BLangPackage bLangPackage = createPackage(" " +
                                                  "@Description { value:\"The Read Append access mode\"}\n" +
                                                  "@final\n" +
                                                  "public string RA = \"RA\";");

        GlobalVariableDoc globalVariableDoc = Generator.createDocForNode(bLangPackage.getGlobalVariables().get(0));
        Assert.assertEquals(globalVariableDoc.name, "RA", "Global variable name should be extracted");
        Assert.assertEquals(globalVariableDoc.dataType, "string", "Global variable type should be extracted");
        Assert.assertEquals(globalVariableDoc.description, "The Read Append access mode", "Description of the " +
                "global variable should be extracted");
    }

    @Test(description = "Global variables should be available via construct with new docerina syntax")
    public void testGlobalVariablePropertiesExtractedWithNewSyntax() throws Exception {
        BLangPackage bLangPackage = createPackage("documentation {The Read Append access mode}\n"
                + "@final\n" + "public string RA = \"RA\";");

        GlobalVariableDoc globalVariableDoc = Generator.createDocForNode(bLangPackage.getGlobalVariables().get(0));
        Assert.assertEquals(globalVariableDoc.name, "RA", "Global variable name should be extracted");
        Assert.assertEquals(globalVariableDoc.dataType, "string", "Global variable type should be extracted");
        Assert.assertEquals(globalVariableDoc.description, "<p>The Read Append access mode</p>\n", "Description of " +
                "the global variable should be extracted");
    }

    @Test(description = "Annotation properties should be available via construct", enabled = false)
    public void testAnnotationPropertiesExtracted() throws Exception {
        String source = " " +
                        "@Description {value: \"AnnotationDoc to upgrade connection from HTTP to WS in the " +
                        "same base path\"}\n" +
                        "@Field {value:\"upgradePath:Upgrade path for the WebSocket service from " +
                        "HTTP to WS\"}\n" +
                        "@Field {value:\"serviceName:Name of the WebSocket service where the HTTP service should " +
                        "               upgrade to\"}\n" +
                        "public annotation webSocket attach service<> {\n" +
                        "    string upgradePath;\n" +
                        "    string serviceName;\n" +
                        "}";
        BLangPackage bLangPackage = createPackage(source);

        AnnotationDoc annotationDoc = Generator.createDocForNode(bLangPackage.getAnnotations().get(0));
        Assert.assertEquals(annotationDoc.name, "webSocket", "Annotation name should be extracted");
        Assert.assertEquals(annotationDoc.description, "AnnotationDoc to upgrade connection from HTTP to WS " +
                "in the same base path", "Description of the annotation should be extracted");

        // Annotation Fields
//        Assert.assertEquals(annotationDoc.attributes.get(0).name, "upgradePath", "Annotation attribute name " +
//                "should be extracted");
//        Assert.assertEquals(annotationDoc.attributes.get(0).dataType, "string", "Annotation attribute type " +
//                "should be extracted");
//        Assert.assertEquals(annotationDoc.attributes.get(0).description, "Upgrade path for the WebSocket service " +
//                "from HTTP to WS", "Description of the annotation attribute should be extracted");
    }
    
    @Test(description = "Private constructs should not appear at all.", enabled = false)
    public void testPrivateConstructsInPackage() {
        BLangPackage bLangPackage = createPackage(" " +
                                                  "function hello(){}" +
                                                  "enum Direction { IN,OUT}" +
                                                  "enum Money { USD,LKR}" +
                                                  "annotation ParameterInfo;" +
                                                  "annotation ReturnInfo;" +
                                                  "int total = 98;" +
                                                  "string content = \"Name\";" +
                                                  "struct Message {}" +
                                                  "struct Response {}");
        Page page = generatePage(bLangPackage);
        Assert.assertEquals(page.constructs.size(), 0);
    }
    
//    @Test(description = "Testing primitive constructs.")
//    public void testPrimitiveConstructsWithFunctions() {
//        BLangPackage bLangPackage = createPackage("package ballerina.builtin;" +
//                                                  "public native function <blob b> data (string encoding) returns" +
//                                                  "(string);" +
//                                                  "public native function <blob b> sample () returns (string);");
//        List<Link> packages = new ArrayList<>();
//        packages.add(new Link(new PackageName((bLangPackage.symbol).pkgID.name.value, ""), "", false));
//        packages.add(new Link(new StaticCaption(BallerinaDocConstants.PRIMITIVE_TYPES_PAGE_NAME),
//                BallerinaDocConstants.PRIMITIVE_TYPES_PAGE_HREF, false));
//        Page primitivesPage = Generator.generatePageForPrimitives(bLangPackage, packages);
//        Assert.assertEquals(primitivesPage.constructs.size(), 1);
//        Assert.assertEquals(primitivesPage.constructs.get(0).children.size(), 2);
//    }
    
    @Test(description = "Tests whether default values are collected.", enabled = false)
    public void testStructDefaultValues() {
        BLangPackage bLangPackage = createPackage(" " +
                                                  "public struct Person {" +
                                                  "  string id;" +
                                                  "  string address = \"20,Palm Grove\";" +
                                                  "}");
        Page page = generatePage(bLangPackage);
        Assert.assertEquals(page.constructs.size(), 1);
        Assert.assertTrue(page.constructs.get(0) instanceof RecordDoc, "Documentable of type RecordDoc expected.");
        RecordDoc personRecordDoc = (RecordDoc) page.constructs.get(0);
        Assert.assertEquals(personRecordDoc.fields.size(), 2, "2 fields are expected.");
        Assert.assertEquals(personRecordDoc.fields.get(0).name, "id", "Field \"id\" expected.");
        Assert.assertEquals(personRecordDoc.fields.get(1).name, "address", "Field \"address\" expected.");
        Assert.assertEquals(personRecordDoc.fields.get(1).defaultValue, "20,Palm Grove",
                "Unexpected address value found.");
    }
    
    @Test(description = "Tests whether anonymous structs are documented.", enabled = false)
    public void testAnonymousStructs() {
        BLangPackage bLangPackage = createPackage("@Description { value:\"Represents a person\"}" +
                                                  "@Field {value:\"id: The identification number\"}\n" +
                                                  "@Field {value:\"address: The address of the person.\"}" +
                                                  "public struct Person {" +
                                                  "  string id;" +
                                                  "  struct {" +
                                                  "     string address1;" +
                                                  "     string address2;" +
                                                  "     string state = \"MN\";" +
                                                  "  } address;" +
                                                  "}");
        Page page = generatePage(bLangPackage);
        Assert.assertEquals(page.constructs.size(), 1);
        Assert.assertTrue(page.constructs.get(0) instanceof RecordDoc, "Documentable of type RecordDoc expected.");
        RecordDoc personRecordDoc = (RecordDoc) page.constructs.get(0);
        Assert.assertEquals(personRecordDoc.fields.size(), 2, "2 fields are expected.");
        Assert.assertEquals(personRecordDoc.fields.get(0).name, "id", "Field \"id\" expected.");
        Assert.assertEquals(personRecordDoc.fields.get(1).name, "address", "Field \"address\" expected.");
        Assert.assertEquals(personRecordDoc.fields.get(1).description, "The address of the person.");
        Assert.assertEquals(personRecordDoc.fields.get(1).dataType,
                "struct {string address1, string address2, string state}");
    }

    /**
     * Create the package from the bal file
     * @param source bal file which contains
     * @return BLangPackage
     */
    private BLangPackage createPackage(String source) {
        LSCompiler lsCompiler = new LSCompiler(WorkspaceDocumentManagerImpl.getInstance());
        BallerinaFile ballerinaFile = lsCompiler.compileContent(source, CompilerPhase.TYPE_CHECK);
        if (!ballerinaFile.getDiagnostics().isEmpty()) {
            ballerinaFile.getDiagnostics().stream().forEach(System.err::println);
            throw new IllegalStateException("Compilation errors detected.");
        }
        return ballerinaFile.getBLangPackage();
    }

    /**
     * Generate the api page using the package
     * @param balPackage bal package
     * @return page generated
     */
    private Page generatePage(BLangPackage balPackage) {
        List<Link> packages = new ArrayList<>();
        packages.add(new Link(new PackageName(balPackage.packageID.name.value, ""), "", false));
        return Generator.generatePage(balPackage, packages, null, null);
    }
}
