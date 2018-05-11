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
package org.ballerinalang.docgen.model;


import org.apache.commons.lang3.EnumUtils;
import org.wso2.ballerinalang.compiler.tree.BLangObject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Documentable node for Endpoints and Objects.
 */
public class EndpointDoc extends Documentable {
    public final boolean isConnector;
    public final boolean isObject;
    public final boolean hasConstructor;
    public final List<Field> fields;
    private BLangObject object;

    private enum FilteredFunctions { init, register, start, stop, getCallerActions }

    /**
     * Constructor.
     *
     * @param name           endpoint/object name.
     * @param description    description.
     * @param children       endpoint's actions/ object's functions.
     * @param fields         fields of the object.
     * @param isEndpoint    whether an endpoint or an object.
     * @param hasConstructor indicates whether this object has a constructor or not.
     */
    public EndpointDoc(String name, String description, List<Documentable> children, List<Field> fields,
                       List<Documentable> utilityFunctions, boolean isEndpoint, boolean hasConstructor) {
        super(name, "fw-endpoint", description, children);
        if (!isEndpoint) {
            super.icon = "fw-struct";
        }
        if (isEndpoint) {
            for (Documentable doc : children) {
                doc.icon = "fw-action";
            }
        }
        children.addAll(utilityFunctions);
        this.fields = fields;
        this.isConnector = isEndpoint;
        this.isObject = !isEndpoint;
        this.hasConstructor = hasConstructor;

        // filter internal functions
        List<Documentable> filteredChildren = children.stream().filter(f -> {
            if (f instanceof FunctionDoc) {
                FunctionDoc functionDoc = (FunctionDoc) f;
                return isNotAFilteredFunction(functionDoc.name);
            }
            return true;
        }).collect(Collectors.toList());

        children.clear();
        children.addAll(filteredChildren);
    }

    public BLangObject getObject() {
        return object;
    }

    public void setObject(BLangObject object) {
        this.object = object;
    }

    private boolean isNotAFilteredFunction(String name) {
        if (EnumUtils.isValidEnum(FilteredFunctions.class, name)) {
            return false;
        }
        return true;
    }
}
