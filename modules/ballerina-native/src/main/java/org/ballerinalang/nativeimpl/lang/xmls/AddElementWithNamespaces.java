/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 * <p>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.nativeimpl.lang.xmls;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.ballerinalang.bre.Context;
import org.ballerinalang.model.types.TypeEnum;
import org.ballerinalang.model.util.XMLUtils;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.model.values.BXML;
import org.ballerinalang.nativeimpl.lang.utils.ErrorHandler;
import org.ballerinalang.natives.AbstractNativeFunction;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.Attribute;
import org.ballerinalang.natives.annotations.BallerinaAnnotation;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.jaxen.JaxenException;
import org.jaxen.XPathSyntaxException;

import java.util.ArrayList;
import java.util.List;

/**
 * Add a XML element to the location that matches the given xPath.
 * If the xPath matches to the text value of an existing element or
 * If the xPath does not match to an existing element, this operation will
 * have no effect. Namespaces are supported.
 */
@BallerinaFunction(
        packageName = "ballerina.lang.xmls",
        functionName = "addElementWithNamespace",
        args = {@Argument(name = "x", type = TypeEnum.XML),
                @Argument(name = "xPath", type = TypeEnum.STRING),
                @Argument(name = "value", type = TypeEnum.XML),
                @Argument(name = "namespaces", type = TypeEnum.MAP)},
        isPublic = true
)
@BallerinaAnnotation(annotationName = "Description", attributes = {@Attribute(name = "value",
        value = "Adds an XML element to the location that matches the given XPath."
                + " If the XPath matches the text value of an existing element,"
                + " or if the XPath does not match an existing element, this operation will have no effect."
                + " Namespaces are supported.") })
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "x",
        value = "An XML object") })
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "xPath",
        value = "An XPath") })
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "value",
        value = "An XML value") })
@BallerinaAnnotation(annotationName = "Param", attributes = {@Attribute(name = "namespaces",
        value = "A map object consisting of namespaces") })
public class AddElementWithNamespaces extends AbstractNativeFunction {

    private static final String OPERATION = "add element to xml";

    @Override
    public BValue[] execute(Context ctx) {
        try {
            // Accessing Parameters.
            BXML xml = (BXML) getRefArgument(ctx, 0);
            String xPath = getStringArgument(ctx, 0);
            OMElement value = (OMElement) ((BXML) getRefArgument(ctx, 1)).value();
            BMap<String, BString> namespaces = (BMap) getRefArgument(ctx, 2);

            if (value == null) {
                return VOID_RETURN;
            }

            xml = XMLUtils.getSingletonValue(xml);
            
            // Setting the value to XML
            AXIOMXPath axiomxPath = new AXIOMXPath(xPath);
            if (namespaces != null && !namespaces.isEmpty()) {
                for (String entry : namespaces.keySet()) {
                    axiomxPath.addNamespace(entry, namespaces.get(entry).stringValue());
                }
            }

            Object result = axiomxPath.evaluate(xml.value());
            if (result instanceof ArrayList) {
                List<?> macthingElements = (List<?>) result;
                if (macthingElements.isEmpty()) {
                    ErrorHandler.logWarn(OPERATION, "xPath '" + xPath + "' does not match to any element.");
                } else {
                    for (Object element : macthingElements) {
                        if (element instanceof OMText) {
                            ErrorHandler.logWarn(OPERATION, "xPath '" + xPath + "' refers to the text value of an " +
                                    "existing element.");
                        } else if (element instanceof OMDocument) {
                            ErrorHandler.logWarn(OPERATION, "xPath: '" + xPath + "' refers to the Document element, " +
                                    "and already exists.");
                        } else if (element instanceof OMAttribute) {
                            ErrorHandler.logWarn(OPERATION, "xPath '" + xPath + "' refers to an attribute.");
                        } else {
                            OMContainer omContainer = (OMContainer) element;
                            omContainer.addChild(value.cloneOMElement());
                        }
                    }
                }
            }
        } catch (XPathSyntaxException e) {
            ErrorHandler.handleInvalidXPath(OPERATION, e);
        } catch (JaxenException e) {
            ErrorHandler.handleXMLException(OPERATION, e);
        } catch (Throwable e) {
            ErrorHandler.handleXMLException(OPERATION, e);
        }

        return VOID_RETURN;
    }
}
