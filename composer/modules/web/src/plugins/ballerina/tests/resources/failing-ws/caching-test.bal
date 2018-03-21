import ballerina/caching;
import ballerina/runtime;

function testCreateCache (string name, int timeOut, int capacity, float evictionFactor) (string, int, int, float) {
    caching:Cache cache = caching:createCache(name, timeOut, capacity, evictionFactor);
    return cache.name, cache.expiryTimeMillis, cache.capacity, cache.evictionFactor;
}

function testPut (string name, int timeOut, int capacity, float evictionFactor, string key, string value) (int) {
    caching:Cache cache = caching:createCache(name, timeOut, capacity, evictionFactor);
    cache.put(key, value);
    return cache.size();
}

function testGet (string name, int timeOut, int capacity, float evictionFactor, string key, string value) (int, string) {
    caching:Cache cache = caching:createCache(name, timeOut, capacity, evictionFactor);
    cache.put(key, value);
    var value, e = (string)cache.get(key);
    if (e != null) {
        return -1, "";
    }
    return cache.size(), value;
}

function testRemove (string name, int timeOut, int capacity, float evictionFactor, string key, string value) (int) {
    caching:Cache cache = caching:createCache(name, timeOut, capacity, evictionFactor);
    cache.put(key, value);
    cache.remove(key);
    return cache.size();
}

function testCacheEviction1 (string name, int timeOut, int capacity, float evictionFactor) (string[], int) {
    caching:Cache cache = caching:createCache(name, timeOut, capacity, evictionFactor);
    cache.put("A", "A");
    runtime:sleepCurrentThread(20);
    cache.put("B", "B");
    runtime:sleepCurrentThread(20);
    cache.put("C", "C");
    runtime:sleepCurrentThread(20);
    cache.put("D", "D");
    runtime:sleepCurrentThread(20);
    cache.put("E", "E");
    runtime:sleepCurrentThread(20);
    cache.put("F", "F");
    runtime:sleepCurrentThread(20);
    cache.put("G", "G");
    runtime:sleepCurrentThread(20);
    cache.put("H", "H");
    runtime:sleepCurrentThread(20);
    cache.put("I", "I");
    runtime:sleepCurrentThread(20);
    cache.put("J", "J");
    runtime:sleepCurrentThread(20);
    cache.put("K", "K");
    return cache.entries.keys(), cache.size();
}

function testCacheEviction2 (string name, int timeOut, int capacity, float evictionFactor) (string[], int) {
    caching:Cache cache = caching:createCache(name, timeOut, capacity, evictionFactor);
    cache.put("A", "A");
    runtime:sleepCurrentThread(20);
    cache.put("B", "B");
    runtime:sleepCurrentThread(20);
    cache.put("C", "C");
    runtime:sleepCurrentThread(20);
    cache.put("D", "D");
    runtime:sleepCurrentThread(20);
    cache.put("E", "E");
    runtime:sleepCurrentThread(20);
    cache.put("F", "F");
    runtime:sleepCurrentThread(20);
    cache.put("G", "G");
    runtime:sleepCurrentThread(20);
    cache.put("H", "H");
    runtime:sleepCurrentThread(20);
    _ = cache.get("B");
    runtime:sleepCurrentThread(20);
    cache.put("I", "I");
    runtime:sleepCurrentThread(20);
    cache.put("J", "J");
    runtime:sleepCurrentThread(20);
    cache.put("K", "K");
    return cache.entries.keys(), cache.size();
}

function testCacheEviction3 (string name, int timeOut, int capacity, float evictionFactor) (string[], int) {
    caching:Cache cache = caching:createCache(name, timeOut, capacity, evictionFactor);
    cache.put("A", "A");
    runtime:sleepCurrentThread(20);
    cache.put("B", "B");
    runtime:sleepCurrentThread(20);
    cache.put("C", "C");
    runtime:sleepCurrentThread(20);
    cache.put("D", "D");
    runtime:sleepCurrentThread(20);
    cache.put("E", "E");
    runtime:sleepCurrentThread(20);
    cache.put("F", "F");
    runtime:sleepCurrentThread(20);
    _ = cache.get("A");
    runtime:sleepCurrentThread(20);
    cache.put("G", "G");
    runtime:sleepCurrentThread(20);
    cache.put("H", "H");
    runtime:sleepCurrentThread(20);
    _ = cache.get("B");
    runtime:sleepCurrentThread(20);
    cache.put("I", "I");
    runtime:sleepCurrentThread(20);
    cache.put("J", "J");
    runtime:sleepCurrentThread(20);
    cache.put("K", "K");
    return cache.entries.keys(), cache.size();
}

function testCacheEviction4 (string name, int timeOut, int capacity, float evictionFactor) (string[], int) {
    caching:Cache cache = caching:createCache(name, timeOut, capacity, evictionFactor);
    cache.put("A", "A");
    runtime:sleepCurrentThread(20);
    cache.put("B", "B");
    runtime:sleepCurrentThread(20);
    cache.put("C", "C");
    runtime:sleepCurrentThread(20);
    cache.put("D", "D");
    runtime:sleepCurrentThread(20);
    cache.put("E", "E");
    runtime:sleepCurrentThread(20);
    _ = cache.get("A");
    runtime:sleepCurrentThread(20);
    _ = cache.get("B");
    runtime:sleepCurrentThread(20);
    _ = cache.get("C");
    runtime:sleepCurrentThread(20);
    _ = cache.get("D");
    runtime:sleepCurrentThread(20);
    cache.put("F", "F");
    runtime:sleepCurrentThread(20);
    return cache.entries.keys(), cache.size();
}

function testCreateCacheWithZeroExpiryTime () {
    _ = caching:createCache("test", 0, 10, 0.1);
}

function testCreateCacheWithNegativeExpiryTime () {
    _ = caching:createCache("test", -10, 10, 0.1);
}

function testCreateCacheWithZeroCapacity () {
    _ = caching:createCache("test", 10000, 0, 0.1);
}

function testCreateCacheWithNegativeCapacity () {
    _ = caching:createCache("test", 10000, -10, 0.1);
}

function testCreateCacheWithZeroEvictionFactor () {
    _ = caching:createCache("test", 10000, 10, 0);
}

function testCreateCacheWithInvalidEvictionFactor () {
    _ = caching:createCache("test", 10000, 10, 1.1);
}
