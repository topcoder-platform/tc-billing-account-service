/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.appirio.service.billingaccount.api.ChallengeFeePercentage;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit test of ChallengeFeePercentage.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class ChallengeFeePercentageTest {
    /**
     * The id used for testing.
     */
    private static final long ID = 1L;

    /**
     * The project id used for testing.
     */
    private static final long PROJECT_ID = 1L;

    /**
     * The fee percentage used for testing.
     */
    private static final Double FEE_PERCENTAGE = 15.0;

    /**
     * The active flag used for testing.
     */
    private static final boolean ACTIVE_FLAG = true;

    /**
     * The object being tested.
     */
    private ChallengeFeePercentage percentage;

    /**
     * Set up the object
     *
     * @throws Exception
     *             to junit
     */
    @Before
    public void setUp() throws Exception {
        percentage = new ChallengeFeePercentage(ID, PROJECT_ID, FEE_PERCENTAGE, ACTIVE_FLAG);
    }

    /**
     * Test ChallengeFeePercentage.getId.
     */
    @Test
    public void testGetId() {
        assertEquals(ID, percentage.getId());
    }

    /**
     * Test ChallengeFeePercentage.setId.
     */
    @Test
    public void testSetId() {
        percentage.setId(2);
        assertEquals(2, percentage.getId());
    }

    /**
     * Test ChallengeFeePercentage.getProjectId.
     */
    @Test
    public void testGetProjectId() {
        assertEquals(PROJECT_ID, percentage.getProjectId());
    }

    /**
     * Test ChallengeFeePercentage.setProjectId.
     */
    @Test
    public void testSetProjectId() {
        percentage.setProjectId(2L);
        assertEquals(2, percentage.getProjectId());
    }

    /**
     * Test ChallengeFeePercentage.getChallengeFeePercentage.
     */
    @Test
    public void testGetChallengeFeePercentage() {
        assertEquals(0, FEE_PERCENTAGE.compareTo(percentage.getChallengeFeePercentage()));
    }

    /**
     * Test ChallengeFeePercentage.setChallengeFeePercentage.
     */
    @Test
    public void testSetChallengeFeePercentage() {
        percentage.setChallengeFeePercentage(2.0);
        assertEquals(0, Double.valueOf(2.0).compareTo(percentage.getChallengeFeePercentage()));
    }

    /**
     * Test ChallengeFeePercentage.isActive.
     */
    @Test
    public void testIsActive() {
        assertTrue(percentage.isActive());
    }

    /**
     * Test ChallengeFeePercentage.setActive.
     */
    @Test
    public void testSetActive() {
        percentage.setActive(false);
        assertFalse(percentage.isActive());
    }
}
