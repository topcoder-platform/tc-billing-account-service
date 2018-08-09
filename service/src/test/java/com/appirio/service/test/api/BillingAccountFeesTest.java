/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.api;

import static org.junit.Assert.assertEquals;

import com.appirio.service.billingaccount.api.BillingAccountFees;
import com.appirio.service.billingaccount.api.ChallengeFee;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.FixtureHelpers;

import org.junit.Test;

import java.util.Arrays;


/**
 * Tests serialization/deserialization of BillingAccountFees representation.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class BillingAccountFeesTest {
    /**
     * The mapper for object serialization/deserialization
     */
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    /**
     * Tests serialization of a BillingAccountFees instance to JSON
     *
     * @throws Exception
     *             to JUnit
     */
    @Test
    public void serializesToJSON() throws Exception {
        final ChallengeFee challengeFee = new ChallengeFee(1L, 80000010L, 39L, 500D, false, "Code", false, "Code xxx");

        final BillingAccountFees billingAccountFees = new BillingAccountFees(true, null, Arrays.asList(challengeFee));

        final String expected = MAPPER.writeValueAsString(MAPPER.readValue(FixtureHelpers.fixture(
            "fixtures/billing_account_fees.json"), BillingAccountFees.class));

        assertEquals(expected, MAPPER.writeValueAsString(billingAccountFees));
    }

    /**
     * Tests deserialization of a BillingAccountFees instance from JSON
     *
     * @throws Exception
     *             to JUnit
     */
    @Test
    public void deserializesFromJSON() throws Exception {
        final ChallengeFee challengeFee = new ChallengeFee(1L, 80000010L, 39L, 500D, false, "Code", false, "Code xxx");
        final BillingAccountFees billingAccountFees = new BillingAccountFees();
        billingAccountFees.setChallengeFeeFixed(true);
        billingAccountFees.setChallengeFeePercentage(null);
        billingAccountFees.setChallengeFees(Arrays.asList(challengeFee));

        BillingAccountFees actual = MAPPER.readValue(FixtureHelpers.fixture("fixtures/billing_account_fees.json"),
            BillingAccountFees.class);
        TestHelper.assertEquals(BillingAccountFees.class, billingAccountFees, actual);
    }
}
