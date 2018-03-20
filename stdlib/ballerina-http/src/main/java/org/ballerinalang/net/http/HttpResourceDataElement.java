/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.net.http;

import io.netty.handler.codec.http.HttpHeaderNames;
import org.ballerinalang.net.uri.DispatcherUtil;
import org.ballerinalang.net.uri.parser.DataElement;
import org.ballerinalang.net.uri.parser.DataReturnAgent;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.wso2.transport.http.netty.message.HTTPCarbonMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Http Node Item for URI template tree.
 */
public class HttpResourceDataElement implements DataElement<HttpResource, HTTPCarbonMessage> {

    private List<HttpResource> resource;
    private boolean isFirstTraverse = true;
    private boolean hasData = false;

    @Override
    public boolean hasData() {
        return hasData;
    }

    @Override
    public void setData(HttpResource newResource) {
        if (isFirstTraverse) {
            this.resource = new ArrayList<>();
            this.resource.add(newResource);
            isFirstTraverse = false;
            hasData = true;
            return;
        }
        List<String> newMethods = newResource.getMethods();
        if (newMethods == null) {
            for (HttpResource previousResource : this.resource) {
                if (previousResource.getMethods() == null) {
                    //if both resources do not have methods but same URI, then throw following error.
                    throw new BallerinaException("Two resources have the same addressable URI, "
                                                     + previousResource.getName() + " and " + newResource.getName());
                }
            }
            this.resource.add(newResource);
            hasData = true;
            return;
        }
        this.resource.forEach(r -> {
            for (String newMethod : newMethods) {
                if (DispatcherUtil.isMatchingMethodExist(r, newMethod)) {
                    throw new BallerinaException("Two resources have the same addressable URI, "
                                                         + r.getName() + " and " + newResource.getName());
                }
            }
        });
        this.resource.add(newResource);
        hasData = true;
    }

    @Override
    public boolean getData(HTTPCarbonMessage carbonMessage, DataReturnAgent<HttpResource> dataReturnAgent) {
        try {
            if (this.resource == null) {
                return false;
            }
            HttpResource resource = validateHTTPMethod(this.resource, carbonMessage);
            if (resource == null) {
                return false;
            }
            validateConsumes(resource, carbonMessage);
            validateProduces(resource, carbonMessage);
            dataReturnAgent.setData(resource);
            return true;
        } catch (BallerinaException e) {
            dataReturnAgent.setError(e);
            return false;
        }
    }

    private HttpResource validateHTTPMethod(List<HttpResource> resources, HTTPCarbonMessage carbonMessage) {
        HttpResource resource = null;
        boolean isOptionsRequest = false;
        String httpMethod = (String) carbonMessage.getProperty(HttpConstants.HTTP_METHOD);
        for (HttpResource resourceInfo : resources) {
            if (DispatcherUtil.isMatchingMethodExist(resourceInfo, httpMethod)) {
                resource = resourceInfo;
                break;
            }
        }
        if (resource == null) {
            resource = tryMatchingToDefaultVerb(resources);
        }
        if (resource == null) {
            isOptionsRequest = setAllowHeadersIfOPTIONS(httpMethod, carbonMessage);
        }
        if (resource != null) {
            return resource;
        }
        if (!isOptionsRequest) {
            carbonMessage.setProperty(HttpConstants.HTTP_STATUS_CODE, 405);
            throw new BallerinaException("Method not allowed");
        }
        return null;
    }

    private HttpResource tryMatchingToDefaultVerb(List<HttpResource> resources) {
        for (HttpResource resourceInfo : resources) {
            if (resourceInfo.getMethods() == null) {
                //this means, no method mentioned in the dataElement, hence it has all the methods by default.
                return resourceInfo;
            }
        }
        return null;
    }

    private boolean setAllowHeadersIfOPTIONS(String httpMethod, HTTPCarbonMessage cMsg) {
        if (httpMethod.equals(HttpConstants.HTTP_METHOD_OPTIONS)) {
            cMsg.setHeader(HttpHeaderNames.ALLOW.toString(), getAllowHeaderValues(cMsg));
            return true;
        }
        return false;
    }

