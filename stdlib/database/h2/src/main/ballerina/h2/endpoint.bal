// Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/sql;

documentation {
    The Client endpoint configuration for h2 databases.

    F{{host}} The host name of the database to connect (in case of server based DB)
    F{{path}} The path of the database connection (in case of file based DB)
    F{{port}} The port of the database to connect (in case of server based DB)
    F{{name}} The name of the database to connect
    F{{username}} Username for the database connection
    F{{password}} Password for the database connection
    F{{poolOptions}} Properties for the connection pool configuration. Refer `sql:PoolOptions` for more details
    F{{dbOptions}} A map of DB specific properties
}
public type ClientEndpointConfiguration record {
    string host,
    string path,
    int port = 9092,
    string name,
    string username,
    string password,
    sql:PoolOptions poolOptions,
    map dbOptions,
};


documentation {
    Represents an H2 client endpoint.

    F{{config}} The configurations associated with the SQL endpoint
}

public type Client object {
    private ClientEndpointConfiguration config;
    private sql:CallerActions h2Client;

    documentation {
        Gets called when the endpoint is being initialized during the package initialization.

        P{{c}} The ClientEndpointConfiguration of the endpoint
    }
    function init(ClientEndpointConfiguration c) {
        self.h2Client = createClient(c);
    }

    documentation {
        Returns the connector that the client code uses.
    }
    public function getCallerActions() returns sql:CallerActions {
        return self.h2Client;
    }

    documentation {
        Stops the JDBC client.
    }
    public function stop() {
        sql:close(self.h2Client);
    }
};

extern function createClient(ClientEndpointConfiguration config) returns sql:CallerActions;
