package org.ballerinalang.plugins.idea.psi.scopeprocessors;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;
import org.ballerinalang.plugins.idea.completion.BallerinaCompletionUtils;
import org.ballerinalang.plugins.idea.psi.BallerinaFieldDefinition;
import org.ballerinalang.plugins.idea.psi.BallerinaFieldDefinitionList;
import org.ballerinalang.plugins.idea.psi.BallerinaFiniteType;
import org.ballerinalang.plugins.idea.psi.BallerinaFiniteTypeUnit;
import org.ballerinalang.plugins.idea.psi.BallerinaObjectBody;
import org.ballerinalang.plugins.idea.psi.BallerinaPrivateObjectFields;
import org.ballerinalang.plugins.idea.psi.BallerinaPublicObjectFields;
import org.ballerinalang.plugins.idea.psi.BallerinaRecordTypeName;
import org.ballerinalang.plugins.idea.psi.BallerinaTypeDefinition;
import org.ballerinalang.plugins.idea.psi.BallerinaTypeName;
import org.ballerinalang.plugins.idea.psi.impl.BallerinaPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Responsible for resolving and completing object fields.
 */
public class BallerinaObjectFieldProcessor extends BallerinaScopeProcessorBase {

    @Nullable
    private final CompletionResultSet myResult;
    @NotNull
    private final PsiElement myElement;

    public BallerinaObjectFieldProcessor(@Nullable CompletionResultSet result, @NotNull PsiElement element,
                                         boolean isCompletion) {
        super(element, element, isCompletion);
        myResult = result;
        myElement = element;
    }

    protected boolean accept(@NotNull PsiElement element) {
        return element instanceof BallerinaTypeDefinition;
    }

    @Override
    public boolean execute(@NotNull PsiElement element, @NotNull ResolveState state) {
        if (accept(element)) {
            BallerinaObjectBody ballerinaObjectBody = PsiTreeUtil.findChildOfType(element, BallerinaObjectBody.class);
            if (ballerinaObjectBody != null) {
                processObjectDefinition(ballerinaObjectBody);
            } else {
                PsiElement owner = ((BallerinaTypeDefinition) element).getIdentifier();
                if (owner == null) {
                    return true;
                }
                BallerinaFiniteType ballerinaFiniteType = PsiTreeUtil.getChildOfType(element,
                        BallerinaFiniteType.class);
                if (ballerinaFiniteType == null) {
                    return true;
                }

                List<BallerinaFiniteTypeUnit> finiteTypeUnitList = ballerinaFiniteType.getFiniteTypeUnitList();
                for (BallerinaFiniteTypeUnit ballerinaFiniteTypeUnit : finiteTypeUnitList) {
                    BallerinaTypeName typeName = ballerinaFiniteTypeUnit.getTypeName();
                    if (typeName instanceof BallerinaRecordTypeName) {
                        BallerinaFieldDefinitionList fieldDefinitionList =
                                ((BallerinaRecordTypeName) typeName).getFieldDefinitionList();
                        List<BallerinaFieldDefinition> fieldList = fieldDefinitionList.getFieldDefinitionList();
                        for (BallerinaFieldDefinition ballerinaFieldDefinition : fieldList) {
                            PsiElement identifier = ballerinaFieldDefinition.getIdentifier();
                            if (myResult != null) {
                                BallerinaTypeName fieldTypeName = ballerinaFieldDefinition.getTypeName();
                                String type;
                                if (fieldTypeName instanceof BallerinaRecordTypeName) {
                                    type = "record {}";
                                } else {
                                    type = fieldTypeName.getText();
                                }
                                myResult.addElement(BallerinaCompletionUtils.createFieldLookupElement(identifier, owner,
                                        type, BallerinaPsiImplUtil.getObjectFieldDefaultValue(ballerinaFieldDefinition),
                                        false));
                            } else if (myElement.getText().equals(identifier.getText())) {
                                add(identifier);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    // Todo - Merge with function in BallerinaBlockProcessor
    private void processObjectDefinition(@NotNull BallerinaObjectBody ballerinaObjectBody) {
        BallerinaTypeDefinition ballerinaTypeDefinition = PsiTreeUtil.getParentOfType(ballerinaObjectBody,
                BallerinaTypeDefinition.class);
        if (ballerinaTypeDefinition == null || ballerinaTypeDefinition.getIdentifier() == null) {
            return;
        }

        BallerinaPublicObjectFields publicObjectFields = ballerinaObjectBody.getPublicObjectFields();
        if (publicObjectFields != null) {
            processObjectFields(ballerinaTypeDefinition.getIdentifier(), publicObjectFields.getFieldDefinitionList(),
                    true);
        }
        BallerinaPrivateObjectFields privateObjectFields = ballerinaObjectBody.getPrivateObjectFields();
        if (privateObjectFields != null) {
            processObjectFields(ballerinaTypeDefinition.getIdentifier(), privateObjectFields.getFieldDefinitionList(),
                    false);
        }
    }

    // Todo - Merge with function in BallerinaBlockProcessor
    private void processObjectFields(@NotNull PsiElement typeName,
                                     @NotNull List<BallerinaFieldDefinition> fieldDefinitionList,
                                     boolean isPublic) {
        for (BallerinaFieldDefinition ballerinaFieldDefinition : fieldDefinitionList) {
            PsiElement identifier = ballerinaFieldDefinition.getIdentifier();
            if (myResult != null) {
                myResult.addElement(BallerinaCompletionUtils.createFieldLookupElement(identifier, typeName,
                        ballerinaFieldDefinition.getTypeName().getText(),
                        BallerinaPsiImplUtil.getObjectFieldDefaultValue(ballerinaFieldDefinition), isPublic));
            } else if (myElement.getText().equals(identifier.getText())) {
                add(identifier);
            }
            if (!isCompletion() && getResult() != null) {
                return;
            }
        }
    }

    @Override
    public boolean isCompletion() {
        return myIsCompletion;
    }

    @Override
    protected boolean crossOff(@NotNull PsiElement e) {
        return false;
    }
}
