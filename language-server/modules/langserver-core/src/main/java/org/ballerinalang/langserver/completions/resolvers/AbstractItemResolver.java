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
package org.ballerinalang.langserver.completions.resolvers;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.ballerinalang.langserver.common.UtilSymbolKeys;
import org.ballerinalang.langserver.common.utils.CommonUtil;
import org.ballerinalang.langserver.compiler.DocumentServiceKeys;
import org.ballerinalang.langserver.compiler.LSServiceOperationContext;
import org.ballerinalang.langserver.completions.CompletionKeys;
import org.ballerinalang.langserver.completions.SymbolInfo;
import org.ballerinalang.langserver.completions.util.CompletionUtil;
import org.ballerinalang.langserver.completions.util.ItemResolverConstants;
import org.ballerinalang.langserver.completions.util.Snippet;
import org.ballerinalang.langserver.completions.util.filters.ConnectorInitExpressionItemFilter;
import org.ballerinalang.model.symbols.SymbolKind;
import org.ballerinalang.model.types.TypeConstants;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.Position;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BAnnotationSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BInvokableSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BPackageSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BServiceSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BTypeSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BVarSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.types.BArrayType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BInvokableType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BNilType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BUnionType;
import org.wso2.ballerinalang.compiler.util.Name;
import org.wso2.ballerinalang.compiler.util.Names;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interface for completion item resolvers.
 */
public abstract class AbstractItemResolver {
    
    public abstract ArrayList<CompletionItem> resolveItems(LSServiceOperationContext completionContext);

    /**
     * Populate the completion item list by considering the.
     * @param symbolInfoList - list of symbol information
     * @param completionItems - completion item list to populate
     */
    protected void populateCompletionItemList(List<SymbolInfo> symbolInfoList, List<CompletionItem> completionItems) {

        symbolInfoList.forEach(symbolInfo -> {
            CompletionItem completionItem = null;
            BSymbol bSymbol = symbolInfo.getScopeEntry() != null ? symbolInfo.getScopeEntry().symbol : null;
            if (!(bSymbol != null && bSymbol.getName().getValue().startsWith("$"))) {
                if ((bSymbol instanceof BInvokableSymbol
                        && SymbolKind.FUNCTION.equals(((BInvokableSymbol) bSymbol).kind))
                        || symbolInfo.isIterableOperation()) {
                    completionItem = this.populateBallerinaFunctionCompletionItem(symbolInfo);
                } else if (!(bSymbol instanceof BInvokableSymbol)
                        && bSymbol instanceof BVarSymbol && !"_".equals(bSymbol.name.getValue())) {
                    completionItem = this.populateVariableDefCompletionItem(symbolInfo);
                } else if (bSymbol instanceof BTypeSymbol
                        && !bSymbol.getName().getValue().equals(UtilSymbolKeys.NOT_FOUND_TYPE)
                        && !(bSymbol instanceof BAnnotationSymbol)
                        && !(bSymbol.getName().getValue().equals("runtime"))
                        && !(bSymbol instanceof BServiceSymbol)) {
                    completionItem = this.populateBTypeCompletionItem(symbolInfo);
                }
            }

            if (completionItem != null) {
                completionItems.add(completionItem);
            }
        });
    }

    /**
     * Populate the Ballerina Function Completion Item.
     * @param symbolInfo - symbol information
     * @return completion item
     */
    private CompletionItem populateBallerinaFunctionCompletionItem(SymbolInfo symbolInfo) {
        String insertText;
        String label;
        CompletionItem completionItem = new CompletionItem();
        
        if (symbolInfo.isIterableOperation()) {
            insertText = symbolInfo.getIterableOperationSignature().getInsertText();
            label = symbolInfo.getIterableOperationSignature().getLabel();
        } else {
            BSymbol bSymbol = symbolInfo.getScopeEntry().symbol;
            if (!(bSymbol instanceof BInvokableSymbol)) {
                return null;
            }
            BInvokableSymbol bInvokableSymbol = (BInvokableSymbol) bSymbol;
            if (bInvokableSymbol.getName().getValue().contains("<")
                    || bInvokableSymbol.getName().getValue().contains("<") ||
                    bInvokableSymbol.getName().getValue().equals("main")) {
                return null;
            }
            FunctionSignature functionSignature = getFunctionSignature(bInvokableSymbol);
            
            insertText = functionSignature.getInsertText();
            label = functionSignature.getLabel();
        }
        completionItem.setInsertTextFormat(InsertTextFormat.Snippet);
        completionItem.setLabel(label);
        completionItem.setInsertText(insertText);
        completionItem.setDetail(ItemResolverConstants.FUNCTION_TYPE);
        completionItem.setKind(CompletionItemKind.Function);

        return completionItem;
    }

