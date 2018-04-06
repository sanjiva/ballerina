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

package ballerina.http;

// Cache-control directives
@Description {value:"Forces the cache to validate a cached response with the origin server before serving."}
@final public string NO_CACHE = "no-cache";
@Description {value:"Instructs the cache to not store a response in non-volatile storage."}
@final public string NO_STORE = "no-store";
@Description {value:"Instructs intermediaries not to transform the payload."}
@final public string NO_TRANSFORM = "no-transform";
@Description {value:"When used in requests, it implies that clients are not willing to accept responses whose age is greater than max-age. When used in responses, the response is to be considered stale after the specified number of seconds."}
@final public string MAX_AGE = "max-age";

// Request only cache-control directives
@Description {value:"Indicates that the client is willing to accept responses which have exceeded their freshness lifetime by no more than the specified number of seconds."}
@final public string MAX_STALE = "max-stale";
@Description {value:"Indicates that the client is only accepting responses whose freshness lifetime >= current age + min-fresh"}
@final public string MIN_FRESH = "min-fresh";
@Description {value:"Indicates that the client is only willing to accept a cached response. A cached response is served subject to other constraints posed by the request."}
@final public string ONLY_IF_CACHED = "only-if-cached";

// Response only cache-control directives
@Description {value:"Indicates that once the response has become stale, it should not be reused for subsequent requests without validating with the origin server."}
@final public string MUST_REVALIDATE = "must-revalidate";
@Description {value:"Indicates that any cache may store the response."}
@final public string PUBLIC = "public";
@Description {value:"Indicates that the response is intended for a single user and should not be stored by shared caches."}
@final public string PRIVATE = "private";
@Description {value:"Has the same meaning as must-revalidate, except that this does not apply to private caches."}
@final public string PROXY_REVALIDATE = "proxy-revalidate";
@Description {value:"In shared caches, this overrides the max-age or Expires header field"}
@final public string S_MAX_AGE = "s-maxage";

@Description {value:"A constant for indicating that the max-stale directive does not specify a limit."}
@final public int MAX_STALE_ANY_AGE = 9223372036854775807;

@Description {value:"Cache control directives configuration for requests"}
@Field {value:"noCache: Represents the no-cache directive"}
@Field {value:"noStore: Represents the no-store directive"}
@Field {value:"noTransform: Represents the no-transform directive"}
@Field {value:"onlyIfCached: Represents the only-if-cached directive"}
@Field {value:"maxAge: Represents the max-age directive"}
@Field {value:"maxStale: Represents the max-stale directive"}
@Field {value:"minFresh: Represents the min-fresh directive"}
public type RequestCacheControl object {
    public {
        boolean noCache = false;
        boolean noStore = false;
        boolean noTransform = true;
        boolean onlyIfCached = false;
        int maxAge = -1;
        int maxStale = -1;
        int minFresh = -1;
    }

    @Description {value:"Build the cache control directives string from the current request cache control configurations"}
    @Param {value:"cacheControl: The request cache control"}
    public function buildCacheControlDirectives () returns string {
        string[] directives = [];
        int i = 0;

        if (noCache) {
            directives[i] = NO_CACHE;
            i = i + 1;
        }

        if (noStore) {
            directives[i] = NO_STORE;
            i = i + 1;
        }

        if (noTransform) {
            directives[i] = NO_TRANSFORM;
            i = i + 1;
        }

        if (onlyIfCached) {
            directives[i] = ONLY_IF_CACHED;
            i = i + 1;
        }

        if (maxAge >= 0) {
            directives[i] = MAX_AGE + "=" + maxAge;
            i = i + 1;
        }

        if (maxStale == MAX_STALE_ANY_AGE) {
            directives[i] = MAX_STALE;
            i = i + 1;
        } else if (maxStale >= 0) {
            directives[i] = MAX_STALE + "=" + maxStale;
            i = i + 1;
        }

        if (minFresh >= 0) {
            directives[i] = MIN_FRESH + "=" + minFresh;
            i = i + 1;
        }

        return buildCommaSeparatedString(directives);
    }
};

