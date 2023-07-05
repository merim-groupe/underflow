package com.merim.digitalpayment.underflow.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * UnderflowMapUtils.
 *
 * @author Pierre Adam
 * @since 23.07.05
 */
public class UnderflowMapUtils {

    /**
     * Create LRU cache map.
     *
     * @param <K>        the type parameter
     * @param <V>        the type parameter
     * @param maxEntries the max entries
     * @return the map
     */
    public static <K, V> Map<K, V> createLRUCacheMap(final int maxEntries) {
        return new LinkedHashMap<K, V>(maxEntries * 10 / 7, 0.7f, true) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
                return this.size() > maxEntries;
            }
        };
    }
}
