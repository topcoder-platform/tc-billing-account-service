/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.appirio.service.billingaccount.api.Client;
import com.appirio.service.billingaccount.dao.ClientDAO;
import com.appirio.service.test.api.TestHelper;
import com.appirio.supply.SupplyException;
import com.appirio.supply.dataaccess.QueryResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.skife.jdbi.v2.Query;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Test ClientDAO.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Query.class)
public class ClientDAOTest extends GenericDAOTest {
    /**
     * DAO being tested
     */
    private ClientDAO dao;

    /**
     * Setup the jdbi instance and required data
     *
     * @throws SupplyException
     *             Exception for supply
     */
    @Before
    public void before() throws SupplyException {
        List<Client> clients = new ArrayList<Client>();

        clients.add(new Client(1L, "ACME", "Active", TestHelper.toDate("2018-04-28T07:24Z"), TestHelper.toDate(
            "2019-12-31T17:00Z"), "ACME", null));
        clients.add(new Client(2L, "TopCoder", "Inactive", TestHelper.toDate("2018-03-28T16:30Z"), TestHelper.toDate(
            "2019-12-31T17:00Z"), "TCS", null));

        List<Map<String, Object>> unmappedData = new ArrayList<Map<String, Object>>();
        unmappedData.add(new HashMap<>());
        unmappedData.get(0).put("ct", new BigDecimal(2));

        dao = createDAO(clients, unmappedData, ClientDAO.class);
    }

    /**
     * <p>
     * Test ClientDAO.findAllClients to retrieve all available clients.
     * </p>
     *
     * @throws IOException
     *             exception for IO
     */
    @Test
    public void testFindAllClients() throws IOException {
        QueryResult<List<Client>> clients = dao.findAllClients(createQueryParam(""));

        // Verify result
        assertNotNull(clients);
        assertEquals(clients.getData().size(), 2);

        // Verify that JDBI was called
        verifyListObjectWithMetadataQuery(mocker);

        // Verify that the generated SQL file is as expected
        verifyGeneratedSQL(mocker, "expected-sql/client/find-all-clients.sql", 0);
    }

    /**
     * <p>
     * Test ClientDAO.addNewClient to create new client.
     * </p>
     *
     * @throws IOException
     *             exception for IO
     */
    @Test
    public void testAddNewClient() throws IOException {
        // Invoke method
        dao.addNewClient(3L, "Notus", 1L, TestHelper.toDate("2010-03-20T11:20Z"), TestHelper.toDate(
            "2011-01-11T09:00Z"), "Notus-code", "heffan", null);

        // Verify that JDBI was called
        verifySingleUpdate(mocker);

        // Verify that the generated SQL file is as expected
        verifyGeneratedSQL(mocker, "expected-sql/client/create-client.sql", 0);
    }

    /**
     * <p>
     * Test ClientDAO.updateClient to update a client.
     * </p>
     *
     * @throws IOException
     *             exception for IO
     */
    @Test
    public void testUpdateClient() throws IOException {
        // Invoke method
        dao.updateClient(1L, "ACME", 1L, TestHelper.toDate("2018-05-20T11:20Z"), TestHelper.toDate("2019-03-11T09:00Z"),
            "ACM", "heffan", null);

        // Verify that JDBI was called
        verifySingleUpdate(mocker);

        // Verify that the generated SQL file is as expected
        verifyGeneratedSQL(mocker, "expected-sql/client/update-client.sql", 0);
    }

    /**
     * <p>
     * Test ClientDAO.getClientById to retrieve a client by id.
     * </p>
     *
     * @throws IOException
     *             exception for IO
     */
    @Test
    public void testGetClientById() throws IOException {
        // Invoke method
        Client client = dao.getClientById(1L);

        // Verify that JDBI was called
        verifySingleObjectQuery(mocker);
        assertEquals(client.getName(), "ACME");

        // Verify that the generated SQL file is as expected
        verifyGeneratedSQL(mocker, "expected-sql/client/get-client-by-id.sql", 0);
    }

    /**
     * <p>
     * Test ClientDAO.getClientByName to retrieve a client by name.
     * </p>
     *
     * @throws IOException
     *             exception for IO
     */
    @Test
    public void testGetClientByName() throws IOException {
        // Invoke method
        Client client = dao.getClientByName("ACME");

        // Verify that JDBI was called
        verifySingleObjectQuery(mocker);
        assertEquals(client.getId().longValue(), 1);

        // Verify that the generated SQL file is as expected
        verifyGeneratedSQL(mocker, "expected-sql/client/get-client-by-name.sql", 0);
    }
}
