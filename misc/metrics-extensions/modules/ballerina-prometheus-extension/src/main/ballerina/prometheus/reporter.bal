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

import ballerina/http;
import ballerina/observe;
import ballerina/config;

@final string METRIC_TYPE_GAUGE = "gauge";
@final string METRIC_TYPE_SUMMARY = "summary";
@final string EMPTY_STRING = "";

@final string PROMETHEUS_PORT_CONFIG = "b7a.observability.metrics.prometheus.port";
@final string PROMETHEUS_HOST_CONFIG = "b7a.observability.metrics.prometheus.host";
@final int REPORTER_PORT = config:getAsInt(PROMETHEUS_PORT_CONFIG, default = 9797);
@final string REPORTER_HOST = config:getAsString(PROMETHEUS_HOST_CONFIG, default = "0.0.0.0");

@final string EXPIRY_TAG = "timeWindow";
@final string PERCENTILE_TAG = "quantile";

endpoint http:Listener prometheusListener {
    host: REPORTER_HOST,
    port: REPORTER_PORT
};

@http:ServiceConfig {
    basePath: "/metrics"
}
//documentation {
//    The Prometheus service reporter. This service will be called periodically by prometheus server.
//}
service<http:Service> PrometheusReporter bind prometheusListener {

    @http:ResourceConfig {
        methods: ["GET"],
        path: "/",
        produces: ["application/text"]
    }
    documentation{
        This method retrieves all metrics registered in the ballerina metrics registry,
        and reformats based on the expected format by prometheus server.
    }
    getMetrics(endpoint caller, http:Request req) {
        observe:Metric[] metrics = observe:getAllMetrics();
        string payload = EMPTY_STRING;
        foreach metric in metrics {
            string  qualifiedMetricName = metric.name.replaceAll("/", "_");
            string metricReportName = getMetricName(qualifiedMetricName, "value");
            payload += generateMetricHelp(metricReportName, metric.desc);
            payload += generateMetricInfo(metricReportName, metric.metricType);
            payload += generateMetric(metricReportName, metric.tags, metric.value);
            if (metric.metricType.equalsIgnoreCase(METRIC_TYPE_GAUGE) && metric.summary != null){
                map<string> tags = metric.tags;
                observe:Snapshot[]? summaries = metric.summary;
                match summaries {
                    observe:Snapshot[] snapshots => {
                        foreach aSnapshot in snapshots{
                            tags[EXPIRY_TAG] = <string>aSnapshot.timeWindow;
                            payload += generateMetricHelp(qualifiedMetricName, "A Summary of " +  qualifiedMetricName
                                    + " for window of " + aSnapshot.timeWindow);
                            payload += generateMetricInfo(qualifiedMetricName, METRIC_TYPE_SUMMARY);
                            payload += generateMetric(getMetricName(qualifiedMetricName, "mean"), tags, aSnapshot.mean);
                            payload += generateMetric(getMetricName(qualifiedMetricName, "max"), tags, aSnapshot.max);
                            payload += generateMetric(getMetricName(qualifiedMetricName, "min"), tags, aSnapshot.min);
                            payload += generateMetric(getMetricName(qualifiedMetricName, "stdDev"), tags,
                                       aSnapshot.stdDev);
                            foreach percentileValue in aSnapshot.percentileValues  {
                                tags[PERCENTILE_TAG] = <string>percentileValue.percentile;
                                payload += generateMetric(qualifiedMetricName, tags, percentileValue.value);
                            }
                            _ = tags.remove(EXPIRY_TAG);
                            _ = tags.remove(PERCENTILE_TAG);
                        }
                    }
                    () => {payload += "\n";}
                }
            }
        }
        http:Response res = new;
        res.setPayload(payload);
        _ = caller->respond(res);
    }
}

documentation{
    This util function creates the type description based on the prometheus format for the specific metric.

    R{{metric}} Formatted metric information.
}
function generateMetricInfo(string name, string metricType) returns string {
    return "# TYPE " + name + " " + metricType + "\n";
}

documentation{
    This util function creates the metric help description based on the prometheus format for the specific metric.

    R{{metric}} Formatted metric help information.
}
function generateMetricHelp(string name, string description) returns string {
    if (!description.equalsIgnoreCase(EMPTY_STRING)) {
        return "# HELP " + name + " " + description + "\n";
    }
    return EMPTY_STRING;
}

documentation{
    This util function creates the metric along with its name, labels, and values based on the prometheus
    format for the specific metric.

    R{{metric}} Formatted metric values.
}
function generateMetric(string name, map<string>? labels, int|float value) returns string {
    string strValue = "";
    match value {
        int intValue => strValue = (<string>intValue) + ".0";
        float floatValue => strValue = <string>floatValue;
    }
    match labels {
        map<string> mapLabels => {
            string strLabels = getLabelsString(mapLabels);
            return (name + strLabels + " " + strValue + "\n");
        }
        () => {
            return (name + " " + strValue + "\n");
        }
    }
}

function getLabelsString(map<string> labels) returns string {
    string stringLabel = "{";
    foreach key, value in labels {
        string labelKey = key.replaceAll("\\.", "_");
        string entry = labelKey + "=\"" + value + "\"";
        stringLabel += (entry + ",");
    }
    if (stringLabel != "{") {
        return (stringLabel + "}");
    } else {
        return "";
    }
}

documentation{
    This utils function generates the name for the summary metric type.

    R{{metric}} Formatted metric name.
}
function getMetricName(string name, string summaryType) returns string {
    return name + "_" + summaryType;
}