    /**
     * Populate the Variable Definition Completion Item.
     * @param symbolInfo - symbol information
     * @return completion item
     */
    private CompletionItem populateVariableDefCompletionItem(SymbolInfo symbolInfo) {
        CompletionItem completionItem = new CompletionItem();
        completionItem.setLabel(symbolInfo.getSymbolName());
        String[] delimiterSeparatedTokens = (symbolInfo.getSymbolName()).split("\\.");
        completionItem.setInsertText(delimiterSeparatedTokens[delimiterSeparatedTokens.length - 1]);
        String typeName = symbolInfo.getScopeEntry().symbol.type.toString();
        completionItem.setDetail((typeName.equals("")) ? ItemResolverConstants.NONE : typeName);
        completionItem.setKind(CompletionItemKind.Unit);

        return completionItem;
    }

    /**
     * Populate the BType Completion Item.
     * @param symbolInfo - symbol information
     * @return completion item
     */
    public CompletionItem populateBTypeCompletionItem(SymbolInfo symbolInfo) {
        CompletionItem completionItem = new CompletionItem();
        completionItem.setLabel(symbolInfo.getSymbolName());
        String[] delimiterSeparatedTokens = (symbolInfo.getSymbolName()).split("\\.");
        completionItem.setInsertText(delimiterSeparatedTokens[delimiterSeparatedTokens.length - 1]);
        completionItem.setDetail(ItemResolverConstants.B_TYPE);

        return completionItem;
    }

    /**
     * Populate the Namespace declaration Completion Item.
     * @param symbolInfo - symbol information
     * @return completion item
     */
    CompletionItem populateNamespaceDeclarationCompletionItem(SymbolInfo symbolInfo) {
        CompletionItem completionItem = new CompletionItem();
        completionItem.setLabel(symbolInfo.getSymbolName());
        String[] delimiterSeparatedTokens = (symbolInfo.getSymbolName()).split("\\.");
        completionItem.setInsertText(delimiterSeparatedTokens[delimiterSeparatedTokens.length - 1]);
        completionItem.setDetail(ItemResolverConstants.XMLNS);

        return completionItem;
    }

    /**
     * Get the function signature.
     * @param bInvokableSymbol - ballerina function instance
     * @return {@link String}
     */
    private FunctionSignature getFunctionSignature(BInvokableSymbol bInvokableSymbol) {
        String[] funcNameComponents = bInvokableSymbol.getName().getValue().split("\\.");
        String functionName = funcNameComponents[funcNameComponents.length - 1];

        // If there is a receiver symbol, then the name comes with the package name and struct name appended.
        // Hence we need to remove it
        if (bInvokableSymbol.receiverSymbol != null) {
            String receiverType = bInvokableSymbol.receiverSymbol.getType().toString();
            functionName = functionName.replace(receiverType + ".", "");
        }
        StringBuilder signature = new StringBuilder(functionName + "(");
        StringBuilder insertText = new StringBuilder(functionName + "(");
        List<BVarSymbol> parameterDefs = bInvokableSymbol.getParameters();
        List<BVarSymbol> defaultParameterDefs = bInvokableSymbol.getDefaultableParameters();

        for (int itr = 0; itr < parameterDefs.size(); itr++) {
            signature.append(this.getParameterSignature(parameterDefs.get(itr), false));
            insertText.append(this.getParameterInsertText(parameterDefs.get(itr), false, itr + 1));
            
            if (!(itr == parameterDefs.size() - 1 && defaultParameterDefs.isEmpty())) {
                signature.append(", ");
                insertText.append(", ");
            }
        }

        for (int itr = 0; itr < defaultParameterDefs.size(); itr++) {
            signature.append(this.getParameterSignature(defaultParameterDefs.get(itr), true));
            insertText.append(this.getParameterInsertText(defaultParameterDefs.get(itr), true,
                    defaultParameterDefs.size() + itr + 1));

            if (itr < defaultParameterDefs.size() - 1) {
                signature.append(", ");
                insertText.append(", ");
            }
        }
        signature.append(")");
        insertText.append(")");
        String initString = "(";
        String endString = ")";

        BType returnType = bInvokableSymbol.type.getReturnType();
        if (returnType != null && !(returnType instanceof BNilType)) {
            signature.append(initString).append(returnType.toString());
            signature.append(endString);
        }
        return new FunctionSignature(insertText.toString(), signature.toString());
    }
    
