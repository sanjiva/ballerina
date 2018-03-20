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

import org.ballerinalang.langserver.TextDocumentServiceContext;
import org.ballerinalang.langserver.completions.CompletionKeys;
import org.ballerinalang.langserver.completions.SymbolInfo;
import org.ballerinalang.langserver.completions.util.ItemResolverConstants;
import org.ballerinalang.langserver.completions.util.Snippet;
import org.ballerinalang.langserver.completions.util.filters.PackageActionFunctionAndTypesFilter;
import org.ballerinalang.langserver.completions.util.filters.StatementTemplateFilter;
import org.ballerinalang.langserver.completions.util.sorters.ItemSorters;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.InsertTextFormat;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BInvokableSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BPackageSymbol;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BSymbol;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Statement context resolver for resolving the items of the statement context.
 */
public class StatementContextResolver extends AbstractItemResolver {
    @Override
    @SuppressWarnings("unchecked")
    public ArrayList<CompletionItem> resolveItems(TextDocumentServiceContext completionContext) {
        ArrayList<CompletionItem> completionItems = new ArrayList<>();

        // Here we specifically need to check whether the statement is function invocation,
        // action invocation or worker invocation
        if (isInvocationOrFieldAccess(completionContext)) {
            ArrayList<SymbolInfo> actionAndFunctions = new ArrayList<>();
            PackageActionFunctionAndTypesFilter actionFunctionTypeFilter = new PackageActionFunctionAndTypesFilter();
            actionAndFunctions.addAll(actionFunctionTypeFilter.filterItems(completionContext));
            this.populateCompletionItemList(actionAndFunctions, completionItems);
        } else {
            CompletionItem xmlns = new CompletionItem();
            xmlns.setLabel(ItemResolverConstants.XMLNS);
            xmlns.setInsertText(Snippet.NAMESPACE_DECLARATION.toString());
            xmlns.setInsertTextFormat(InsertTextFormat.Snippet);
            xmlns.setDetail(ItemResolverConstants.SNIPPET_TYPE);
            completionItems.add(xmlns);

            // Add the var keyword
            CompletionItem varKeyword = new CompletionItem();
            varKeyword.setInsertText("var ");
            varKeyword.setLabel("var");
            varKeyword.setDetail(ItemResolverConstants.KEYWORD_TYPE);
            completionItems.add(varKeyword);

            StatementTemplateFilter statementTemplateFilter = new StatementTemplateFilter();
            // Add the statement templates
            completionItems.addAll(statementTemplateFilter.filterItems(completionContext));
            // We need to remove the functions having a receiver symbol and the bTypes of the following
            // ballerina.coordinator, ballerina.runtime, and anonStructs
            ArrayList<String> invalidPkgs = new ArrayList<>(Arrays.asList("ballerina.runtime",
                    "ballerina.transactions.coordinator"));
            completionContext.get(CompletionKeys.VISIBLE_SYMBOLS_KEY).removeIf(symbolInfo -> {
                BSymbol bSymbol = symbolInfo.getScopeEntry().symbol;
                String symbolName = bSymbol.getName().getValue();
                return (bSymbol instanceof BInvokableSymbol && ((BInvokableSymbol) bSymbol).receiverSymbol != null)
                        || (bSymbol instanceof BPackageSymbol && invalidPkgs.contains(symbolName))
                        || (symbolName.startsWith("$anonStruct"));
            });
            populateCompletionItemList(completionContext.get(CompletionKeys.VISIBLE_SYMBOLS_KEY), completionItems);
            // Now we need to sort the completion items and populate the completion items specific to the scope owner
            // as an example, resource, action, function scopes are different from the if-else, while, and etc
            Class itemSorter = completionContext.get(CompletionKeys.BLOCK_OWNER_KEY).getClass();
            ItemSorters.getSorterByClass(itemSorter).sortItems(completionContext, completionItems);
        }

        return completionItems;
    }
}
