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
package org.ballerinalang.nativeimpl.actions;

import org.ballerinalang.connector.api.BallerinaConnectorException;
import org.ballerinalang.connector.api.ConnectorFuture;
import org.ballerinalang.connector.api.ConnectorFutureListener;
import org.ballerinalang.model.values.BValue;

/**
 * {@code ClientConnectorFuture} This connector future will be provided to the ballerina side to get notifications
 * from client connections.
 *
 * @since 0.94
 */
public class ClientConnectorFuture implements ConnectorFuture {
    private ConnectorFutureListener listener;
    private boolean success = false;
    private BValue[] value;
    private BallerinaConnectorException exception;

    @Override
    public void setConnectorFutureListener(ConnectorFutureListener futureListener) {
        this.listener = futureListener;
        if (value != null) {
            listener.notifyReply(value);
        } else if (exception != null) {
            listener.notifyFailure(exception);
            success = false; //double check this.
        }
        if (success) {
            listener.notifySuccess();
        }
        value = null;
        exception = null;
        success = false;
    }

    public void notifySuccess() {
        if (listener != null) {
            listener.notifySuccess();
            return;
        }
        this.success = true;
    }

    public void notifyReply(BValue... value) {
        if (listener != null) {
            listener.notifyReply(value);
            return;
        }
        this.value = value;
    }

    public void notifyFailure(BallerinaConnectorException ex) {
        if (listener != null) {
            listener.notifyFailure(ex);
            return;
        }
        this.exception = ex;
    }
}
