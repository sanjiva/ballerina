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

package org.ballerinalang.swagger;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.helper.StringHelpers;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import io.swagger.oas.models.OpenAPI;
import io.swagger.parser.v3.OpenAPIV3Parser;
import org.ballerinalang.swagger.utils.GeneratorConstants;
import org.ballerinalang.swagger.utils.GeneratorConstants.GenType;
import org.ballerinalang.swagger.utils.OpenApiWrapper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>This class generates Ballerina Services/Connectors for a provided OAS definition.</p>
 */
public class CodeGenerator {
    private String apiPackage;

    /**
     * Generates ballerina source for provided Open API Definition in <code>definitionPath</code>
     * <p>Method can be user for generating Ballerina service skeletons, mock services and connectors</p>
     *
     * @param type           Output type. Following types are supported
     *                       <ul>
     *                       <li>skeleton</li>
     *                       <li>mock</li>
     *                       <li>connector</li>
     *                       </ul>
     * @param definitionPath Input Open Api Definition file path
     * @param outPath        Destination file path to save generated source files. If not provided
     *                       <code>destinationPath</code> will be used as the default destination path
     * @throws IOException when file operations fail
     */
    public void generate(GenType type, String definitionPath, String outPath) throws IOException {

        OpenAPI api = new OpenAPIV3Parser().read(definitionPath);
        OpenApiWrapper context = new OpenApiWrapper().buildFromOpenAPI(api).apiPackage(apiPackage);

        // Write output to the input definition location if no destination directory is provided
        if (outPath == null || outPath.isEmpty()) {
            String fileName = api.getInfo().getTitle().replaceAll(" ", "") + ".bal";
            outPath = definitionPath.substring(0, definitionPath.lastIndexOf(File.separator) + 1);
            outPath += fileName;
        }
        switch (type) {
            case SKELETON:
                writeBallerina(context, GeneratorConstants.DEFAULT_SKELETON_DIR,
                        GeneratorConstants.SKELETON_TEMPLATE_NAME, outPath);
                break;
            case CONNECTOR:
                writeBallerina(context, GeneratorConstants.DEFAULT_CONNECTOR_DIR,
                        GeneratorConstants.CONNECTOR_TEMPLATE_NAME, outPath);
                break;
            case MOCK:
                writeBallerina(context, GeneratorConstants.DEFAULT_MOCK_DIR, GeneratorConstants.MOCK_TEMPLATE_NAME,
                        outPath);
                break;
            default:
                return;
        }
    }

    /**
     * Write ballerina definition of a <code>object</code> to a file as described by <code>template.</code>
     *
     * @param object       Context object to be used by the template parser
     * @param templateDir  Directory with all the templates required for generating the source file
     * @param templateName Name of the parent template to be used
     * @param outPath      Destination path for writing the resulting source file
     * @throws IOException when file operations fail
     */
    public void writeBallerina(Object object, String templateDir, String templateName, String outPath)
            throws IOException {
        PrintWriter writer = null;

        try {
            Template template = compileTemplate(templateDir, templateName);
            Context context = Context.newBuilder(object).resolver(FieldValueResolver.INSTANCE).build();
            writer = new PrintWriter(outPath, "UTF-8");
            writer.println(template.apply(context));
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private Template compileTemplate(String defaultTemplateDir, String templateName) throws IOException {
        String templatesDirPath = System.getProperty(GeneratorConstants.TEMPLATES_DIR_PATH_KEY, defaultTemplateDir);
        ClassPathTemplateLoader cpTemplateLoader = new ClassPathTemplateLoader((templatesDirPath));
        FileTemplateLoader fileTemplateLoader = new FileTemplateLoader(templatesDirPath);
        cpTemplateLoader.setSuffix(GeneratorConstants.TEMPLATES_SUFFIX);
        fileTemplateLoader.setSuffix(GeneratorConstants.TEMPLATES_SUFFIX);
        
        Handlebars handlebars = new Handlebars().with(cpTemplateLoader, fileTemplateLoader);
        handlebars.registerHelpers(StringHelpers.class);
        handlebars.registerHelper("equals", (object, options) -> {
            CharSequence result;
            Object param0 = options.param(0);

            if (param0 == null) {
                throw new IllegalArgumentException("found 'null', expected 'string'");
            }
            if (object != null && object.toString().equals(param0.toString())) {
                result = options.fn(options.context);
            } else {
                result = null;
            }

            return result;
        });

        return handlebars.compile(templateName);
    }

    public String getApiPackage() {
        return apiPackage;
    }

    public void setApiPackage(String apiPackage) {
        this.apiPackage = apiPackage;
    }
}
