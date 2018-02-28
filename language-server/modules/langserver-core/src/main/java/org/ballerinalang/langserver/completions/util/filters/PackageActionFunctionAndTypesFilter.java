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
package org.ballerinalang.langserver.completions.util.filters;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.ballerinalang.langserver.DocumentServiceKeys;
import org.ballerinalang.langserver.TextDocumentServiceContext;
import org.ballerinalang.langserver.completions.CompletionKeys;
import org.ballerinalang.langserver.completions.SymbolInfo;
import org.ballerinalang.langserver.completions.util.ItemResolverConstants;
import org.ballerinalang.langserver.completions.util.Snippet;
import org.ballerinalang.model.types.Type;
import org.wso2.ballerinalang.compiler.semantics.model.Scope;
import org.wso2.ballerinalang.compiler.semantics.model.SymbolTable;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BInvokableSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BPackageSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BTypeSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.types.BArrayType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BConnectorType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BEndpointType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BJSONType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BMapType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BTableType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BTupleCollectionType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BXMLType;
import org.wso2.ballerinalang.compiler.util.Name;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Filter the actions and the functions in a package.
 */
public class PackageActionFunctionAndTypesFilter extends AbstractSymbolFilter {

    private static final String CREATE_KEYWORD = "create";

    private static final String PKG_DELIMITER_KEYWORD = ":";

    private static final String DOT_SYMBOL_KEY = ".";

    private static final String CONNECTOR_KIND = "CONNECTOR";
    
    private static final String ITR_OP_LAMBDA_PARAM_REPLACE_TOKEN = "%params%";

    @Override
    public List<SymbolInfo> filterItems(TextDocumentServiceContext completionContext) {

        TokenStream tokenStream = completionContext.get(DocumentServiceKeys.TOKEN_STREAM_KEY);
        int delimiterIndex = this.getPackageDelimiterTokenIndex(completionContext);
        String delimiter = tokenStream.get(delimiterIndex).getText();
        ArrayList<SymbolInfo> returnSymbolsInfoList = new ArrayList<>();

        if (DOT_SYMBOL_KEY.equals(delimiter)) {
            // If the delimiter is "." then we are filtering the bound functions for the structs
            returnSymbolsInfoList.addAll(this.invocationsAndFieldsOnIdentifier(completionContext, delimiterIndex));
        } else if (PKG_DELIMITER_KEYWORD.equals(delimiter)) {
            // We are filtering the package functions, actions and the types
            ArrayList<SymbolInfo> filteredList = this.getActionsFunctionsAndTypes(completionContext, delimiterIndex);

            // If this is a connector init
            if (isConnectorInit(delimiterIndex, completionContext)) {
                List<SymbolInfo> connectorKindList = filteredList
                        .stream()
                        .filter(item -> item.getScopeEntry().symbol.kind.toString().equals(CONNECTOR_KIND)).
                                collect(Collectors.toList());
                returnSymbolsInfoList.addAll(connectorKindList);
            } else {
                returnSymbolsInfoList.addAll(filteredList);
            }
        }

        return returnSymbolsInfoList;
    }

    /**
     * Check whether the statement being writing is a connector init by analyzing the tokens.
     * @param startIndex    Search start index
     * @param context       Document service context
     * @return {@link Boolean} connector init or not
     */
    private boolean isConnectorInit(int startIndex, TextDocumentServiceContext context) {
        int nonHiddenTokenCount = 0;
        int counter = startIndex - 1;
        TokenStream tokenStream = context.get(DocumentServiceKeys.TOKEN_STREAM_KEY);

        while (true) {
            Token token = tokenStream.get(counter);
            if (nonHiddenTokenCount == 2 && tokenStream.get(counter + 1).getText().equals(CREATE_KEYWORD)) {
                return true;
            } else if (nonHiddenTokenCount == 2) {
                break;
            }

            if (token.getChannel() == Token.DEFAULT_CHANNEL) {
                nonHiddenTokenCount++;
            }
            counter--;
        }

        return false;
    }

