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
package org.ballerinalang.langserver.workspace.repository;

import org.wso2.ballerinalang.compiler.packaging.converters.PathConverter;
import org.wso2.ballerinalang.compiler.packaging.repo.ProjectSourceRepo;

import java.nio.file.Path;

/**
 * Language Server Source Repo.
 */
public class LSProjectSourceRepo extends ProjectSourceRepo {
    public LSProjectSourceRepo(PathConverter converter) {
        super(converter);
    }

    public LSProjectSourceRepo(Path projectRoot) {
        this(new LSPathConverter(projectRoot));
    }
}