@Description {value:"Cache control directives configuration for responses"}
@Field {value:"mustRevalidate: Represents the must-revalidate directive"}
@Field {value:"noCache: Represents the no-cache directive"}
@Field {value:"noStore: Represents the no-store directive"}
@Field {value:"noTransform: Represents the no-transform directive"}
@Field {value:"isPrivate: Represents the private and public directives"}
@Field {value:"proxyRevalidate: Represents the proxy-revalidate directive"}
@Field {value:"maxAge: Represents the max-age directive"}
@Field {value:"sMaxAge: Represents the s-maxage directive"}
@Field {value:"noCacheFields: Optional fields for no-cache directive. If sending any of the listed fields in a response, they must validated with the origin server."}
@Field {value:"privateFields: Optional fields for private directive. A cache can omit the fields specified and store the rest of the response."}
public type ResponseCacheControl object {
    public {
        boolean mustRevalidate = false;
        boolean noCache = false;
        boolean noStore = false;
        boolean noTransform = true;
        boolean isPrivate = false;
        boolean proxyRevalidate = false;
        int maxAge = -1;
        int sMaxAge = -1;
        string[] noCacheFields = [];
        string[] privateFields = [];
    }

    @Description {value:"Build the cache control directives string from the current response cache control configurations"}
    @Param {value:"cacheControl: The response cache control"}
    public function buildCacheControlDirectives () returns string {
        string[] directives = [];
        int i = 0;

        if (self.mustRevalidate) {
            directives[i] = MUST_REVALIDATE;
            i = i + 1;
        }

        if (self.noCache) {
            directives[i] = NO_CACHE + appendFields(self.noCacheFields);
            i = i + 1;
        }

        if (self.noStore) {
            directives[i] = NO_STORE;
            i = i + 1;
        }

        if (self.noTransform) {
            directives[i] = NO_TRANSFORM;
            i = i + 1;
        }

        if (self.isPrivate) {
            directives[i] = PRIVATE + appendFields(self.privateFields);
        } else {
            directives[i] = PUBLIC;
        }
        i = i + 1;

        if (self.proxyRevalidate) {
            directives[i] = PROXY_REVALIDATE;
            i = i + 1;
        }

        if (self.maxAge >= 0) {
            directives[i] = MAX_AGE + "=" + self.maxAge;
            i = i + 1;
        }

        if (self.sMaxAge >= 0) {
            directives[i] = S_MAX_AGE + "=" + self.sMaxAge;
            i = i + 1;
        }

        return buildCommaSeparatedString(directives);
    }
};

function Request::parseCacheControlHeader () {
    self.cacheControl = {};

    // If the request doesn't contain a cache-control header, resort to default cache control settings
    if (!self.hasHeader(CACHE_CONTROL)) {
        return;
    }

    string cacheControl = self.getHeader(CACHE_CONTROL);
    string[] directives = cacheControl.split(",");

    foreach directive in directives {
        directive = directive.trim();
        if (directive == NO_CACHE) {
            self.cacheControl.noCache = true;
        } else if (directive == NO_STORE) {
            self.cacheControl.noStore = true;
        } else if (directive == NO_TRANSFORM) {
            self.cacheControl.noTransform = true;
        } else if (directive == ONLY_IF_CACHED) {
            self.cacheControl.onlyIfCached = true;
        } else if (directive.hasPrefix(MAX_AGE)) {
            self.cacheControl.maxAge = getExpirationDirectiveValue(directive);
        } else if (directive == MAX_STALE) {
            self.cacheControl.maxStale = MAX_STALE_ANY_AGE;
        } else if (directive.hasPrefix(MAX_STALE)) {
            self.cacheControl.maxStale = getExpirationDirectiveValue(directive);
        } else if (directive.hasPrefix(MIN_FRESH)) {
            self.cacheControl.minFresh = getExpirationDirectiveValue(directive);
        }
        // non-standard directives are ignored
    }
}

function appendFields (string[] fields) returns string {
    if (lengthof fields > 0) {
        return "=\"" + buildCommaSeparatedString(fields) + "\"";
    }
    return "";
}

function buildCommaSeparatedString (string[] values) returns string {
    string delimitedValues = values[0];

    int i = 1;
    while (i < lengthof values) {
        delimitedValues = delimitedValues + ", " + values[i];
        i = i + 1;
    }

    return delimitedValues;
}

function getExpirationDirectiveValue (string directive) returns int {
    string[] directiveParts = directive.split("=");

    // Disregarding the directive if a value isn't provided
    if (lengthof directiveParts != 2) {
        return -1;
    }

    match <int>directiveParts[1] {
        int age => return age;
        error => return -1; // Disregarding the directive if the value cannot be parsed
    }
}
