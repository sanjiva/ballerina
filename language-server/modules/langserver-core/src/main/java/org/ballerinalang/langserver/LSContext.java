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
package org.ballerinalang.langserver;

/**
 * Ballerina Language server context.
 * @since 0.970.0
 */
public interface LSContext {

    /**
     * Add new Context property.
     * @param key   Property Key
     * @param value Property value
     * @param <V>   Key Type
     */
    <V> void put(Key<V> key, V value);

    /**
     * Get property by Key.
     * @param key   Property Key
     * @param <V>   Key Type
     * @return {@link Object}   Property
     */
    <V> V get(Key<V> key);

    /**
     * @param <K>
     * @since 0.95.5
     */
    class Key<K> {
    }
}
