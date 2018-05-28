/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.appirio.service.billingaccount.api.UserIdDTO;

import org.junit.Test;


/**
 * Unit test of UserIdDTO.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class UserIdDTOTest {
    /**
     * The object being tested.
     */
    private final UserIdDTO dto = new UserIdDTO();

    /**
     * Test UserIdDTO.getUserId and UserIdDTO.setUserId
     */
    @Test
    public void testGetSetUserId() {
        assertNull(dto.getUserId());

        dto.setUserId(1L);
        assertEquals(1L, dto.getUserId().longValue());
    }
}
