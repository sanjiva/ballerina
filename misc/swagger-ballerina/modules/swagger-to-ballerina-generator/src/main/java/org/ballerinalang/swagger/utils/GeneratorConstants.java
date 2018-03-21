/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ballerinalang.swagger.utils;

import java.io.File;

/**
 * Constants for swagger code generator.
 */
public class GeneratorConstants {


    /**
     * Enum to select the code generation mode.
     * Ballerina service, mock and connector generation is available
     */
    public enum GenType {
        SKELETON("skeleton"), MOCK("mock"), CONNECTOR("connector");

        private String name;

        GenType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static final String SKELETON_TEMPLATE_NAME = "skeleton";
    public static final String CONNECTOR_TEMPLATE_NAME = "connector";
    public static final String MOCK_TEMPLATE_NAME = "mock";
    public static final String MODELS_TEMPLATE_NAME = "models";
    public static final String MODELS_FILE_NAME = "models.bal";

    public static final String TEMPLATES_SUFFIX = ".mustache";
    public static final String TEMPLATES_DIR_PATH_KEY = "templates.dir.path";
    public static final String DEFAULT_TEMPLATE_DIR = File.separator + "templates";
    public static final String DEFAULT_SKELETON_DIR = DEFAULT_TEMPLATE_DIR + File.separator + "skeleton";
    public static final String DEFAULT_MOCK_DIR = DEFAULT_TEMPLATE_DIR + File.separator + "mock";
    public static final String DEFAULT_CONNECTOR_DIR = DEFAULT_TEMPLATE_DIR + File.separator + "connector";

}
