/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.api;

import com.appirio.service.billingaccount.api.Client;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.FixtureHelpers;

import org.junit.Assert;
import org.junit.Test;


/**
 * Tests serialization/deserialization of Client representation.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class ClientTest {
    /**
     * The mapper for object serialization/deserialization
     */
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    /**
     * Tests serialization of a Client instance to JSON
     *
     * @throws Exception
     *             to JUnit
     */
    @Test
    public void serializesToJSON() throws Exception {
        final Client client = new Client(1L, "ACME", "Active", TestHelper.toDate("2018-04-28T07:24Z"), TestHelper
            .toDate("2019-12-31T17:00Z"), "ACME", null);

        final String expected = MAPPER.writeValueAsString(MAPPER.readValue(FixtureHelpers.fixture(
            "fixtures/client.json"), Client.class));

        Assert.assertEquals(expected, MAPPER.writeValueAsString(client));
    }

    /**
     * Tests deserialization of a Client instance from JSON
     *
     * @throws Exception
     *             to JUnit
     */
    @Test
    public void deserializesFromJSON() throws Exception {
        final Client client = new Client();
        client.setId(1L);
        client.setName("ACME");
        client.setStatus("Active");
        client.setStartDate(TestHelper.toDate("2018-04-28T07:24Z"));
        client.setEndDate(TestHelper.toDate("2019-12-31T17:00Z"));
        client.setCodeName("ACME");
        client.setCustomerNumber(null);

        Client actual = MAPPER.readValue(FixtureHelpers.fixture("fixtures/client.json"), Client.class);
        TestHelper.assertEquals(Client.class, client, actual);
    }
}
