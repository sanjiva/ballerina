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

import org.ballerinalang.langserver.compiler.LSServiceOperationContext;
import org.ballerinalang.langserver.completions.CompletionKeys;
import org.ballerinalang.langserver.completions.util.ItemResolverConstants;
import org.ballerinalang.langserver.completions.util.Priority;
import org.ballerinalang.langserver.completions.util.Snippet;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.InsertTextFormat;
import org.wso2.ballerinalang.compiler.tree.BLangInvokableNode;
import org.wso2.ballerinalang.compiler.tree.BLangNode;
import org.wso2.ballerinalang.compiler.tree.BLangWorker;
import org.wso2.ballerinalang.compiler.tree.statements.BLangVariableDef;

import java.util.List;

/**
 * Completion Item sorter to sort items inside a callable unit body. This is used by functions and resources.
 */
class CallableUnitBodyItemSorter extends CompletionItemSorter {
    @Override
    public void sortItems(LSServiceOperationContext ctx, List<CompletionItem> completionItems) {
        BLangNode previousNode = ctx.get(CompletionKeys.PREVIOUS_NODE_KEY);
        
        this.clearItemsIfWorkerExists(ctx, completionItems);
        if (previousNode == null) {
            this.populateWhenCursorBeforeOrAfterEp(completionItems);
        } else if (previousNode instanceof BLangVariableDef) {
            if (ctx.get(CompletionKeys.INVOCATION_STATEMENT_KEY) == null
                    || !ctx.get(CompletionKeys.INVOCATION_STATEMENT_KEY)) {
                CompletionItem workerItem = this.getWorkerSnippet();
                workerItem.setSortText(Priority.PRIORITY160.toString());
                completionItems.add(workerItem);
            }
        } else if (previousNode instanceof BLangWorker) {
            completionItems.add(this.getWorkerSnippet());
        }
        this.setPriorities(completionItems);
    }

    private void populateWhenCursorBeforeOrAfterEp(List<CompletionItem> completionItems) {
        CompletionItem epSnippet = this.getEndpointSnippet();
        CompletionItem workerSnippet = this.getWorkerSnippet();
        this.setPriorities(completionItems);

        epSnippet.setSortText(Priority.PRIORITY150.toString());
        workerSnippet.setSortText(Priority.PRIORITY160.toString());
        completionItems.add(epSnippet);
        completionItems.add(workerSnippet);
    }

    private CompletionItem getWorkerSnippet() {
        CompletionItem workerItem = new CompletionItem();
        workerItem.setLabel(ItemResolverConstants.WORKER);
        workerItem.setInsertText(Snippet.WORKER.toString());
        workerItem.setInsertTextFormat(InsertTextFormat.Snippet);
        workerItem.setDetail(ItemResolverConstants.SNIPPET_TYPE);
        return workerItem;
    }
    
    private void clearItemsIfWorkerExists(LSServiceOperationContext ctx, List<CompletionItem> completionItems) {
        BLangNode blockOwner = (BLangNode) ctx.get(CompletionKeys.BLOCK_OWNER_KEY);
        
        if (blockOwner instanceof BLangInvokableNode && !((BLangInvokableNode) blockOwner).getWorkers().isEmpty()) {
            completionItems.clear();
        }
    }
}
