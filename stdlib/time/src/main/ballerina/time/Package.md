## Package overview
The `ballerina/time` package provides implementations related to time, date, time zones, and durations. 

The package has two main types as [Time](time.html#Time) and [Timezone](time.html#Timezone). The type `Time` represents a time associated with a given time zone. It has `time` and `zone` as attributes. The type `Timezone` represents the time zone associated with a given time. It has `zoneId` and `zoneOffset` as attributes. A `zoneId` can be one of the following:

* If `zoneId` equals 'Z', the result is UTC.
* If `zoneId` equals 'GMT', 'UTC' or 'UT', it is equivalent to UTC.
* If `zoneId` starts with '+' or '-', the ID is parsed as an offset. Offset can be specified in one of the following ways. +h, +hh, +hh:mm, -hh:mm, +hhmm, -hhmm, +hh:mm:ss, -hh:mm:ss, +hhmmss, -hhmmss
* Also `zoneId` can be a region-based zone ID. The format is '{area}/{city}' eg: "America/Panama". The zones are based on IANA Time Zone Database (TZDB) supplied data.

## Samples

### Getting the current time/date

```ballerina
time:Time time = time:currentTime(); // Create an object of type ‘Time’.

int timeValue = time.time;  // Time in milliseconds since January 1, 1970, 00:00:00 GMT. E.g., 1523513039.
int nanoTime = time:nanoTime(); // Time in nanoseconds since an arbitrary origin. Therefore, it should be used only to calculate durations. E.g., 2426115697486340.
string zoneId = time.zone.zoneId; // Time zone as an identifier. E.g., “America/Panama”.
int zoneoffset = time.zone.zoneOffset; // Time zone as an offset. E.g., -05:00.

// Get the current date and time upto milliseconds.
int year = time.year(); // E.g., 2018
int month = time.month(); // E.g., 3
int day = time.day(); // E.g., 1
int hour = time.hour(); // E.g., 18 
int minute = time.minute(); // E.g., 56 
int second = time.second(); // E.g., 23
int milliSecond = time.milliSecond(); // E.g., 555 
string weekday = time.weekday(); // Day of the week. E.g., “TUESDAY”.
```

### Creating a time/date object

```ballerina
// Create an object of type ‘Time’ with time zone.
time:Timezone zoneIdValue = {zoneId:"America/Panama"};
time:Time time1 = new (1498488382000, zoneIdValue);

// Create an object of type ‘Time’ with the time zone offset.
time:Timezone zoneOffsetValue = {zoneId:"-05:00"};
time:Time time2 =new (1498488382000, zoneOffsetValue);

// Create an object of type ‘Time’ without the time zone.
time:Timezone noZoneValue = {zoneId:""};
time:Time time3 = new (1498488382000, noZoneValue);

// Create an object of type ‘Time’ with time and date. E.g., 2018-03-28T23:42:45.554-05:00  
time:Time dateTime = time:createTime(2018, 3, 28, 23, 42, 45, 554, "America/Panama");
```


### Formatting a time/date to a string

```ballerina
time:Timezone zoneValue = {zoneId:"America/Panama"};
time:Time time = new (1498488382444, zoneValue);

//Format a time to a string of a given pattern.
string time1 = time.format("yyyy-MM-dd'T'HH:mm:ss.SSSZ"); //E.g., “2017-06-26T09:46:22.444-0500”.

//Format a time to a string of the RFC-1123 format.
string time2 = time.format(time:TIME_FORMAT_RFC_1123); // E.g., "Mon, 26 Jun 2017 09:46:22 -0500”

// Convert a time object to a string value.
string time3 = time.toString(); //”2017-06-26T09:46:22.444-05:00”
```

### Parsing a string to time/date

```ballerina
// Parse a time string of a given format. 
time:Time time1 = time:parse("2017-06-26T09:46:22.444-0500", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"); // The ‘Z’ stands for the time zone.

// Parse a time string of the RFC-1123 format.
time:Time time2 = time:parse("Wed, 28 Mar 2018 11:56:23 +0530", time:TIME_FORMAT_RFC_1123);
```

### Setting time durations

```ballerina
// Add a duration to a given time.
time:Time time1 = time:parse("2017-06-26T09:46:22.444-0500", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
time1 = time.addDuration(1, 1, 1, 1, 1, 1, 1); // Adds 1 year, 1 month, 1 day, 1 hour, 1 minute, 1 second, and 1 millisecond.

// Subtract a duration from a given time.
time:Time time2 = time:parse("2016-03-01T09:46:22.444-0500", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
time2 = time.subtractDuration(1, 1, 1, 1, 1, 1, 1);  // Subtracts 1 year, 1 month, 1 day, 1 hour, 1 minute, 1 second, and 1 millisecond.

// Get the duration between two times.
int diffInMillis = time1.time - time2.time;
```
