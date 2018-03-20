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

/**
 * JSON web signature.
 *
 * @since 0.964.0
 */
public interface JWSSigner {

    /**
     * Signs the specified input data.
     *
     * @param data      This input should contain the header and body part of the JWT.
     *                  BASE64URL(UTF8(JOSE header)) || '.' || BASE64URL(JWS payload)
     * @param algorithm JWS algorithm used to secure the JWS.
     *                  This is the 'alg' header parameter.
     * @return The resulting signature part (third part) of the JWS object.
     * @throws JWSException If the JWS algorithm is not supported or if
     *                      signing failed for some other internal reason.
     */
    String sign(final String data, final String algorithm) throws JWSException;
}
