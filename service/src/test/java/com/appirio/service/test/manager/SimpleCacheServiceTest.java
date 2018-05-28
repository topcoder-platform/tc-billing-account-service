/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.appirio.service.billingaccount.manager.SimpleCacheService;

import org.junit.Test;


/**
 * Test SimpleCacheService.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class SimpleCacheServiceTest {
    /**
     * The cache object key
     */
    private static final String KEY = "KEY";

    /**
     * The cached object
     */
    private static final Object VALUE = "value";

    /**
     * Service being tested
     */
    private final SimpleCacheService cacheService = new SimpleCacheService();

    /**
     * Test SimpleCacheService.put to put an object into the cache
     *
     * @throws Exception to junit
     */
    @Test
    public void testPut() throws Exception {
        cacheService.put(KEY, VALUE, 1);
        Object cached = cacheService.get(KEY);
        assertEquals(VALUE, cached);

        Thread.sleep(1500L);
        // now it should be expired
        assertNull(cacheService.get(KEY));

        // put another, it never be expired unless manually removed
        cacheService.put(KEY, "new", 0);
        assertEquals("new", cacheService.get(KEY));
    }

    /**
     * Test SimpleCacheService.get
     */
    @Test
    public void testGet() {
        assertNull(cacheService.get(KEY));
    }

    @Test
    public void testDelete() {
        assertNull(cacheService.delete(KEY));

        // put another
        cacheService.put(KEY, VALUE, 0);
        Object deleted = cacheService.delete(KEY);
        assertEquals(VALUE, deleted);
    }
}
