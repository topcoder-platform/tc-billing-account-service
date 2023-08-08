/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.api;

import static org.junit.Assert.assertEquals;

import com.appirio.service.billingaccount.api.BillingAccount;
import com.appirio.service.billingaccount.api.BillingAccountUpdatedDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.FixtureHelpers;

import org.junit.Test;


/**
 * Tests serialization/deserialization of BillingAccountUpdatedDTO representation.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class BillingAccountUpdatedDTOTest {
    /**
     * The mapper for object serialization/deserialization
     */
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    /**
     * Tests serialization of a BillingAccountUpdatedDTO instance to JSON
     *
     * @throws Exception
     *             to JUnit
     */
    @Test
    public void serializesToJSON() throws Exception {
        final BillingAccount original = new BillingAccount(2L, "Test Project 2", "Active", TestHelper.toDate(
            "2018-04-28T07:24Z"), TestHelper.toDate("2019-12-31T17:00Z"), 5000F, 105F, 205F, 0F, "TESTPROJECT-02", null,
            "description", "null", 1L, 0L, 2L, true);
        final BillingAccount updated = new BillingAccount(2L, "New billing account2", "Active", TestHelper.toDate(
            "2017-04-01T09:00Z"), TestHelper.toDate("2017-05-20T09:00Z"), null, 106F, 206F, 1.6F, "New POOO", null,
            "The billing account description", "25", 1L, 1L, 1L, false);

        final BillingAccountUpdatedDTO billingAccountUpdatedDTO = new BillingAccountUpdatedDTO(original, updated);

        final String expected = MAPPER.writeValueAsString(MAPPER.readValue(FixtureHelpers.fixture(
            "fixtures/billing_account_updated_dto.json"), BillingAccountUpdatedDTO.class));

        assertEquals(expected, MAPPER.writeValueAsString(billingAccountUpdatedDTO));
    }

    /**
     * Tests deserialization of a BillingAccountUpdatedDTO instance from JSON
     *
     * @throws Exception
     *             to JUnit
     */
    @Test
    public void deserializesFromJSON() throws Exception {
        final BillingAccount original = new BillingAccount(2L, "Test Project 2", "Active", TestHelper.toDate(
            "2018-04-28T07:24Z"), TestHelper.toDate("2019-12-31T17:00Z"), 5000F, 105F, 205F, 0F, "TESTPROJECT-02", null,
            "description", "null", 1L, 0L, 2L, true);
        final BillingAccount updated = new BillingAccount(2L, "New billing account2", "Active", TestHelper.toDate(
            "2017-04-01T09:00Z"), TestHelper.toDate("2017-05-20T09:00Z"), null, 106F, 206F, 1.6F, "New POOO", null,
            "The billing account description", "25", 1L, 1L, 1L, false);

        final BillingAccountUpdatedDTO billingAccountUpdatedDTO = new BillingAccountUpdatedDTO();
        billingAccountUpdatedDTO.setOriginal(original);
        billingAccountUpdatedDTO.setUpdated(updated);

        BillingAccountUpdatedDTO actual = MAPPER.readValue(FixtureHelpers.fixture(
            "fixtures/billing_account_updated_dto.json"), BillingAccountUpdatedDTO.class);
        TestHelper.assertEquals(BillingAccountUpdatedDTO.class, billingAccountUpdatedDTO, actual);
    }
}
