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
package org.ballerinalang.langserver;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.TextDocumentPositionParams;
import org.wso2.ballerinalang.compiler.semantics.model.SymbolTable;
import org.wso2.ballerinalang.compiler.util.CompilerContext;

import java.util.List;

/**
 * Text Document Service context keys for the completion operation context.
 * @since 0.95.5
 */
public class DocumentServiceKeys {
    public static final LanguageServerContext.Key<String> FILE_URI_KEY
            = new LanguageServerContext.Key<>();
    public static final LanguageServerContext.Key<TextDocumentPositionParams> POSITION_KEY
            = new LanguageServerContext.Key<>();
    public static final LanguageServerContext.Key<String> FILE_NAME_KEY
            = new LanguageServerContext.Key<>();
    public static final LanguageServerContext.Key<CompilerContext> COMPILER_CONTEXT_KEY
            = new LanguageServerContext.Key<>();
    public static final LanguageServerContext.Key<ParserRuleContext> PARSER_RULE_CONTEXT_KEY
            = new LanguageServerContext.Key<>();
    public static final LanguageServerContext.Key<TokenStream> TOKEN_STREAM_KEY
            = new LanguageServerContext.Key<>();
    public static final LanguageServerContext.Key<Vocabulary> VOCABULARY_KEY
            = new LanguageServerContext.Key<>();
    public static final LanguageServerContext.Key<Integer> TOKEN_INDEX_KEY
            = new LanguageServerContext.Key<>();
    public static final LanguageServerContext.Key<SymbolTable> SYMBOL_TABLE_KEY
            = new LanguageServerContext.Key<>();
    public static final LanguageServerContext.Key<List<SymbolInformation>> SYMBOL_LIST_KEY
            = new LanguageServerContext.Key<>();
    public static final LanguageServerContext.Key<BLangPackageContext> B_LANG_PACKAGE_CONTEXT_KEY
            = new LanguageServerContext.Key<>();
    public static final LanguageServerContext.Key<String> CURRENT_PACKAGE_NAME_KEY
            = new LanguageServerContext.Key<>();
    public static final LanguageServerContext.Key<TextDocumentServiceContext> OPERATION_META_CONTEXT_KEY
            = new LanguageServerContext.Key<>();
}
