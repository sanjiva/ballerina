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
import org.ballerinalang.langserver.common.UtilSymbolKeys;
import org.ballerinalang.langserver.common.utils.CommonUtil;
import org.ballerinalang.langserver.common.utils.completion.BInvokableSymbolUtil;
import org.ballerinalang.langserver.common.utils.completion.BPackageSymbolUtil;
import org.ballerinalang.langserver.compiler.LSServiceOperationContext;
import org.ballerinalang.langserver.completions.CompletionKeys;
import org.ballerinalang.langserver.completions.SymbolInfo;
import org.ballerinalang.langserver.completions.util.ItemResolverConstants;
import org.ballerinalang.langserver.completions.util.Snippet;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BInvokableSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BPackageSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BTypeSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BVarSymbol;
import org.wso2.ballerinalang.util.Flags;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interface for completion item resolvers.
 */
public abstract class AbstractItemResolver {
    
    public abstract List<CompletionItem> resolveItems(LSServiceOperationContext completionContext);

    /**
     * Populate the completion item list by considering the.
     *
     * @param symbolInfoList    list of symbol information
     * @return {@link List}     list of completion items
     */
    protected List<CompletionItem> getCompletionItemList(List<SymbolInfo> symbolInfoList) {
        List<CompletionItem> completionItems = new ArrayList<>();
        symbolInfoList.removeIf(CommonUtil.invalidSymbolsPredicate());
        symbolInfoList.forEach(symbolInfo -> {
            BSymbol bSymbol = symbolInfo.isIterableOperation() ? null : symbolInfo.getScopeEntry().symbol;
            if (CommonUtil.isValidInvokableSymbol(bSymbol) || symbolInfo.isIterableOperation()) {
                completionItems.add(this.populateBallerinaFunctionCompletionItem(symbolInfo));
            } else if (!(bSymbol instanceof BInvokableSymbol) && bSymbol instanceof BVarSymbol) {
                completionItems.add(this.populateVariableDefCompletionItem(symbolInfo));
            } else if (bSymbol instanceof BTypeSymbol) {
                completionItems.add(BPackageSymbolUtil.getBTypeCompletionItem(symbolInfo.getSymbolName()));
            }
        });
        
        return completionItems;
    }

    /**
     * Populate the completion item list by either list.
     * @param list              Either List of completion items or symbol info
     * @return {@link List}     Completion Items List
     */
    protected List<CompletionItem> getCompletionItemList(Either<List<CompletionItem>, List<SymbolInfo>> list) {
        List<CompletionItem> completionItems = new ArrayList<>();
        if (list.isLeft()) {
            completionItems.addAll(list.getLeft());
        } else {
            completionItems.addAll(this.getCompletionItemList(list.getRight()));
        }
        
        return completionItems;
    }

    /**
     * Populate the Ballerina Function Completion Item.
     * @param symbolInfo - symbol information
     * @return completion item
     */
    private CompletionItem populateBallerinaFunctionCompletionItem(SymbolInfo symbolInfo) {
        if (symbolInfo.isIterableOperation()) {
            return BInvokableSymbolUtil.getFunctionCompletionItem(
                    symbolInfo.getIterableOperationSignature().getInsertText(),
                    symbolInfo.getIterableOperationSignature().getLabel());
        }
        BSymbol bSymbol = symbolInfo.getScopeEntry().symbol;
        if (!(bSymbol instanceof BInvokableSymbol)) {
            return null;
        }
        return BInvokableSymbolUtil.getFunctionCompletionItem((BInvokableSymbol) bSymbol);
    }

