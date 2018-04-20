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
package ballerina.grpc;

public type Chunking "AUTO"|"ALWAYS"|"NEVER";

@final public Chunking CHUNKING_AUTO = "AUTO";
@final public Chunking CHUNKING_ALWAYS = "ALWAYS";
@final public Chunking CHUNKING_NEVER = "NEVER";

public type Compression "AUTO"|"ALWAYS"|"NEVER";

@final public Compression COMPRESSION_AUTO = "AUTO";
@final public Compression COMPRESSION_ALWAYS = "ALWAYS";
@final public Compression COMPRESSION_NEVER = "NEVER";

public type TransferEncoding "CHUNKING";

@final public TransferEncoding TRANSFERENCODE_CHUNKING = "CHUNKING";

@Description {value:
"TrustStore record represents trust store related options to be used for HTTP client/service invocation"}
@Field {value:"filePath: File path to trust store file"}
@Field {value:"password: Trust store password"}
public type TrustStore {
    string path,
    string password,
};

@Description {value:"KeyStore record represents key store related options to be used for HTTP client/service invocation"
}
@Field {value:"filePath: File path to key store file"}
@Field {value:"password: Key store password"}
public type KeyStore {
    string path,
    string password,
};

@Description {value:
"Protocols record represents SSL/TLS protocol related options to be used for HTTP client/service invocation"}
@Field {value:"name: SSL Protocol to be used. eg: TLS1.2"}
@Field {value:"versions: SSL/TLS protocols to be enabled. eg: TLSv1,TLSv1.1,TLSv1.2"}
public type Protocols {
    string name,
    string[] versions,
};

@Description {value:"ValidateCert record represents options related to check whether a certificate is revoked or not"}
@Field {value:"enable: The status of validateCertEnabled"}
@Field {value:"cacheSize: Maximum size of the cache"}
@Field {value:"cacheValidityPeriod: Time duration of cache validity period"}
public type ValidateCert {
    boolean enable,
    int cacheSize,
    int cacheValidityPeriod,
};

@Description {value:"OcspStapling record represents options related to check whether a certificate is revoked or not"}
@Field {value:"enable: The status of OcspStapling"}
@Field {value:"cacheSize: Maximum size of the cache"}
@Field {value:"cacheValidityPeriod: Time duration of cache validity period"}
public type ServiceOcspStapling {
    boolean enable,
    int cacheSize,
    int cacheValidityPeriod,
};

@Description {value:"Represent all http payload related errors"}
@Field {value:"message: The error message"}
@Field {value:"cause: The error which caused the entity error"}
public type PayloadError {
    string message,
    error? cause,
};