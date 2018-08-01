/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.test.services.testutils;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.wso2.transport.http.netty.message.HttpCarbonMessage;

/**
 * Extended HttpCarbonMessage to handle error path.
 */
public class HTTPTestRequest extends HttpCarbonMessage {

    private TestCallableUnitCallback callback;
    private HttpCarbonMessage httpCarbonMessage;
    private TestHttpResponseStatusFuture testHttpResponseStatusFuture;

    public HTTPTestRequest() {
        super(new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, ""));
        testHttpResponseStatusFuture = new TestHttpResponseStatusFuture();
    }

    @Override
    public TestHttpResponseStatusFuture respond(HttpCarbonMessage httpCarbonMessage) {
        this.httpCarbonMessage = httpCarbonMessage;
        if (callback != null) {
            callback.setResponseMsg(httpCarbonMessage);
            this.httpCarbonMessage = null;
        }
        return testHttpResponseStatusFuture;
    }

    public void setCallback(TestCallableUnitCallback callback) {
        this.callback = callback;
        if (httpCarbonMessage != null) {
            this.callback.setResponseMsg(httpCarbonMessage);
        }
    }

    public TestHttpResponseStatusFuture getTestHttpResponseStatusFuture() {
        return testHttpResponseStatusFuture;
    }
}
