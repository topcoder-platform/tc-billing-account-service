/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.appirio.service.billingaccount.api.ChallengeType;

import org.junit.Test;


/**
 * Unit test of ChallengeType.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class ChallengeTypeTest {
    /**
     * The object being tested.
     */
    private final ChallengeType type = new ChallengeType();

    /**
     * Test ChallengeType.getChallengeTypeId.
     */
    @Test
    public void testGetChallengeTypeId() {
        assertEquals(0, type.getChallengeTypeId());
    }

    /**
     * Test ChallengeType#setChallengeTypeId.
     */
    @Test
    public void testSetChallengeTypeId() {
        type.setChallengeTypeId(2L);
        assertEquals(2L, type.getChallengeTypeId());
    }

    /**
     * Test ChallengeType.getDescription.
     */
    @Test
    public void testGetDescription() {
        assertNull(type.getDescription());
    }

    /**
     * Test ChallengeType.setDescription.
     */
    @Test
    public void testSetDescription() {
        type.setDescription("desc");
        assertEquals("desc", type.getDescription());
    }

    /**
     * Test ChallengeType.isStudio.
     */
    @Test
    public void testIsStudio() {
        assertFalse(type.isStudio());
    }

    /**
     * Test ChallengeType.setStudio.
     */
    @Test
    public void testSetStudio() {
        type.setStudio(true);
        assertTrue(type.isStudio());
    }
}