    private String getTypeName(BVarSymbol bVarSymbol) {
        BType paramType = bVarSymbol.getType();
        String typeName;
        if (paramType instanceof BInvokableType) {
            // Check for the case when we can give a function as a parameter
            typeName = bVarSymbol.type.toString();
        } else if (paramType instanceof BUnionType) {
            typeName = paramType.toString();
        } else {
            BTypeSymbol tSymbol;
            tSymbol = (paramType instanceof BArrayType) ?
                    ((BArrayType) paramType).eType.tsymbol : paramType.tsymbol;
            List<Name> nameComps = tSymbol.pkgID.nameComps;
            if (tSymbol.pkgID.getName().getValue().equals(Names.BUILTIN_PACKAGE.getValue())
                    || tSymbol.pkgID.getName().getValue().equals(Names.DOT.getValue())) {
                typeName = tSymbol.getName().getValue();
            } else {
                typeName = nameComps.get(nameComps.size() - 1).getValue() + UtilSymbolKeys.PKG_DELIMITER_KEYWORD
                        + tSymbol.getName().getValue();
            }
        }
        
        return typeName;
    }
    
    private String getParameterSignature(BVarSymbol bVarSymbol, boolean isDefault) {
        if (!isDefault) {
            return this.getTypeName(bVarSymbol) + " " + bVarSymbol.getName();
        } else {
            String defaultStringVal;
            if (bVarSymbol.defaultValue == null) {
                defaultStringVal = "()";
            } else {
                defaultStringVal = bVarSymbol.defaultValue.toString();
            }
            return this.getTypeName(bVarSymbol) + " " + bVarSymbol.getName() + " = " + defaultStringVal;
        }
    }
    
