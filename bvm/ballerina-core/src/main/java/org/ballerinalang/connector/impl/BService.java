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
import org.ballerinalang.connector.api.Resource;
import org.ballerinalang.connector.api.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code BService} This is the implementation for the {@code Service} API.
 *
 * @since 0.94
 */
public class BService extends AbstractServiceResource implements Service {
    private String name;
    private String packagePath;
    private String protocolPackage;

    //key - resourceName, value - resource
    private Map<String, Resource> resourceMap = new HashMap<>();

    public BService(String name, String packagePath, String protocolPackage) {
        this.name = name;
        this.packagePath = packagePath;
        this.protocolPackage = protocolPackage;
    }

    public void addResource(String name, Resource resource) {
        resourceMap.put(name, resource);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPackage() {
        return packagePath;
    }

    @Override
    public String getProtocolPackage() {
        return protocolPackage;
    }

    @Override
    public List<Annotation> getAnnotationList(String pkgPath, String name) {
        String key = pkgPath + ":" + name;
        return annotationMap.get(key);
    }

    @Override
    public Resource[] getResources() {
        return resourceMap.values().toArray(new Resource[0]);
    }
}
