/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.dto;

import static org.junit.Assert.assertEquals;

import com.appirio.service.billingaccount.dto.SaveClientDTO;
import com.appirio.service.test.api.TestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.FixtureHelpers;

import org.junit.Test;


/**
 * Tests serialization/deserialization of SaveClientDTO representation.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class SaveClientDTOTest {
    /**
     * The mapper for object serialization/deserialization
     */
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    /**
     * Tests serialization of a SaveClientDTO instance to JSON
     *
     * @throws Exception
     *             to JUnit
     */
    @Test
    public void serializesToJSON() throws Exception {
        final SaveClientDTO saveClientDTO = new SaveClientDTO(0, "Test Client5", "Active", TestHelper.toDate(
            "2017-02-12T09:00Z"), TestHelper.toDate("2017-05-20T09:00Z"), "CodeName 1", "number");

        final String expected = MAPPER.writeValueAsString(MAPPER.readValue(FixtureHelpers.fixture(
            "fixtures/save_client_dto.json"), SaveClientDTO.class));

        assertEquals(expected, MAPPER.writeValueAsString(saveClientDTO));
    }

    /**
     * Tests deserialization of a SaveClientDTO instance from JSON
     *
     * @throws Exception
     *             to JUnit
     */
    @Test
    public void deserializesFromJSON() throws Exception {
        final SaveClientDTO saveClientDTO = new SaveClientDTO();
        saveClientDTO.setId(0);
        saveClientDTO.setName("Test Client5");
        saveClientDTO.setStatus("Active");
        saveClientDTO.setStartDate(TestHelper.toDate("2017-02-12T09:00Z"));
        saveClientDTO.setEndDate(TestHelper.toDate("2017-05-20T09:00Z"));
        saveClientDTO.setCodeName("CodeName 1");
        saveClientDTO.setCustomerNumber("number");

        SaveClientDTO actual = MAPPER.readValue(FixtureHelpers.fixture("fixtures/save_client_dto.json"),
            SaveClientDTO.class);
        TestHelper.assertEquals(SaveClientDTO.class, saveClientDTO, actual);
    }
}
