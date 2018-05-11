/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

// This is a generated file. Not intended for manual editing.
package org.ballerinalang.plugins.idea.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.ballerinalang.plugins.idea.psi.impl.BallerinaTopLevelDefinition;
import com.intellij.psi.StubBasedPsiElement;
import org.ballerinalang.plugins.idea.stubs.BallerinaEndpointDefinitionStub;

public interface BallerinaEndpointDefinition extends BallerinaNamedElement, BallerinaTopLevelDefinition, StubBasedPsiElement<BallerinaEndpointDefinitionStub> {

  @NotNull
  List<BallerinaAnnotationAttachment> getAnnotationAttachmentList();

  @Nullable
  BallerinaEndpointInitialization getEndpointInitialization();

  @Nullable
  BallerinaEndpointType getEndpointType();

  @Nullable
  PsiElement getSemicolon();

  @NotNull
  PsiElement getEndpoint();

  @Nullable
  PsiElement getIdentifier();

}