    /**
     * Get the actions, functions and types.
     * @param completionContext     Text Document Service context (Completion Context)
     * @param delimiterIndex        delimiter index (index of either . or :)
     * @return {@link ArrayList}    List of filtered symbol info
     */
    private ArrayList<SymbolInfo> getActionsFunctionsAndTypes(TextDocumentServiceContext completionContext,
                                                              int delimiterIndex) {
        ArrayList<SymbolInfo> actionFunctionList = new ArrayList<>();
        TokenStream tokenStream = completionContext.get(DocumentServiceKeys.TOKEN_STREAM_KEY);
        List<SymbolInfo> symbols = completionContext.get(CompletionKeys.VISIBLE_SYMBOLS_KEY);
        String packageName = tokenStream.get(delimiterIndex - 1).getText();

        // Extract the package symbol
        SymbolInfo packageSymbolInfo = symbols.stream().filter(item -> {
            Scope.ScopeEntry scopeEntry = item.getScopeEntry();
            return item.getSymbolName().equals(packageName) && scopeEntry.symbol instanceof BPackageSymbol;
        }).findFirst().orElse(null);

        if (packageSymbolInfo != null) {
            Scope.ScopeEntry packageEntry = packageSymbolInfo.getScopeEntry();
            SymbolInfo symbolInfo = new SymbolInfo(packageSymbolInfo.getSymbolName(), packageEntry);

            symbolInfo.getScopeEntry().symbol.scope.entries.forEach((name, value) -> {
                if ((value.symbol instanceof BInvokableSymbol
                        && ((BInvokableSymbol) value.symbol).receiverSymbol == null)
                        || (value.symbol instanceof BTypeSymbol && !(value.symbol instanceof BPackageSymbol))) {
                    actionFunctionList.add(new SymbolInfo(name.toString(), value));
                }
            });
        }

        return actionFunctionList;
    }

