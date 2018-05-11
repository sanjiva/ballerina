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
package org.ballerinalang.plugins.idea.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.ballerinalang.plugins.idea.psi.BallerinaTypes.*;
import org.ballerinalang.plugins.idea.stubs.BallerinaFunctionDefinitionStub;
import org.ballerinalang.plugins.idea.psi.*;
import com.intellij.psi.stubs.IStubElementType;

public class BallerinaFunctionDefinitionImpl extends BallerinaNamedElementImpl<BallerinaFunctionDefinitionStub> implements BallerinaFunctionDefinition {

  public BallerinaFunctionDefinitionImpl(BallerinaFunctionDefinitionStub stub, IStubElementType type) {
    super(stub, type);
  }

  public BallerinaFunctionDefinitionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull BallerinaVisitor visitor) {
    visitor.visitFunctionDefinition(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BallerinaVisitor) accept((BallerinaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public BallerinaAttachedObject getAttachedObject() {
    return PsiTreeUtil.getChildOfType(this, BallerinaAttachedObject.class);
  }

  @Override
  @Nullable
  public BallerinaCallableUnitBody getCallableUnitBody() {
    return PsiTreeUtil.getChildOfType(this, BallerinaCallableUnitBody.class);
  }

  @Override
  @Nullable
  public BallerinaCallableUnitSignature getCallableUnitSignature() {
    return PsiTreeUtil.getChildOfType(this, BallerinaCallableUnitSignature.class);
  }

  @Override
  @Nullable
  public BallerinaParameter getParameter() {
    return PsiTreeUtil.getChildOfType(this, BallerinaParameter.class);
  }

  @Override
  @Nullable
  public PsiElement getDoubleColon() {
    return findChildByType(DOUBLE_COLON);
  }

  @Override
  @Nullable
  public PsiElement getGt() {
    return findChildByType(GT);
  }

  @Override
  @Nullable
  public PsiElement getLt() {
    return findChildByType(LT);
  }

  @Override
  @Nullable
  public PsiElement getSemicolon() {
    return findChildByType(SEMICOLON);
  }

  @Override
  @NotNull
  public PsiElement getFunction() {
    return notNullChild(findChildByType(FUNCTION));
  }

  @Override
  @Nullable
  public PsiElement getNative() {
    return findChildByType(NATIVE);
  }

  @Override
  @Nullable
  public PsiElement getPublic() {
    return findChildByType(PUBLIC);
  }

  @Nullable
  public PsiElement getIdentifier() {
    return BallerinaPsiImplUtil.getIdentifier(this);
  }

  @Nullable
  public String getName() {
    return BallerinaPsiImplUtil.getName(this);
  }

}
