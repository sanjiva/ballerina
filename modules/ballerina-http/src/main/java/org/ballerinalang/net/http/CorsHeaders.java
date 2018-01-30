/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.net.http;

import java.util.List;

/**
 * {@code CorsHeaders} This is the cors headers holder.
 *
 * @since 0.94
 */
public class CorsHeaders {
    private boolean available;
    private List<String> allowOrigins;
    private int allowCredentials; //Not found = -1, false = 0, true = 1
    private List<String> allowMethods;
    private List<String> allowHeaders;
    private long maxAge;
    private List<String> exposeHeaders;

    public CorsHeaders() {
        available = false;
        allowCredentials = -1;
        maxAge = -1;
    }

    public boolean isAvailable() {
        return available;
    }

    public List<String> getAllowOrigins() {
        return allowOrigins;
    }

    public void setAllowOrigins(List<String> allowOrigins) {
        if (allowOrigins != null) {
            available = true;
        }
        this.allowOrigins = allowOrigins;
    }

    public int getAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(int allowCredentials) {
        if (allowCredentials >= 0) {
            available = true;
        }
        this.allowCredentials = allowCredentials;
    }

    public List<String> getAllowMethods() {
        return allowMethods;
    }

    public void setAllowMethods(List<String> allowMethods) {
        if (allowMethods != null) {
            available = true;
        }
        this.allowMethods = allowMethods;
    }

    public List<String> getAllowHeaders() {
        return allowHeaders;
    }

    public void setAllowHeaders(List<String> allowHeaders) {
        if (allowHeaders != null) {
            available = true;
        }
        this.allowHeaders = allowHeaders;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        if (maxAge >= 0) {
            available = true;
        }
        this.maxAge = maxAge;
    }

    public List<String> getExposeHeaders() {
        return exposeHeaders;
    }

    public void setExposeHeaders(List<String> exposeHeaders) {
        if (exposeHeaders != null) {
            available = true;
        }
        this.exposeHeaders = exposeHeaders;
    }
}
