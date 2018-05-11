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
 *
 */

package org.ballerinalang.plugins.idea.stubs.types;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.containers.ContainerUtil;
import org.ballerinalang.plugins.idea.psi.BallerinaEndpointDefinition;
import org.ballerinalang.plugins.idea.psi.BallerinaNamedElement;
import org.ballerinalang.plugins.idea.psi.impl.BallerinaEndpointDefinitionImpl;
import org.ballerinalang.plugins.idea.stubs.BallerinaEndpointDefinitionStub;
import org.ballerinalang.plugins.idea.stubs.index.BallerinaEndpointIndex;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents endpoint definition stub element.
 */
public class BallerinaEndpointDefinitionStubElementType extends
        BallerinaNamedStubElementType<BallerinaEndpointDefinitionStub, BallerinaEndpointDefinition> {

    private static final ArrayList<StubIndexKey<String, ? extends BallerinaNamedElement>> EXTRA_KEYS =
            ContainerUtil.newArrayList(BallerinaEndpointIndex.KEY);

    public BallerinaEndpointDefinitionStubElementType(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    public BallerinaEndpointDefinition createPsi(@NotNull BallerinaEndpointDefinitionStub stub) {
        return new BallerinaEndpointDefinitionImpl(stub, this);
    }

    @NotNull
    @Override
    public BallerinaEndpointDefinitionStub createStub(@NotNull BallerinaEndpointDefinition psi, StubElement
            parentStub) {
        return new BallerinaEndpointDefinitionStub(parentStub, this, psi.getName(), psi.isPublic());
    }

    @Override
    public void indexStub(@NotNull BallerinaEndpointDefinitionStub stub, @NotNull IndexSink sink) {
        String name = stub.getName();
        if (shouldIndex() && StringUtil.isNotEmpty(name)) {
            sink.occurrence(BallerinaEndpointIndex.KEY, name);
        }
    }

    @Override
    public void serialize(@NotNull BallerinaEndpointDefinitionStub stub, @NotNull StubOutputStream dataStream)
            throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeBoolean(stub.isPublic());
    }

    @NotNull
    @Override
    public BallerinaEndpointDefinitionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub)
            throws IOException {
        return new BallerinaEndpointDefinitionStub(parentStub, this, dataStream.readName(), dataStream.readBoolean());
    }

    @NotNull
    @Override
    protected Collection<StubIndexKey<String, ? extends BallerinaNamedElement>> getExtraIndexKeys() {
        return EXTRA_KEYS;
    }
}
