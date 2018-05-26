/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.appirio.service.billingaccount.api.IdSequence;

import org.junit.Test;


/**
 * Unit test of IdSequence.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class IdSequenceTest {
    /**
     * The object being tested.
     */
    private final IdSequence idSequence = new IdSequence();

    /**
     * Test IdSequence.getNextId and IdSequence.setId
     */
    @Test
    public void testGetSetNextId() {
        assertNull(idSequence.getNextId());

        idSequence.setNextId(1L);
        assertEquals(1L, idSequence.getNextId().longValue());
    }
}
