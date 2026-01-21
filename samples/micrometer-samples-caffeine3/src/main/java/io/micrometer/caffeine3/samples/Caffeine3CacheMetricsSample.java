/*
 * Copyright 2025 VMware, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.caffeine3.samples;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.jspecify.annotations.Nullable;

public class Caffeine3CacheMetricsSample {

    public static void main(String[] args) {
        MeterRegistry registry = new SimpleMeterRegistry();

        System.out.println("=== Caffeine 3.x Cache Metrics Sample ===");
        System.out.println();

        // Caffeine 3.x cache with explicit nullable value type
        // This demonstrates the new signature: Cache<K, V extends @Nullable Object>
        Cache<String, @Nullable Object> cache = Caffeine.newBuilder().maximumSize(100).recordStats().build();

        System.out.println("Created Caffeine 3.x cache with nullable generics");

        // Register metrics - this verifies compatibility with Caffeine 3.x generics
        CaffeineCacheMetrics.monitor(registry, cache, "caffeine3.cache");

        System.out.println("✓ Successfully registered CaffeineCacheMetrics with Caffeine 3.x cache");
        System.out.println();

        // Demonstrate cache usage with various value types
        System.out.println("Populating cache with different value types...");
        cache.put("string-key", "string-value");
        cache.put("integer-key", 42);
        cache.put("boolean-key", true);
        cache.put("double-key", 3.14);

        for (int i = 0; i < 5; i++) {
            cache.put("generated-" + i, "value-" + i);
        }

        System.out.println("Added 9 entries to cache");
        System.out.println();

        // Simulate some cache hits and misses
        System.out.println("Simulating cache access patterns...");
        String[] testKeys = { "string-key", "integer-key", "boolean-key", "double-key", "generated-0", "generated-2",
                "generated-4", "missing-1", "missing-2", "missing-3" };

        for (String key : testKeys) {
            Object value = cache.getIfPresent(key);
            String status = value != null ? "HIT" : "MISS";
            System.out.printf("  %-12s -> %-10s %s%n", key, status, value != null ? ("(" + value + ")") : "");
        }

        System.out.println();

        // Display cache statistics
        System.out.println("=== Cache Statistics ===");
        var stats = cache.stats();
        System.out.printf("Request Count: %d%n", stats.requestCount());
        System.out.printf("Hit Count: %d%n", stats.hitCount());
        System.out.printf("Miss Count: %d%n", stats.missCount());
        System.out.printf("Hit Rate: %.2f%%%n", stats.hitRate() * 100);
        System.out.println();

        // Display Micrometer metrics
        System.out.println("=== Micrometer Metrics ===");
        registry.getMeters().forEach(meter -> {
            if (meter.getId().getName().startsWith("caffeine3.cache")) {
                System.out.printf("%-25s = %s%n", meter.getId().getName() + meter.getId().getTags().toString(),
                        meter.measure().iterator().next().getValue());
            }
        });

        System.out.println();
        System.out.println("✓ Caffeine 3.x compatibility verified!");
        System.out.println("CaffeineCacheMetrics successfully handles nullable generics");
        System.out.println("and works seamlessly with Caffeine 3.x Cache instances.");
    }

}
