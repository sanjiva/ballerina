## Package Overview

This package provides apis for observing Ballerina services.
Ballerina supports Observability out of the box. This package provides user api's to make Ballerina Observability more flexible for the user.

To observe ballerina code, the `--observe` flag should be given when starting the service. i.e. `ballerina run hello_world.bal --observe`.
For more information on Ballerina Observability visit [How to Observe Ballerina Services](https://ballerina.io/learn/how-to-observe-ballerina-code/)

## Tracing

### Samples

#### Start a root span & attach a child span

The following code snippet show an example of how start a root span with no parent and start another span as a child of the first span.
Note: Make sure that all started spans are closed properly to ensure that all spans are reported properly.

```ballerina

    int spanId = observe:startRootSpan("Parent Span");
    
    //Do Something
    
    int spanId2 = check observe:startSpan("Child Span", parentSpanId = spanId);
    
    // Do Something
    
    _ = observe:finishSpan(spanId2);
    
    // Do Something
    
    _ = observe:finishSpan(spanId);
```

#### Start a span attached to a system trace

When no parentSpanId is given or a parentSpanId of -1 is given, a span is started as a child span to the current active span in the ootb system trace.

```ballerina
    int spanId = check observe:startSpan("Child Span");
    
    // Do Something
    
    _ = observe:finishSpan(spanId);
```

#### Attach a tag to a span

It is possible to add tags to span by using the `observe:addTagToSpan()` api by providing the span id and relevant tag key and tag value.

```ballerina
    _ = observe:addTagToSpan(spanId, "Tag Key", "Tag Value");
```

## Metrics 
There are mainly two kind of metrics instances supported; Counter and Gauge. A counter is a cumulative metric that 
represents a single monotonically increasing counter whose value can only increase or be reset to zero on restart. 
For example, you can use a counter to represent the number of requests served, tasks completed, or errors. 
The Gauge metric instance represents a single numerical value that can arbitrarily go up and down, and also based on the
statistics configurations provided to the Gauge, it can also report the statistics such as max, min, mean, percentiles, etc. 

### Counter Samples

#### Create
The following code snippets provides the information on how Counter instances can be created. Instantiating the counter 
will simply create an instance based on the params passed. 

```ballerina
    //Create counter with simply by name.
    observe:Counter simpleCounter = new ("SimpleCounter"); 
    
    //Create counter with description.
    observe:Counter counterWithDesc = new ("CounterWithDesc", desc = "This is a sample counter description");
    
    //Create counter with tags.
    map<string> counterTags = { "method": "GET" };
    observe:Counter counterWithTags = new ("CounterWithTags", desc = "Some description", tags = counterTags);

```

#### Register
The counter can registered with the global metrics registry, therefore it can be looked up later without having the 
reference of the counter that was created. Also, only the registered counters will be reported to the Metrics reporter 
such as Prometheus. In case, if there is already another non counter metric registered, 
then there will be an error returned. But if it's another counter instance, then the registered counter instance will 
be returned.

```ballerina
    map<string> counterTags = { "method": "GET" };
    observe:Counter counterWithTags = new ("CounterWithTags", desc = "Some description", tags = counterTags);
    counterWithTags.register() but {
        error e => log:printError("Cannot register the counter", err = e)
    };
    
```

#### Unregister
The counter can unregistered with the global metrics registry if it is already registered. If a metrics is unregistered,
then further it'll not be included in metrics reporting.

```ballerina
    map<string> counterTags = { "method": "GET" };
    observe:Counter counterWithTags = new ("CounterWithTags", desc = "Some description", tags = counterTags);
    counterWithTags.register() but {
        error e => log:printError("Cannot register the counter", err = e)
    };
    counterWithTags.unregister();
    
```

#### Increment
The counter can be incremented without passing any params (defaulted to 1), or by a specific amount. 

```ballerina
    map<string> counterTags = { "method": "GET" };
    observe:Counter counterWithTags = new ("CounterWithTags", desc = "Some description", tags = counterTags);
    //Increment by 1.
    counterWithTags.increment(); 
    //Increment by amount 10.
    counterWithTags.increment(amount = 10);
    
```

#### Reset
The counter can be resetted to default amount = 0. 

```ballerina
    map<string> counterTags = { "method": "GET" };
    observe:Counter counterWithTags = new ("CounterWithTags", desc = "Some description", tags = counterTags);
    counterWithTags.reset(); 
    
```

#### Get Value
The current value can be retrieved by this operation. 

```ballerina
    map<string> counterTags = { "method": "GET" };
    observe:Counter counterWithTags = new ("CounterWithTags", desc = "Some description", tags = counterTags);
    int currentValue = counterWithTags.getValue(); 
    
```

### Gauge Samples

#### Create
The following code snippets provides the information on how Gauge instances can be created. Instantiating the gauge 
will simply create an instance based on the params passed. 

```ballerina
    //Create gauge with simply by name. 
    //Uses the default statistics configuration. 
    observe:Gauge simpleGauge = new ("SimpleGauge"); 
        
    //Create gauge with description.
    //Uses the default statistics configuration. 
    observe:Gauge gaugeWithDesc = new ("GaugeWithDesc", desc = "This is a sample gauge description");
        
    //Create gauge with tags.
    //Uses the default statistics configuration. 
    map<string> gaugeTags = { "method": "GET" };
    observe:Counter gaugeWithTags = new ("GaugeWithTags", desc = "Some description", tags = gaugeTags);
        
    //Create gauge with disabled statistics. 
    observe:StatisticConfig[] statsConfigs = [];
    observe:Gauge gaugeWithNoStats = new ("GaugeWithTags", desc = "Some description", 
                                        tags = gaugeTags, statisticConfig = statsConfigs);
                                        
    //Create gauge with statistics config. 
    observe:StatisticConfig config = {timeWindow:30000, percentiles:[0.33, 0.5, 0.9, 0.99], buckets:3};
    statsConfigs[0]=config; 
        
    observe:Gauge gaugeWithStats = new ("GaugeWithTags", desc = "Some description", 
                                        tags = gaugeTags, statisticConfig = statsConfigs);                                        

```

#### Register
The gauge can registered with the global metrics registry, therefore it can be looked up later without having the 
reference of the gauge that was created. Also, only the registered counters will be reported to the Metrics reporter 
such as Prometheus. In case, if there is already another non gauge metric registered, 
then there will be an error returned. But if it's another gauge instance, then the registered gauge instance will 
be returned.

```ballerina
    map<string> gaugeTags = { "method": "GET" };
    observe:Gauge gaugeWithTags = new ("GaugeWithTags", desc = "Some description", tags = gaugeTags);
    gaugeWithTags.register() but {
        error e => log:printError("Cannot register the gauge.", err = e)
    };
    
```

#### Unregister
The gauge can unregistered with the global metrics registry if it is already registered. If a metrics is unregistered,
then further it'll not be included in metrics reporting.

```ballerina
     map<string> gaugeTags = { "method": "GET" };
     observe:Gauge gaugeWithTags = new ("GaugeWithTags", desc = "Some description", tags = gaugeTags);
     gaugeWithTags.register() but {
            error e => log:printError("Cannot register the gauge.", err = e)
     };
     gaugeWithTags.unregister();
    
```

#### Increment
The gauge can be incremented without passing any params (defaulted to 1.0), or by a specific amount. 

```ballerina
    map<string> gaugeTags = { "method": "GET" };
    observe:Gauge gaugeWithTags = new ("GaugeWithTags", desc = "Some description", tags = gaugeTags);
    //Increment by 1.
    gaugeWithTags.increment(); 
    //Increment by amount 10.
    gaugeWithTags.increment(amount = 10.0);
    
```

#### Decrement
The gauge can be decremented without passing any params (defaulted to 1.0), or by a specific amount. 

```ballerina
    map<string> gaugeTags = { "method": "GET" };
    observe:Gauge gaugeWithTags = new ("GaugeWithTags", desc = "Some description", tags = gaugeTags);
    //Increment by 1.
    gaugeWithTags.decrement(); 
    //Increment by amount 10.
    gaugeWithTags.decrement(amount = 10.0);
    
```

#### Set Value
This method sets the gauge's value with specific amount. 

```ballerina
     map<string> gaugeTags = { "method": "GET" };
     observe:Gauge gaugeWithTags = new ("GaugeWithTags", desc = "Some description", tags = gaugeTags);
     gaugeWithTags.setValue(100.0);
    
```

#### Get Value.
The current value can be retrieved by this operation. 

```ballerina
    map<string> gaugeTags = { "method": "GET" };
    observe:Gauge gaugeWithTags = new ("GaugeWithTags", desc = "Some description", tags = gaugeTags);
    float currentValue = gaugeWithTags.getValue(); 
    
```

#### Get Snapshot.
This method retrieves current snapshot of the statistics calculation based on the configurations passed to the gauge. 
If the statistics are disabled, then it'll be returning nil ().

```ballerina
    map<string> gaugeTags = { "method": "GET" };
    observe:Gauge gaugeWithTags = new ("GaugeWithTags", desc = "Some description", tags = gaugeTags);
    gaugeWithTags.setValue(1.0);
    gaugeWithTags.setValue(2.0);
    gaugeWithTags.setValue(3.0);
    
    observe:Snapshot[]? summarySnapshot = gaugeWithTags.getSnapshot();
    match summarySnapshot {
        observe:Snapshot[] stats => {
            io:println(stats);
        }
        () => {
            io:println("No statistics available!");
        }
    } 
```

### Global Metrics Samples

#### Get All Metrics 
This method returns all the metrics that are registered in the global metrics registry. This method is mainly useful for
metric reporters, where they can fetch all metrics, format those, and report. 

```ballerina
     observe:Metric[] metrics = observe:getAllMetrics();
     foreach metric in metrics {
        //do something.
     }
```

#### Lookup Metric
This method will lookup for the metric from the global metric registry and return it. 

```ballerina
    map<string> tags = { "method": "GET" };
    observe:Counter|observe:Gauge|() metric = observe:lookupMetric("MetricName", tags = tags);
    match metric {
        observe:Counter counter => {
                counter.increment(amount=10);
        }
        observe:Gauge gauge => {
                gauge.increment(amount = 10.0);
        }
        () => {
               io:println("No Metric Found!");
        }
    }
```
