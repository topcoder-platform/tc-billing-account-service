/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.api;

import static org.junit.Assert.assertEquals;

import com.appirio.service.billingaccount.api.IdDTO;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit test of IdDTO.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class IdDTOTest {
    /**
     * The id used for testing.
     */
    private static final Long ID = 1L;

    /**
     * The object being tested.
     */
    private IdDTO dto;

    /**
     * Set up the object.
     *
     * @throws Exception
     *             to junit
     */
    @Before
    public void setUp() throws Exception {
        dto = new IdDTO(ID);
    }

    /**
     * Test IdDTO.getId.
     */
    @Test
    public void testGetId() {
        assertEquals(ID, dto.getId());
    }

    /**
     * Test IdDTO.setId.
     */
    @Test
    public void testSetId() {
        dto.setId(2L);
        assertEquals(2L, dto.getId().longValue());
    }
}
