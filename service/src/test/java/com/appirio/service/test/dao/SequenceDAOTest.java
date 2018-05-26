/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.dao;

import static org.junit.Assert.assertEquals;

import com.appirio.service.billingaccount.api.IdSequence;
import com.appirio.service.billingaccount.dao.SequenceDAO;
import com.appirio.supply.SupplyException;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Test SequenceDAO.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class SequenceDAOTest extends GenericDAOTest {
    /**
     * DAO being tested
     */
    private SequenceDAO dao;

    /**
     * Setup the jdbi instance and required data
     *
     * @throws SupplyException
     *             Exception for supply
     */
    @Before
    public void before() throws SupplyException {
        List<IdSequence> idSequences = new ArrayList<IdSequence>();
        IdSequence idSequence = new IdSequence();
        idSequence.setNextId(1001L);

        idSequences.add(idSequence);

        List<Map<String, Object>> unmappedData = new ArrayList<Map<String, Object>>();
        unmappedData.add(new HashMap<>());
        unmappedData.get(0).put("ct", new BigDecimal(2));

        dao = createDAO(idSequences, unmappedData, SequenceDAO.class);
    }

    /**
     * <p>
     * Test SequenceDAO.getIdSequence to get next id.
     * </p>
     *
     * @throws IOException
     *             exception for IO
     */
    @Test
    public void testGetIdSequence() throws IOException {
        // Invoke method
        IdSequence idSequence = dao.getIdSequence("seqName");

        // Verify that JDBI was called
        verifySingleObjectQuery(mocker);
        assertEquals(idSequence.getNextId().longValue(), 1001);

        // Verify that the generated SQL file is as expected
        verifyGeneratedSQL(mocker, "expected-sql/sequence/get-id-sequence.sql", 0);
    }

}