    private String getAllowHeaderValues(HTTPCarbonMessage cMsg) {
        List<String> methods = new ArrayList<>();
        List<HttpResource> resourceInfos = new ArrayList<>();
        for (HttpResource resourceInfo : this.resource) {
            if (resourceInfo.getMethods() != null) {
                methods.addAll(resourceInfo.getMethods());
            }
            resourceInfos.add(resourceInfo);
        }
        cMsg.setProperty(HttpConstants.PREFLIGHT_RESOURCES, resourceInfos);
        methods = DispatcherUtil.validateAllowMethods(methods);
        return DispatcherUtil.concatValues(methods, false);
    }

    public HttpResource validateConsumes(HttpResource resource, HTTPCarbonMessage cMsg) {
        String contentMediaType = extractContentMediaType(cMsg.getHeader(HttpHeaderNames.CONTENT_TYPE.toString()));
        List<String> consumesList = resource.getConsumes();

        if (consumesList == null) {
            return resource;
        }
        //when Content-Type header is not set, treat it as "application/octet-stream"
        contentMediaType = (contentMediaType != null ? contentMediaType : HttpConstants.VALUE_ATTRIBUTE);
        for (String consumeType : consumesList) {
            if (contentMediaType.equals(consumeType.trim())) {
                return resource;
            }
        }
        cMsg.setProperty(HttpConstants.HTTP_STATUS_CODE, 415);
        throw new BallerinaException();
    }

    private String extractContentMediaType(String header) {
        if (header == null) {
            return null;
        }
        if (header.contains(";")) {
            header = header.substring(0, header.indexOf(";")).trim();
        }
        return header;
    }

    public HttpResource validateProduces(HttpResource resource, HTTPCarbonMessage cMsg) {
        List<String> acceptMediaTypes = extractAcceptMediaTypes(cMsg.getHeader(HttpHeaderNames.ACCEPT.toString()));
        List<String> producesList = resource.getProduces();

        if (producesList == null || acceptMediaTypes == null) {
            return resource;
        }
        //If Accept header field is not present, then it is assumed that the client accepts all media types.
        if (acceptMediaTypes.contains("*/*")) {
            return resource;
        }
        if (acceptMediaTypes.stream().anyMatch(mediaType -> mediaType.contains("/*"))) {
            List<String> subTypeWildCardMediaTypes = acceptMediaTypes.stream()
                    .filter(mediaType -> mediaType.contains("/*"))
                    .map(mediaType -> mediaType.substring(0, mediaType.indexOf("/")))
                    .collect(Collectors.toList());
            for (String token : resource.getProducesSubTypes()) {
                if (subTypeWildCardMediaTypes.contains(token)) {
                    return resource;
                }
            }
        }
        List<String> noWildCardMediaTypes = acceptMediaTypes.stream()
                .filter(mediaType -> !mediaType.contains("/*")).collect(Collectors.toList());
        for (String produceType : producesList) {
            if (noWildCardMediaTypes.contains(produceType)) {
                return resource;
            }
        }
        cMsg.setProperty(HttpConstants.HTTP_STATUS_CODE, 406);
        throw new BallerinaException();
    }

    private List<String> extractAcceptMediaTypes(String header) {
        List<String> acceptMediaTypes = new ArrayList();
        if (header == null) {
            return null;
        }
        if (header.contains(",")) {
            //process headers like this: text/*;q=0.3, text/html;Level=1;q=0.7, */*
            acceptMediaTypes = Arrays.stream(header.split(","))
                    .map(mediaRange -> mediaRange.contains(";") ? mediaRange
                            .substring(0, mediaRange.indexOf(";")) : mediaRange)
                    .map(String::trim).distinct().collect(Collectors.toList());
        } else if (header.contains(";")) {
            //process headers like this: text/*;q=0.3
            acceptMediaTypes.add(header.substring(0, header.indexOf(";")).trim());
        } else {
            acceptMediaTypes.add(header.trim());
        }
        return acceptMediaTypes;
    }
}