    /**
     * Get the invocations and fields against an identifier (functions, struct fields and types including the enums).
     * @param context     Text Document Service context (Completion Context)
     * @param delimiterIndex        delimiter index (index of either . or :)
     * @return {@link ArrayList}    List of filtered symbol info
     */
    private ArrayList<SymbolInfo> invocationsAndFieldsOnIdentifier(TextDocumentServiceContext context,
                                                                   int delimiterIndex) {
        ArrayList<SymbolInfo> actionFunctionList = new ArrayList<>();
        TokenStream tokenStream = context.get(DocumentServiceKeys.TOKEN_STREAM_KEY);
        List<SymbolInfo> symbols = context.get(CompletionKeys.VISIBLE_SYMBOLS_KEY);
        SymbolTable symbolTable = context.get(DocumentServiceKeys.SYMBOL_TABLE_KEY);
        String variableName = tokenStream.get(delimiterIndex - 1).getText();
        SymbolInfo variable = this.getVariableByName(variableName, symbols);
        String builtinPkgName = symbolTable.builtInPackageSymbol.name.getValue();
        Map<Name, Scope.ScopeEntry> entries = new HashMap<>();
        String constraintTypeName = null;

        if (variable == null) {
            return actionFunctionList;
        }

        String packageID;
        BType bType = variable.getScopeEntry().symbol.getType();
        String bTypeValue;
        if (bType instanceof BEndpointType) {
            // If the BType is a BEndPointType we get the package id of the constraint
            Type constraint = ((BEndpointType) bType).getConstraint();
            packageID = ((BConnectorType) constraint).tsymbol.pkgID.toString();
            constraintTypeName = constraint.toString();
            bTypeValue = constraint.toString();
        } else if (bType instanceof BArrayType) {
            packageID = ((BArrayType) bType).eType.tsymbol.pkgID.toString();
            bTypeValue = bType.toString();
        } else {
            packageID = bType.tsymbol.pkgID.toString();
            bTypeValue = bType.toString();
        }

        // Extract the package symbol. This is used to extract the entries of the particular package
        SymbolInfo packageSymbolInfo = symbols.stream().filter(item -> {
            Scope.ScopeEntry scopeEntry = item.getScopeEntry();
            return (scopeEntry.symbol instanceof BPackageSymbol)
                    && scopeEntry.symbol.pkgID.toString().equals(packageID);
        }).findFirst().orElse(null);

        if (packageSymbolInfo == null && packageID.equals(builtinPkgName)) {
            // If the packageID is ballerina.builtin, we extract entries of builtin package
            entries = symbolTable.builtInPackageSymbol.scope.entries;
        } else if (packageSymbolInfo == null && packageID.equals(".")) {
            entries = this.getScopeEntries(bType, context);
        } else if (packageSymbolInfo != null) {
            // If the package exist, we extract particular entries from package
            entries = packageSymbolInfo.getScopeEntry().symbol.scope.entries;
            if (constraintTypeName != null) {
                // If there is a constraint type for the variable, which means we are filtering the actions for the
                // particular endpoint. Hence we get the particular ClientConnector entry and it's action entries
                String filterName = constraintTypeName;
                Map.Entry filteredEntry = entries.entrySet()
                        .stream()
                        .filter(nameScopeEntry -> nameScopeEntry.getKey().getValue().equals(filterName))
                        .findFirst()
                        .orElse(null);
                entries = ((Scope.ScopeEntry) filteredEntry.getValue()).symbol.scope.entries;
            }
        }
        
        entries.forEach((name, scopeEntry) -> {
            if (scopeEntry.symbol instanceof BInvokableSymbol
                    && ((BInvokableSymbol) scopeEntry.symbol).receiverSymbol != null) {
                String symbolBoundedName = ((BInvokableSymbol) scopeEntry.symbol)
                        .receiverSymbol.getType().toString();

                if (symbolBoundedName.equals(bTypeValue)) {
                    // TODO: Need to handle the name in a proper manner
                    String[] nameComponents = name.toString().split("\\.");
                    SymbolInfo actionFunctionSymbol =
                            new SymbolInfo(nameComponents[nameComponents.length - 1], scopeEntry);
                    actionFunctionList.add(actionFunctionSymbol);
                }
            } else if ((scopeEntry.symbol instanceof BTypeSymbol)
                    && bTypeValue.equals(scopeEntry.symbol.type.toString())) {
                // Get the struct fields
                Map<Name, Scope.ScopeEntry> fields = scopeEntry.symbol.scope.entries;
                fields.forEach((fieldName, fieldScopeEntry) -> {
                    actionFunctionList.add(new SymbolInfo(fieldName.getValue(), fieldScopeEntry));
                });
            }
        });
        
        // Populate possible iterable operators over the variable
        populateIterableOperations(variable, actionFunctionList);

        return actionFunctionList;
    }

    /**
     * Get the package delimiter token index, which is the index of . or :.
     * @param completionContext     Text Document Service context (Completion Context)
     * @return {@link Integer}      Index of the delimiter
     */
    private int getPackageDelimiterTokenIndex(TextDocumentServiceContext completionContext) {
        ArrayList<String> terminalTokens = new ArrayList<>(Arrays.asList(new String[]{";", "}", "{"}));
        int searchTokenIndex = completionContext.get(DocumentServiceKeys.TOKEN_INDEX_KEY);
        TokenStream tokenStream = completionContext.get(DocumentServiceKeys.TOKEN_STREAM_KEY);
        int delimiterIndex = -1;
        String currentTokenStr = tokenStream.get(searchTokenIndex).getText();

        if (terminalTokens.contains(currentTokenStr)) {
            searchTokenIndex -= 1;
            while (true) {
                if (tokenStream.get(searchTokenIndex).getChannel() == Token.DEFAULT_CHANNEL) {
                    break;
                } else {
                    searchTokenIndex -= 1;
                }
            }
        }

        while (true) {
            if (searchTokenIndex >= tokenStream.size()) {
                break;
            }
            String tokenString = tokenStream.get(searchTokenIndex).getText();
            if (".".equals(tokenString) || ":".equals(tokenString)) {
                delimiterIndex = searchTokenIndex;
                break;
            } else if (terminalTokens.contains(tokenString)) {
                break;
            } else {
                searchTokenIndex++;
            }
        }

        return delimiterIndex;
    }

