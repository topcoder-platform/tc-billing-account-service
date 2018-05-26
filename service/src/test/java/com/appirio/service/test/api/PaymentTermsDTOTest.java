/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.api;

import static org.junit.Assert.assertEquals;

import com.appirio.service.billingaccount.api.PaymentTermsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.FixtureHelpers;

import org.junit.Test;


/**
 * Tests serialization/deserialization of PaymentTermsDTO representation.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class PaymentTermsDTOTest {
    /**
     * The mapper for object serialization/deserialization
     */
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    /**
     * Tests serialization of a PaymentTermsDTO instance to JSON
     *
     * @throws Exception
     *             to JUnit
     */
    @Test
    public void serializesToJSON() throws Exception {
        final PaymentTermsDTO paymentTermsDTO = new PaymentTermsDTO(1L, "Term 1");

        final String expected = MAPPER.writeValueAsString(MAPPER.readValue(FixtureHelpers.fixture(
            "fixtures/payment_terms_dto.json"), PaymentTermsDTO.class));

        assertEquals(expected, MAPPER.writeValueAsString(paymentTermsDTO));
    }

    /**
     * Tests deserialization of a PaymentTermsDTO instance from JSON
     *
     * @throws Exception
     *             to JUnit
     */
    @Test
    public void deserializesFromJSON() throws Exception {
        final PaymentTermsDTO paymentTermsDTO = new PaymentTermsDTO();
        paymentTermsDTO.setId(1L);
        paymentTermsDTO.setDescription("Term 1");

        PaymentTermsDTO actual = MAPPER.readValue(FixtureHelpers.fixture("fixtures/payment_terms_dto.json"),
            PaymentTermsDTO.class);
        TestHelper.assertEquals(PaymentTermsDTO.class, paymentTermsDTO, actual);
    }
}
