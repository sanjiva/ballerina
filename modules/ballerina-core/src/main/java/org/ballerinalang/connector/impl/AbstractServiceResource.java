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
package org.ballerinalang.connector.impl;

import org.ballerinalang.connector.api.Annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code AbstractServiceResource} This is service resource level abstraction which contains annotations for
 * both services and resources.
 *
 * @since 0.94
 */
public class AbstractServiceResource {

    //key - packagePath:annotationName, value - annotation
    //with below impl however only one annotation can be there with same name for a resource todo
    protected Map<String, List<Annotation>> annotationMap = new HashMap<>();

    protected void addAnnotation(String key, Annotation annotation) {
        annotationMap.computeIfAbsent(key, k -> new ArrayList<>()).add(annotation);
    }


}
