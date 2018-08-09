/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.dto;

import static org.junit.Assert.assertEquals;

import com.appirio.service.billingaccount.dto.TCUserDTO;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit test of TCUserDTO.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class TCUserDTOTest {
    /**
     * The id used for testing.
     */
    private static final Long ID = 1L;

    /**
     * The handle used for testing.
     */
    private static final String HANDLE = "handle";

    /**
     * The object being tested.
     */
    private TCUserDTO dto;

    /**
     * Set up the object
     *
     * @throws Exception
     *             to junit
     */
    @Before
    public void setUp() throws Exception {
        dto = new TCUserDTO(ID, HANDLE);
    }

    /**
     * Test TCUserDTO.getId.
     */
    @Test
    public void testGetId() {
        assertEquals(ID, dto.getId());
    }

    /**
     * Test TCUserDTO.setId.
     */
    @Test
    public void testSetId() {
        dto.setId(2L);
        assertEquals(2, dto.getId().longValue());
    }

    /**
     * Test TCUserDTO.getHandle.
     */
    @Test
    public void testGetHandle() {
        assertEquals(HANDLE, dto.getHandle());
    }

    /**
     * Test TCUserDTO.setHandle.
     */
    @Test
    public void testSetHandle() {
        dto.setHandle("value");
        assertEquals("value", dto.getHandle());
    }
}
