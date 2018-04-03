/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.nativeimpl.io.csv;

/**
 * Holds the format which should be supported for CSV.
 */
public enum Format {
    /**
     * The format would be similar to RFC4180, however empty lines will be allowed.
     */
    DEFAULT(",", "\\r?\\n", ",", "\n", false),
    /**
     * CSV should conform with RFC4180 specification.
     */
    RFC4180(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", "\\r?\\n", ",", "\n", true),
    /**
     * Tab delimited records.
     */
    TDF("\\t", "\\r?\\n", "\t", "\n", true);

    /**
     * Defines the record separator for the format.
     */
    private String readRecSeparator;
    /**
     * Defines the field separator for the format.
     */
    private String readFieldSeparator;
    /**
     * Defines the record separator which should be used when writing.
     */
    private String writeRecSeparator;
    /**
     * Defines the field separator which should be used when writing.
     */
    private String writeFieldSeparator;
    /**
     * Specifies whether white spaces should be ignored.
     */
    private boolean ignoreSpaces;

    Format(String rfs, String rrs, String wfs, String wrs, boolean ignoreSpaces) {
        this.readFieldSeparator = rfs;
        this.readRecSeparator = rrs;
        this.writeFieldSeparator = wfs;
        this.writeRecSeparator = wrs;
        this.ignoreSpaces = ignoreSpaces;
    }

    public String getReadRecSeparator() {
        return readRecSeparator;
    }

    public String getReadFieldSeparator() {
        return readFieldSeparator;
    }

    public String getWriteRecSeparator() {
        return writeRecSeparator;
    }

    public String getWriteFieldSeparator() {
        return writeFieldSeparator;
    }

    public boolean isIgnoreSpaces() {
        return ignoreSpaces;
    }
}