    private String getParameterInsertText(BVarSymbol bVarSymbol, boolean isDefault, int iteration) {
        if (!isDefault) {
            return "${" + iteration + ":" + bVarSymbol.getName() + "}";
        } else {
            String defaultStringVal;
            if (bVarSymbol.defaultValue == null) {
                defaultStringVal = "()";
            } else {
                defaultStringVal = bVarSymbol.defaultValue.toString();
                if (bVarSymbol.getType() != null
                        && bVarSymbol.getType().toString().equals(TypeConstants.STRING_TNAME)) {
                    defaultStringVal = "\"" + defaultStringVal + "\"";
                }
            }
            return bVarSymbol.getName() + " = " + "${" + iteration + ":" + defaultStringVal + "}";
        }
    }
    /**
     * Check whether the token stream corresponds to a action invocation or a function invocation.
     * @param documentServiceContext - Completion operation context
     * @return {@link Boolean}
     */
    protected boolean isInvocationOrFieldAccess(LSServiceOperationContext documentServiceContext) {
        ArrayList<String> terminalTokens = new ArrayList<>(Arrays.asList(new String[]{";", "}", "{", "(", ")", "="}));
        Position position = documentServiceContext.get(DocumentServiceKeys.POSITION_KEY).getPosition();
        int cursorLine = position.getLine();
        TokenStream tokenStream = documentServiceContext.get(DocumentServiceKeys.TOKEN_STREAM_KEY);
        if (tokenStream == null) {
            String lineSegment = documentServiceContext.get(CompletionKeys.CURRENT_LINE_SEGMENT_KEY);
            String tokenString = CompletionUtil.getDelimiterTokenFromLineSegment(documentServiceContext, lineSegment);
            return (UtilSymbolKeys.DOT_SYMBOL_KEY.equals(tokenString)
                    || UtilSymbolKeys.PKG_DELIMITER_KEYWORD.equals(tokenString)
                    || UtilSymbolKeys.ACTION_INVOCATION_SYMBOL_KEY.equals(tokenString));
        }
        int searchTokenIndex = documentServiceContext.get(DocumentServiceKeys.TOKEN_INDEX_KEY);
        
        /*
        In order to avoid the token index inconsistencies, current token index offsets from two default tokens
         */
        Token offsetToken = CommonUtil.getNthDefaultTokensToLeft(tokenStream, searchTokenIndex, 2);
        if (!terminalTokens.contains(offsetToken.getText())) {
            searchTokenIndex = offsetToken.getTokenIndex();
        }

        while (true) {
            if (searchTokenIndex >= tokenStream.size()) {
                documentServiceContext.put(CompletionKeys.INVOCATION_STATEMENT_KEY, false);
                return false;
            }
            Token token = tokenStream.get(searchTokenIndex);
            String tokenString = token.getText();
            if (terminalTokens.contains(tokenString)
                    && documentServiceContext.get(DocumentServiceKeys.TOKEN_INDEX_KEY) <= searchTokenIndex) {
                documentServiceContext.put(CompletionKeys.INVOCATION_STATEMENT_KEY, false);
                return false;
            } else if ((UtilSymbolKeys.DOT_SYMBOL_KEY.equals(tokenString)
                    || UtilSymbolKeys.PKG_DELIMITER_KEYWORD.equals(tokenString)
                    || UtilSymbolKeys.ACTION_INVOCATION_SYMBOL_KEY.equals(tokenString))
                    && cursorLine == token.getLine() - 1) {
                documentServiceContext.put(CompletionKeys.INVOCATION_STATEMENT_KEY, true);
                return true;
            } else {
                searchTokenIndex++;
            }
        }
    }

    /**
     * Check whether the token stream contains an annotation start (@).
     * @param ctx - Completion operation context
     * @return {@link Boolean}
     */
    protected boolean isAnnotationContext(LSServiceOperationContext ctx) {
        return ctx.get(DocumentServiceKeys.PARSER_RULE_CONTEXT_KEY) != null
                && UtilSymbolKeys.ANNOTATION_START_SYMBOL_KEY
                .equals(ctx.get(DocumentServiceKeys.TOKEN_STREAM_KEY).get(ctx.get(DocumentServiceKeys.TOKEN_INDEX_KEY))
                        .getText());
    }

    /**
     * Inner static class for the Function Signature. This holds both the insert text and the label for the FUnction.
     * Signature Completion Item
     */
    private static class FunctionSignature {
        private String insertText;
        private String label;

        FunctionSignature(String insertText, String label) {
            this.insertText = insertText;
            this.label = label;
        }

        String getInsertText() {
            return insertText;
        }

        public void setInsertText(String insertText) {
            this.insertText = insertText;
        }

        String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    /**
     * Populate a completion item with the given data and return it.
     * @param insertText insert text
     * @param type type of the completion item
     * @param label completion item label
     * @return {@link CompletionItem}
     */
    protected CompletionItem populateCompletionItem(String insertText, String type, String label) {
        CompletionItem completionItem = new CompletionItem();
        completionItem.setInsertText(insertText);
        completionItem.setDetail(type);
        completionItem.setLabel(label);
        return completionItem;
    }