    /**
     * Get the variable symbol info by the name.
     * @param name      name of the variable
     * @param symbols   list of symbol info
     * @return {@link SymbolInfo}   Symbol Info extracted
     */
    private SymbolInfo getVariableByName(String name, List<SymbolInfo> symbols) {
        return symbols.stream()
                .filter(symbolInfo -> symbolInfo.getSymbolName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get the scope entries.
     * @param bType             BType 
     * @param completionCtx     Completion context
     * @return                  {@link Map} Scope entries map
     */
    private Map<Name, Scope.ScopeEntry> getScopeEntries(BType bType, TextDocumentServiceContext completionCtx) {
        SymbolInfo filteredSymbolInfo = completionCtx.get(CompletionKeys.VISIBLE_SYMBOLS_KEY)
                .stream()
                .filter(symbolInfo -> symbolInfo.getScopeEntry().symbol instanceof BTypeSymbol
                        && symbolInfo.getScopeEntry().symbol.getType() != null 
                        && symbolInfo.getScopeEntry().symbol.getType().toString().equals(bType.toString()))
                .findFirst()
                .orElse(null);

        HashMap<Name, Scope.ScopeEntry> returnMap = new HashMap<>();
        returnMap.put(filteredSymbolInfo.getScopeEntry().symbol.getName(), filteredSymbolInfo.getScopeEntry());
        return returnMap;
    }
    
    private void populateIterableOperations(SymbolInfo variable, List<SymbolInfo> symbolInfoList) {
        BType bType = variable.getScopeEntry().symbol.getType();

        if (bType instanceof BArrayType || bType instanceof BMapType || bType instanceof BJSONType
                || bType instanceof BXMLType || bType instanceof BTableType || bType instanceof BTupleCollectionType) {
            fillForeachIterableOperation(bType, symbolInfoList);
            fillMapIterableOperation(bType, symbolInfoList);
            fillFilterIterableOperation(bType, symbolInfoList);
            fillCountIterableOperation(symbolInfoList);
            if (bType instanceof BArrayType && (((BArrayType) bType).eType.toString().equals("int")
                    || ((BArrayType) bType).eType.toString().equals("float"))) {
                fillMinIterableOperation(symbolInfoList);
                fillMaxIterableOperation(symbolInfoList);
                fillAverageIterableOperation(symbolInfoList);
                fillSumIterableOperation(symbolInfoList);
            }
            
            // TODO: Add support for Table and Tuple collection
        }
    }
    
    private void fillForeachIterableOperation(BType bType, List<SymbolInfo> symbolInfoList) {
        String params = getIterableOpLambdaParam(bType);
        
        String lambdaSignature = Snippet.ITR_FOREACH.toString().replace(ITR_OP_LAMBDA_PARAM_REPLACE_TOKEN, params);
        SymbolInfo.IterableOperationSignature signature =
                new SymbolInfo.IterableOperationSignature(ItemResolverConstants.ITR_FOREACH_LABEL, lambdaSignature);
        SymbolInfo forEachSymbolInfo = new SymbolInfo();
        forEachSymbolInfo.setIterableOperation(true);
        forEachSymbolInfo.setIterableOperationSignature(signature);
        symbolInfoList.add(forEachSymbolInfo);
    }
    
    private void fillMapIterableOperation(BType bType, List<SymbolInfo> symbolInfoList) {
        String params = getIterableOpLambdaParam(bType);

        String lambdaSignature = Snippet.ITR_MAP.toString().replace(ITR_OP_LAMBDA_PARAM_REPLACE_TOKEN, params);
        SymbolInfo.IterableOperationSignature signature =
                new SymbolInfo.IterableOperationSignature(ItemResolverConstants.ITR_MAP_LABEL, lambdaSignature);
        SymbolInfo forEachSymbolInfo = new SymbolInfo();
        forEachSymbolInfo.setIterableOperation(true);
        forEachSymbolInfo.setIterableOperationSignature(signature);
        symbolInfoList.add(forEachSymbolInfo);
    }

    private void fillFilterIterableOperation(BType bType, List<SymbolInfo> symbolInfoList) {
        String params = getIterableOpLambdaParam(bType);

        String lambdaSignature = Snippet.ITR_FILTER.toString().replace(ITR_OP_LAMBDA_PARAM_REPLACE_TOKEN, params);
        SymbolInfo.IterableOperationSignature signature =
                new SymbolInfo.IterableOperationSignature(ItemResolverConstants.ITR_FILTER_LABEL, lambdaSignature);
        SymbolInfo forEachSymbolInfo = new SymbolInfo();
        forEachSymbolInfo.setIterableOperation(true);
        forEachSymbolInfo.setIterableOperationSignature(signature);
        symbolInfoList.add(forEachSymbolInfo);
    }

    private void fillCountIterableOperation(List<SymbolInfo> symbolInfoList) {
        String lambdaSignature = Snippet.ITR_COUNT.toString();
        SymbolInfo.IterableOperationSignature signature =
                new SymbolInfo.IterableOperationSignature(ItemResolverConstants.ITR_COUNT_LABEL, lambdaSignature);
        SymbolInfo forEachSymbolInfo = new SymbolInfo();
        forEachSymbolInfo.setIterableOperation(true);
        forEachSymbolInfo.setIterableOperationSignature(signature);
        symbolInfoList.add(forEachSymbolInfo);
    }

    private void fillMinIterableOperation(List<SymbolInfo> symbolInfoList) {
        String lambdaSignature = Snippet.ITR_MIN.toString();
        SymbolInfo.IterableOperationSignature signature =
                new SymbolInfo.IterableOperationSignature(ItemResolverConstants.ITR_MIN_LABEL, lambdaSignature);
        SymbolInfo forEachSymbolInfo = new SymbolInfo();
        forEachSymbolInfo.setIterableOperation(true);
        forEachSymbolInfo.setIterableOperationSignature(signature);
        symbolInfoList.add(forEachSymbolInfo);
    }

    private void fillMaxIterableOperation(List<SymbolInfo> symbolInfoList) {
        String lambdaSignature = Snippet.ITR_MAX.toString();
        SymbolInfo.IterableOperationSignature signature =
                new SymbolInfo.IterableOperationSignature(ItemResolverConstants.ITR_MAX_LABEL, lambdaSignature);
        SymbolInfo forEachSymbolInfo = new SymbolInfo();
        forEachSymbolInfo.setIterableOperation(true);
        forEachSymbolInfo.setIterableOperationSignature(signature);
        symbolInfoList.add(forEachSymbolInfo);
    }

    private void fillAverageIterableOperation(List<SymbolInfo> symbolInfoList) {
        String lambdaSignature = Snippet.ITR_AVERAGE.toString();
        SymbolInfo.IterableOperationSignature signature =
                new SymbolInfo.IterableOperationSignature(ItemResolverConstants.ITR_AVERAGE_LABEL, lambdaSignature);
        SymbolInfo forEachSymbolInfo = new SymbolInfo();
        forEachSymbolInfo.setIterableOperation(true);
        forEachSymbolInfo.setIterableOperationSignature(signature);
        symbolInfoList.add(forEachSymbolInfo);
    }

    private void fillSumIterableOperation(List<SymbolInfo> symbolInfoList) {
        String lambdaSignature = Snippet.ITR_SUM.toString();
        SymbolInfo.IterableOperationSignature signature =
                new SymbolInfo.IterableOperationSignature(ItemResolverConstants.ITR_SUM_LABEL, lambdaSignature);
        SymbolInfo forEachSymbolInfo = new SymbolInfo();
        forEachSymbolInfo.setIterableOperation(true);
        forEachSymbolInfo.setIterableOperationSignature(signature);
        symbolInfoList.add(forEachSymbolInfo);
    }
    
    private String getIterableOpLambdaParam(BType bType) {
        String params = "";
        if (bType instanceof BMapType) {
            params = Snippet.ITR_ON_MAP_PARAMS.toString();
        } else if (bType instanceof BArrayType) {
            params = ((BArrayType) bType).eType.toString() + " v";
        } else if (bType instanceof BJSONType) {
            params = Snippet.ITR_ON_JSON_PARAMS.toString();
        } else if (bType instanceof BXMLType) {
            params = Snippet.ITR_ON_XML_PARAMS.toString();
        }
        
        return params;
    }
}
