/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.api;

import static org.junit.Assert.assertEquals;

import com.appirio.service.billingaccount.api.BillingAccountUser;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.FixtureHelpers;

import org.junit.Test;


/**
 * Tests serialization/deserialization of BillingAccountUser representation.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class BillingAccountUserTest {
    /**
     * The mapper for object serialization/deserialization
     */
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    /**
     * Tests serialization of a BillingAccountUser instance to JSON
     *
     * @throws Exception
     *             to JUnit
     */
    @Test
    public void serializesToJSON() throws Exception {
        final BillingAccountUser billingAccountUser = new BillingAccountUser();
        billingAccountUser.setId(2L);
        billingAccountUser.setName("heffan");
        billingAccountUser.setDescription(null);

        final String expected = MAPPER.writeValueAsString(MAPPER.readValue(FixtureHelpers.fixture(
            "fixtures/billing_account_user.json"), BillingAccountUser.class));

        assertEquals(expected, MAPPER.writeValueAsString(billingAccountUser));
    }

    /**
     * Tests deserialization of a BillingAccountUser instance from JSON
     *
     * @throws Exception
     *             to JUnit
     */
    @Test
    public void deserializesFromJSON() throws Exception {
        final BillingAccountUser billingAccountUser = new BillingAccountUser();
        billingAccountUser.setId(2L);
        billingAccountUser.setName("heffan");
        billingAccountUser.setDescription(null);

        BillingAccountUser actual = MAPPER.readValue(FixtureHelpers.fixture("fixtures/billing_account_user.json"),
            BillingAccountUser.class);
        TestHelper.assertEquals(BillingAccountUser.class, billingAccountUser, actual);
    }
}
