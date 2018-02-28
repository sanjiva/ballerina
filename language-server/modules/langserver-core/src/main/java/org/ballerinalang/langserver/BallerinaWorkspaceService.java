/*
 * Copyright (c) 2017, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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
package org.ballerinalang.langserver;

import org.ballerinalang.langserver.command.CommandExecutor;
import org.ballerinalang.langserver.command.ExecuteCommandKeys;
import org.ballerinalang.langserver.workspace.WorkspaceDocumentManagerImpl; 
import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.WorkspaceSymbolParams;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Workspace service implementation for Ballerina.
 */
public class BallerinaWorkspaceService implements WorkspaceService {
    private BallerinaLanguageServer ballerinaLanguageServer;
    private WorkspaceDocumentManagerImpl workspaceDocumentManager;
    private BLangPackageContext bLangPackageContext;
    
    public BallerinaWorkspaceService(BallerinaLanguageServer ballerinaLanguageServer,
                                     WorkspaceDocumentManagerImpl workspaceDocumentManager,
                                     BLangPackageContext packageContext) {
        this.ballerinaLanguageServer = ballerinaLanguageServer;
        this.workspaceDocumentManager = workspaceDocumentManager;
        this.bLangPackageContext = packageContext;
    }

    @Override
    public CompletableFuture<List<? extends SymbolInformation>> symbol(WorkspaceSymbolParams params) {
        List<SymbolInformation> symbols = new ArrayList<>();
        return CompletableFuture.completedFuture(symbols);
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {
    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
    }

    @Override
    public CompletableFuture<Object> executeCommand(ExecuteCommandParams params) {
        WorkspaceServiceContext executeCommandContext = new WorkspaceServiceContext();
        executeCommandContext.put(ExecuteCommandKeys.COMMAND_ARGUMENTS_KEY, params.getArguments());
        executeCommandContext.put(ExecuteCommandKeys.DOCUMENT_MANAGER_KEY, this.workspaceDocumentManager);
        executeCommandContext.put(ExecuteCommandKeys.BALLERINA_PKG_CONTEXT_KEY, this.bLangPackageContext);
        executeCommandContext.put(ExecuteCommandKeys.LANGUAGE_SERVER_KEY, this.ballerinaLanguageServer);
        
        return CompletableFuture.supplyAsync(() -> {
            CommandExecutor.executeCommand(params, executeCommandContext);
            return true;
        });
    }
}