    protected void populateBasicTypes(List<CompletionItem> completionItems, List<SymbolInfo> visibleSymbols) {
        visibleSymbols.forEach(symbolInfo -> {
            BSymbol bSymbol = symbolInfo.getScopeEntry().symbol;
            if (bSymbol instanceof BTypeSymbol
                    && !bSymbol.getName().getValue().equals(UtilSymbolKeys.NOT_FOUND_TYPE)
                    && !bSymbol.getName().getValue().startsWith(UtilSymbolKeys.ANON_STRUCT_CHECKER)
                    && !((bSymbol instanceof BPackageSymbol) && bSymbol.pkgID.getName().getValue().equals("runtime"))
                    && !(bSymbol instanceof BAnnotationSymbol)
                    && !(bSymbol instanceof BServiceSymbol)) {
                completionItems.add(this.populateBTypeCompletionItem(symbolInfo));
            }
        });
    }

    /**
     * Remove the invalid symbols such as anon types, injected packages and invokable symbols without receiver.
     * @param symbolInfoList    Symbol info list to be filtered
     * @return {@link List}     List of filtered symbols
     */
    protected List<SymbolInfo> removeInvalidStatementScopeSymbols(List<SymbolInfo> symbolInfoList) {
        // We need to remove the functions having a receiver symbol and the bTypes of the following
        // ballerina.coordinator, ballerina.runtime, and anonStructs
        ArrayList<String> invalidPkgs = new ArrayList<>(Arrays.asList("runtime",
                "transactions"));
        symbolInfoList.removeIf(symbolInfo -> {
            BSymbol bSymbol = symbolInfo.getScopeEntry().symbol;
            String symbolName = bSymbol.getName().getValue();
            return (bSymbol instanceof BInvokableSymbol && (((BInvokableSymbol) bSymbol).receiverSymbol) != null
                    && !bSymbol.kind.equals(SymbolKind.RESOURCE))
                    || (bSymbol instanceof BPackageSymbol && invalidPkgs.contains(symbolName))
                    || (symbolName.startsWith("$anonStruct"));
        });
        
        return symbolInfoList;
    }

    /**
     * Get variable definition context related completion items. This will extract the completion items analyzing the
     * variable definition context properties.
     * 
     * @param completionContext     Completion context
     * @return {@link List}         List of resolved completion items
     */
    protected List<CompletionItem> getVariableDefinitionCompletionItems(LSServiceOperationContext completionContext) {
        ArrayList<CompletionItem> completionItems = new ArrayList<>();
        ConnectorInitExpressionItemFilter connectorInitItemFilter = new ConnectorInitExpressionItemFilter();
        // Fill completions if user is writing a connector init
        List<SymbolInfo> filteredConnectorInitSuggestions = connectorInitItemFilter.filterItems(completionContext);
        if (!filteredConnectorInitSuggestions.isEmpty()) {
            populateCompletionItemList(filteredConnectorInitSuggestions, completionItems);
        }

        // Add the create keyword
        CompletionItem createKeyword = new CompletionItem();
        createKeyword.setInsertText(Snippet.CHECK_KEYWORD_SNIPPET.toString());
        createKeyword.setLabel(ItemResolverConstants.CHECK_KEYWORD);
        createKeyword.setDetail(ItemResolverConstants.KEYWORD_TYPE);

        List<SymbolInfo> filteredList = completionContext.get(CompletionKeys.VISIBLE_SYMBOLS_KEY)
                .stream()
                .filter(symbolInfo -> {
                    BSymbol bSymbol = symbolInfo.getScopeEntry().symbol;
                    SymbolKind symbolKind = bSymbol.kind;

                    // Here we return false if the BType is not either a package symbol or ENUM
                    return !((bSymbol instanceof BTypeSymbol) && !(bSymbol instanceof BPackageSymbol
                            || SymbolKind.ENUM.equals(symbolKind)));
                })
                .collect(Collectors.toList());

        // Remove the functions without a receiver symbol
        filteredList.removeIf(symbolInfo -> {
            BSymbol bSymbol = symbolInfo.getScopeEntry().symbol;
            return bSymbol instanceof BInvokableSymbol && ((BInvokableSymbol) bSymbol).receiverSymbol != null;
        });
        populateCompletionItemList(filteredList, completionItems);
        completionItems.add(createKeyword);
        
        return completionItems;
    }
}
