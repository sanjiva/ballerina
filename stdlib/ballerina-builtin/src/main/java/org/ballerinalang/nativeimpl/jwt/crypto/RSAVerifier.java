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

package org.ballerinalang.nativeimpl.jwt.crypto;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

/**
 * Verify signature with RSA algorithms. Expects a public RSA key.
 *
 * <p>See RFC 7518, sections
 * <a href="https://tools.ietf.org/html/rfc7518#section-3.3">3.3</a> for more
 * information.
 *
 * @since 0.964.0
 */
public class RSAVerifier implements JWSVerifier {

    /**
     * The public RSA key.
     */
    private final RSAPublicKey publicKey;

    public RSAVerifier(final RSAPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public boolean verify(String data, String signature, String algorithm) throws JWSException {
        final Signature signatureVerifier;
        byte[] dataInBytes = data.getBytes(StandardCharsets.UTF_8);
        byte[] signatureData = Base64.getUrlDecoder().decode(signature.getBytes(StandardCharsets.UTF_8));
        String alg = RSASSAProvider.getJCAAlgorithmName(algorithm);
        try {
            signatureVerifier = Signature.getInstance(alg);
            signatureVerifier.initVerify(publicKey);
            signatureVerifier.update(dataInBytes);
            return signatureVerifier.verify(signatureData);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            throw new JWSException(e.getMessage(), e);
        }
    }
}
