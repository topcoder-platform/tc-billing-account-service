/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.api;

import static org.junit.Assert.assertEquals;

import com.appirio.service.billingaccount.api.BillingAccount;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.FixtureHelpers;

import org.junit.Test;


/**
 * Tests serialization/deserialization of BillingAccount representation.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class BillingAccountTest {
    /**
     * The mapper for object serialization/deserialization
     */
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    /**
     * Tests serialization of a BillingAccount instance to JSON
     *
     * @throws Exception
     *             to JUnit
     */
    @Test
    public void serializesToJSON() throws Exception {
        final BillingAccount billingAccount = new BillingAccount(70015984L, "Liquid (CA)", "Active", TestHelper.toDate(
            "2010-03-20T08:52Z"), TestHelper.toDate("2011-12-31T17:00Z"), 5000F, 101F, 201F, 0F, "Liquid PO 1", null, "description",
            "null", 1L, 0L, 70014096L, null);

        final String expected = MAPPER.writeValueAsString(MAPPER.readValue(FixtureHelpers.fixture(
            "fixtures/billing_account.json"), BillingAccount.class));

        assertEquals(expected, MAPPER.writeValueAsString(billingAccount));
    }

    /**
     * Tests deserialization of a BillingAccount instance from JSON
     *
     * @throws Exception
     *             to JUnit
     */
    @Test
    public void deserializesFromJSON() throws Exception {
        final BillingAccount billingAccount = new BillingAccount();
        billingAccount.setId(70015984L);
        billingAccount.setName("Liquid (CA)");
        billingAccount.setStatus("Active");
        billingAccount.setStartDate(TestHelper.toDate("2010-03-20T08:52Z"));
        billingAccount.setEndDate(TestHelper.toDate("2011-12-31T17:00Z"));
        billingAccount.setBudgetAmount(5000F);
        billingAccount.setConsumedAmount(101F);
        billingAccount.setLockedAmount(201F);
        billingAccount.setSalesTax(0F);
        billingAccount.setPoNumber("Liquid PO 1");
        billingAccount.setPaymentTerms(null);
        billingAccount.setDescription("description");
        billingAccount.setSubscriptionNumber("null");
        billingAccount.setCompanyId(1L);
        billingAccount.setManualPrizeSetting(0L);
        billingAccount.setClientId(70014096L);
        billingAccount.setBillable(null);

        BillingAccount actual = MAPPER.readValue(FixtureHelpers.fixture("fixtures/billing_account.json"),
            BillingAccount.class);
        TestHelper.assertEquals(BillingAccount.class, billingAccount, actual);
    }
}
