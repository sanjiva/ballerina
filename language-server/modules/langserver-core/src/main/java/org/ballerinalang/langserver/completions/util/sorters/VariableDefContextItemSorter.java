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
package org.ballerinalang.langserver.completions.util.sorters;

import org.antlr.v4.runtime.Token;
import org.ballerinalang.langserver.compiler.LSServiceOperationContext;
import org.ballerinalang.langserver.completions.CompletionKeys;
import org.ballerinalang.langserver.completions.util.ItemResolverConstants;
import org.eclipse.lsp4j.CompletionItem;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Item Sorter for the Variable Definition context.
 */
public class VariableDefContextItemSorter extends CompletionItemSorter {
    /**
     * Sort Completion Items based on a particular criteria.
     *
     * @param ctx             Completion context
     * @param completionItems List of initial completion items
     */
    @Override
    public void sortItems(LSServiceOperationContext ctx, List<CompletionItem> completionItems) {
        this.setPriorities(completionItems);
        String variableType = this.getVariableType(ctx);
        List<String> poppedTokens = ctx.get(CompletionKeys.FORCE_CONSUMED_TOKENS_KEY)
                .stream()
                .map(Token::getText)
                .collect(Collectors.toList());

        if (poppedTokens.contains("=")) {
            completionItems.forEach(completionItem -> {
                if (completionItem.getDetail().equals(ItemResolverConstants.FUNCTION_TYPE)) {
                    String label = completionItem.getLabel();
                    String functionReturnType = label.substring(label.lastIndexOf("(") + 1, label.lastIndexOf(")"));

                    if (variableType.equals(functionReturnType)) {
                        this.increasePriority(completionItem);
                    }
                }
            });
        }
    }
    
    public void increasePriority(CompletionItem completionItem) {
        int sortText = Integer.parseInt(completionItem.getSortText());
        completionItem.setSortText(Integer.toString(sortText - 1));
    }

    /**
     * Get the variable type.
     * @param ctx   Document Service context (Completion context)
     * @return      {@link String} type of the variable
     */
    String getVariableType(LSServiceOperationContext ctx) {
        List<String> poppedTokens = ctx.get(CompletionKeys.FORCE_CONSUMED_TOKENS_KEY)
                .stream()
                .map(Token::getText)
                .collect(Collectors.toList());
        return poppedTokens.get(0);
    }
}