    /**
     * Populate the Variable Definition Completion Item.
     *
     * @param symbolInfo                symbol information
     * @return {@link CompletionItem}   completion item
     */
    protected CompletionItem populateVariableDefCompletionItem(SymbolInfo symbolInfo) {
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
     * Check whether the token stream corresponds to a action invocation or a function invocation.
     *
     * @param context               Completion operation context
     * @return {@link Boolean}      Whether invocation or Field Access
     */
    protected boolean isInvocationOrFieldAccess(LSServiceOperationContext context) {
        List<String> poppedTokens = CommonUtil.popNFromStack(context.get(CompletionKeys.FORCE_CONSUMED_TOKENS_KEY), 2)
                .stream()
                .map(Token::getText)
                .collect(Collectors.toList());
        return poppedTokens.contains(UtilSymbolKeys.DOT_SYMBOL_KEY)
                || poppedTokens.contains(UtilSymbolKeys.PKG_DELIMITER_KEYWORD)
                || poppedTokens.contains(UtilSymbolKeys.ACTION_INVOCATION_SYMBOL_KEY)
                || poppedTokens.contains(UtilSymbolKeys.BANG_SYMBOL_KEY);
    }

    /**
     * Check whether the token stream contains an annotation start (@).
     *
     * @param ctx                   Completion operation context
     * @return {@link Boolean}      Whether annotation context start or not
     */
    protected boolean isAnnotationStart(LSServiceOperationContext ctx) {
        List<String> poppedTokens = CommonUtil.popNFromStack(ctx.get(CompletionKeys.FORCE_CONSUMED_TOKENS_KEY), 4)
                .stream()
                .map(Token::getText)
                .collect(Collectors.toList());
        return poppedTokens.contains(UtilSymbolKeys.ANNOTATION_START_SYMBOL_KEY);
    }

    /**
     * Populate the basic types.
     *
     * @param visibleSymbols    List of visible symbols
     * @return {@link List}     List of completion items
     */
    protected List<CompletionItem> populateBasicTypes(List<SymbolInfo> visibleSymbols) {
        visibleSymbols.removeIf(CommonUtil.invalidSymbolsPredicate());
        List<CompletionItem> completionItems = new ArrayList<>();
        visibleSymbols.forEach(symbolInfo -> {
            BSymbol bSymbol = symbolInfo.getScopeEntry().symbol;
            if (bSymbol instanceof BTypeSymbol) {
                completionItems.add(BPackageSymbolUtil.getBTypeCompletionItem(symbolInfo.getSymbolName()));
            }
        });
        
        return completionItems;
    }

    /**
     * Get variable definition context related completion items. This will extract the completion items analyzing the
     * variable definition context properties.
     * 
     * @param completionContext     Completion context
     * @return {@link List}         List of resolved completion items
     */
    protected List<CompletionItem> getVarDefCompletionItems(LSServiceOperationContext completionContext) {
        ArrayList<CompletionItem> completionItems = new ArrayList<>();
        List<SymbolInfo> filteredList = completionContext.get(CompletionKeys.VISIBLE_SYMBOLS_KEY);
        // Remove the functions without a receiver symbol, bTypes not being packages and attached functions
        filteredList.removeIf(symbolInfo -> {
            BSymbol bSymbol = symbolInfo.getScopeEntry().symbol;
            return (bSymbol instanceof BInvokableSymbol
                    && ((BInvokableSymbol) bSymbol).receiverSymbol != null
                    && CommonUtil.isValidInvokableSymbol(bSymbol))
                    || ((bSymbol instanceof BTypeSymbol)
                    && !(bSymbol instanceof BPackageSymbol))
                    || (bSymbol instanceof BInvokableSymbol
                    && ((bSymbol.flags & Flags.ATTACHED) == Flags.ATTACHED));
        });
        completionItems.addAll(getCompletionItemList(filteredList));

        // Add the check keyword
        CompletionItem checkKeyword = new CompletionItem();
        checkKeyword.setInsertText(Snippet.CHECK_KEYWORD_SNIPPET.toString());
        checkKeyword.setLabel(ItemResolverConstants.CHECK_KEYWORD);
        checkKeyword.setDetail(ItemResolverConstants.KEYWORD_TYPE);

        // Add But keyword item
        CompletionItem butKeyword = new CompletionItem();
        butKeyword.setInsertText(Snippet.BUT.toString());
        butKeyword.setLabel(ItemResolverConstants.BUT);
        butKeyword.setInsertTextFormat(InsertTextFormat.Snippet);
        butKeyword.setDetail(ItemResolverConstants.STATEMENT_TYPE);

        // Add lengthof keyword item
        CompletionItem lengthofKeyword = new CompletionItem();
        lengthofKeyword.setInsertText(Snippet.LENGTHOF.toString());
        lengthofKeyword.setLabel(ItemResolverConstants.LENGTHOF);
        lengthofKeyword.setDetail(ItemResolverConstants.KEYWORD_TYPE);

        completionItems.add(checkKeyword);
        completionItems.add(butKeyword);
        completionItems.add(lengthofKeyword);
        
        return completionItems;
    }

    /**
     * Get the completion Items from the either list.
     * 
     * Note: By Default we populate the completions with the getCompletionItemList. Resolvers can override when needed.
     * @param either            Either symbol info list or completion Item list
     * @return {@link List}     List of completion Items
     */
    protected List<CompletionItem> getCompletionsFromEither(Either<List<CompletionItem>, List<SymbolInfo>> either) {
        if (either.isLeft()) {
            return either.getLeft();
        } else {
            return this.getCompletionItemList(either.getRight());
        }
    }
}
