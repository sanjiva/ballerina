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
package org.ballerinalang.model.elements;

import org.wso2.ballerinalang.compiler.util.Name;
import org.wso2.ballerinalang.compiler.util.Names;
import org.wso2.ballerinalang.util.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This represents a specific package and its version.
 *
 * @since 0.94
 */
public class PackageID {

    public static final PackageID DEFAULT = new PackageID(Names.ANON_ORG, Names.DEFAULT_PACKAGE, Names.DEFAULT_VERSION);
    private final Name orgName;

    public List<Name> nameComps;
    public Name name = Names.DEFAULT_PACKAGE;
    public Name version = Names.DEFAULT_VERSION;

    public PackageID(Name orgName, List<Name> nameComps, Name version) {
        this.orgName = orgName;
        this.nameComps = nameComps;
        this.name = new Name(
                nameComps.stream()
                        .map(Name::getValue)
                        .collect(Collectors.joining(".")));
    }

    public PackageID(Name orgName, Name name, Name version) {
        this.orgName = orgName;
        this.name = name;
        if (name == Names.DEFAULT_PACKAGE) {
            this.nameComps = Lists.of(Names.DEFAULT_PACKAGE);
        } else {
            this.nameComps = Arrays.stream(name.value.split("\\."))
                    .map(Name::new).collect(Collectors.toList());
        }
    }

    public Name getName() {
        return name;
    }

    public Name getNameComp(int index) {
        return nameComps.get(index);
    }

    public List<Name> getNameComps() {
        return nameComps;
    }

    public Name getPackageVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PackageID packageID = (PackageID) o;
        return name.equals(packageID.name) && version.equals(packageID.version);
    }

    @Override
    public int hashCode() {
        int result = nameComps.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }

    @Override
    public String toString() {
        if (version == Names.DEFAULT_VERSION) {
            return this.name.value;
        }

        return this.name + "[" + this.version + "]";
    }

    public Name getOrgName() {
        return orgName;
    }
}
