/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.api;

import static org.junit.Assert.assertEquals;

import com.appirio.service.billingaccount.api.ChallengeFee;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.FixtureHelpers;

import org.junit.Test;


/**
 * Tests serialization/deserialization of ChallengeFee representation.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class ChallengeFeeTest {
    /**
     * The mapper for object serialization/deserialization
     */
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    /**
     * Tests serialization of a ChallengeFee instance to JSON
     *
     * @throws Exception
     *             to JUnit
     */
    @Test
    public void serializesToJSON() throws Exception {
        final ChallengeFee challengeFee = new ChallengeFee(1L, 80000010L, 39L, 500D, false, "Code", false, "Code xxx");

        final String expected = MAPPER.writeValueAsString(MAPPER.readValue(FixtureHelpers.fixture(
            "fixtures/challenge_fee.json"), ChallengeFee.class));

        assertEquals(expected, MAPPER.writeValueAsString(challengeFee));
    }

    /**
     * Tests deserialization of a ChallengeFee instance from JSON
     *
     * @throws Exception
     *             to JUnit
     */
    @Test
    public void deserializesFromJSON() throws Exception {
        final ChallengeFee challengeFee = new ChallengeFee();
        challengeFee.setId(1L);
        challengeFee.setProjectId(80000010L);
        challengeFee.setChallengeTypeId(39L);
        challengeFee.setChallengeFee(500D);
        challengeFee.setStudio(false);
        challengeFee.setChallengeTypeDescription("Code");
        challengeFee.setDeleted(false);
        challengeFee.setName("Code xxx");

        ChallengeFee actual = MAPPER.readValue(FixtureHelpers.fixture("fixtures/challenge_fee.json"),
            ChallengeFee.class);
        TestHelper.assertEquals(ChallengeFee.class, challengeFee, actual);
    }

}
