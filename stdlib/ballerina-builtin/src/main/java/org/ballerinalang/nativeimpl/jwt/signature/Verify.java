/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.nativeimpl.jwt.signature;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BLangVMErrors;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BBoolean;
import org.ballerinalang.nativeimpl.jwt.crypto.JWSVerifier;
import org.ballerinalang.nativeimpl.jwt.crypto.RSAVerifier;
import org.ballerinalang.nativeimpl.security.KeyStore;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;

import java.security.interfaces.RSAPublicKey;

/**
 * Native function ballerinalang.jwt.signature:verify.
 *
 * @since 0.964.0
 */
@BallerinaFunction(
        packageName = "ballerina.jwt.signature",
        functionName = "verify",
        args = {
                @Argument(name = "data", type = TypeKind.STRING),
                @Argument(name = "signature", type = TypeKind.STRING),
                @Argument(name = "algorithm", type = TypeKind.STRING),
                @Argument(name = "keyAlias", type = TypeKind.STRING)
        },
        returnType = {@ReturnType(type = TypeKind.BOOLEAN)},
        isPublic = true
)
public class Verify extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context context) {
        String data = context.getStringArgument(0);
        String signature = context.getStringArgument(1);
        String algorithm = context.getStringArgument(2);
        String keyAlias = context.getStringArgument(3);
        Boolean validSignature = null;
        RSAPublicKey publicKey;
        try {
            publicKey = (RSAPublicKey) KeyStore.getKeyStore().getTrustedPublicKey(keyAlias);
            JWSVerifier verifier = new RSAVerifier(publicKey);
            validSignature = verifier.verify(data, signature, algorithm);
            context.setReturnValues(new BBoolean(validSignature));
        } catch (Exception e) {
            context.setReturnValues(new BBoolean(false), BLangVMErrors.createError(context, 0, e.getMessage()));
        }        
    }
}
