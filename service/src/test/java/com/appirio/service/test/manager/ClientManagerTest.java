/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.appirio.service.billingaccount.api.Client;
import com.appirio.service.billingaccount.dao.ClientDAO;
import com.appirio.service.billingaccount.dto.SaveClientDTO;
import com.appirio.service.billingaccount.manager.ClientManager;
import com.appirio.service.test.BaseTest;
import com.appirio.service.test.api.TestHelper;
import com.appirio.supply.SupplyException;
import com.appirio.supply.dataaccess.QueryResult;
import com.appirio.supply.dataaccess.db.IdGenerator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;


/**
 * Test ClientManager.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class ClientManagerTest extends BaseTest {
    /**
     * The user id for testing.
     */
    private static final String USER_ID = "123";

    /**
     * The DAO used for testing.
     */
    private final ClientDAO dao = Mockito.mock(ClientDAO.class);

    /**
     * The id generator used for testing.
     */
    private final IdGenerator idGenerator = Mockito.mock(IdGenerator.class);

    /**
     * The clients data.
     */
    private List<Client> data = new ArrayList<Client>();

    /**
     * Manager being tested
     */
    private ClientManager manager;

    /**
     * Prepare the needed data for testing
     *
     * @throws Exception
     *             to junit
     */
    @Before
    public void before() throws Exception {
        // prepare some clients data
        Client client1 = new Client(1L, "ACME", "Active", TestHelper.toDate("2018-04-28T07:24Z"), TestHelper.toDate(
            "2019-12-31T17:00Z"), "ACME", null);
        Client client2 = new Client(2L, "TopCoder", "Inactive", TestHelper.toDate("2018-03-28T16:30Z"), TestHelper
            .toDate("2019-12-31T17:00Z"), "TCS", null);
        Client client3 = new Client(3L, "Notus", "Active", TestHelper.toDate("2010-03-20T11:20Z"), TestHelper.toDate(
            "2011-01-11T09:00Z"), "Notus-code", null);

        data.add(client1);
        data.add(client2);
        data.add(client3);

        // mock methods and return values
        QueryResult<List<Client>> result = new QueryResult<List<Client>>(data);
        when(dao.findAllClients(anyObject())).thenReturn(result);

        for (Client client : data) {
            when(dao.getClientById(client.getId())).thenReturn(client);
            when(dao.getClientByName(client.getName())).thenReturn(client);
        }

        // mock add new client
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Client client = new Client();
                client.setId((Long) args[0]);
                client.setName((String) args[1]);
                client.setStatus(((Long) args[2]) == 1 ? "Active" : "Inactive");
                client.setStartDate((Date) args[3]);
                client.setEndDate((Date) args[4]);
                client.setCodeName((String) args[5]);
                client.setCustomerNumber((String) args[7]);

                data.add(client);
                when(dao.getClientById(client.getId())).thenReturn(client);
                when(dao.getClientByName(client.getName())).thenReturn(client);
                return null;
            }
        }).when(dao).addNewClient(anyLong(), anyString(), anyLong(), any(Date.class), any(Date.class), anyString(),
            anyString(), anyString());

        // mock update client
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                for (Client client : data) {
                    if (client.getId() == (Long) args[0]) {
                        client.setName((String) args[1]);
                        client.setStatus(((Long) args[2]) == 1 ? "Active" : "Inactive");
                        client.setStartDate((Date) args[3]);
                        client.setEndDate((Date) args[4]);
                        client.setCodeName((String) args[5]);
                        client.setCustomerNumber((String) args[7]);
                    }
                }
                return null;
            }
        }).when(dao).updateClient(anyLong(), anyString(), anyLong(), any(Date.class), any(Date.class), anyString(),
            anyString(), anyString());

        // mock id generator
        doAnswer(new Answer<Long>() {
            @Override
            public Long answer(InvocationOnMock invocation) throws Throwable {
                return (long) (data.size() + 1);
            }
        }).when(idGenerator).getNextId();

        // create manager
        manager = new ClientManager(dao, idGenerator);
    }

    /**
     * Test ClientManager.findAllClients to find all available clients.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testFindAllClients() throws Exception {
        QueryResult<List<Client>> result = manager.findAllClients(createQueryParam(""));

        // Verify result
        assertEquals(data.size(), result.getData().size());
        for (Client client : result.getData()) {
            checkClient(client, data);
        }
        verify(dao).findAllClients(anyObject());
    }

    /**
     * <p>
     * Test ClientManager.addNewClient to add new client.
     * </p>
     * <p>
     * Expects the new client is added in persistence.
     * </p>
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testAddNewClient() throws Exception {
        SaveClientDTO clientDTO = new SaveClientDTO();
        clientDTO.setName("Test Client4");
        clientDTO.setStatus("Active");
        clientDTO.setCodeName("CodeName 1");
        clientDTO.setCustomerNumber("number");

        // invoke method
        Client newClient = manager.addNewClient(clientDTO, USER_ID);

        // verify
        assertEquals(data.size(), 4); // one is added
        assertNotNull(newClient.getId());
        assertEquals(newClient.getId().longValue(), 4);
        assertEquals(newClient.getName(), clientDTO.getName());

        TestHelper.assertEquals(Client.class, data.get(3), newClient);

        // add another
        clientDTO.setName("Test Client5");
        clientDTO.setStatus("Inactive");
        clientDTO.setStartDate(TestHelper.toDate("2018-05-22T09:00Z"));

        Client newClient2 = manager.addNewClient(clientDTO, USER_ID);

        // verify
        assertEquals(data.size(), 5); // another one is added
        assertNotNull(newClient2.getId());
        assertEquals(newClient2.getId().longValue(), 5);
        assertEquals(newClient2.getName(), clientDTO.getName());

        verify(dao, times(2)).addNewClient(anyLong(), anyString(), anyLong(), any(Date.class), any(Date.class),
            anyString(), anyString(), anyString());
    }

    /**
     * <p>
     * Test ClientManager.addNewClient when adding new client with same name as existing one.
     * </p>
     * <p>
     * Expects an exception is thrown.
     * </p>
     *
     * @throws Exception
     *             to junit
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNewClient_Exist() throws Exception {
        SaveClientDTO clientDTO = new SaveClientDTO();
        clientDTO.setName(data.get(0).getName());
        clientDTO.setStatus("Active");
        clientDTO.setCodeName("CodeName 1");
        clientDTO.setCustomerNumber("number");

        // invoke method
        manager.addNewClient(clientDTO, USER_ID);
    }

    /**
     * <p>
     * Test ClientManager.addNewClient when adding new client with same name as invalid status.
     * </p>
     * <p>
     * Expects an exception is thrown.
     * </p>
     *
     * @throws Exception
     *             to junit
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNewClient_InvalidStatus() throws Exception {
        SaveClientDTO clientDTO = new SaveClientDTO();
        clientDTO.setName(data.get(0).getName());
        clientDTO.setStatus("Pending");
        clientDTO.setCodeName("CodeName 1");
        clientDTO.setCustomerNumber("number");

        // invoke method
        manager.addNewClient(clientDTO, USER_ID);
    }

    /**
     * Test ClientManager.getClientById to get client by id
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testGetClientById() {
        Client client = manager.getClientById(1L);

        // verify
        assertEquals("ACME", client.getName());
        verify(dao, atLeastOnce()).getClientById(anyLong());
    }

    /**
     * <p>
     * Test ClientManager.updateClient to update client in persistence.
     * </p>
     * <p>
     * Expects the new client is updated.
     * </p>
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testUpdateClient() throws Exception {
        SaveClientDTO clientDTO = new SaveClientDTO();
        clientDTO.setName("Notus2");
        clientDTO.setStatus("Inactive");

        // invoke method
        Client updated = manager.updateClient(3L, clientDTO, USER_ID);

        // verify
        assertEquals(updated.getId().longValue(), 3);
        assertEquals(updated.getName(), clientDTO.getName());
        assertEquals(updated.getStatus(), clientDTO.getStatus());

        // update another one
        clientDTO = new SaveClientDTO();
        clientDTO.setName("TopCoder");
        clientDTO.setStatus("Active");
        clientDTO.setStartDate(TestHelper.toDate("2018-04-23T11:00Z"));

        // invoke method
        updated = manager.updateClient(2L, clientDTO, USER_ID);

        // verify
        assertEquals(updated.getId().longValue(), 2);
        assertEquals(updated.getName(), clientDTO.getName());
        assertEquals(updated.getStatus(), clientDTO.getStatus());

        verify(dao, times(2)).updateClient(anyLong(), anyString(), anyLong(), any(Date.class), any(Date.class),
            anyString(), anyString(), anyString());
    }

    /**
     * <p>
     * Test ClientManager.updateClient when client to update does not exist.
     * </p>
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testUpdateClient_NotExist() throws Exception {
        SaveClientDTO clientDTO = new SaveClientDTO();
        clientDTO.setName("Notus2");
        clientDTO.setStatus("Inactive");

        try {
            manager.updateClient(9999L, clientDTO, USER_ID);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_NOT_FOUND, e.getStatusCode());
        }
    }

    /**
     * <p>
     * Test ClientManager.updateClient when client with same name exists.
     * </p>
     *
     * @throws Exception
     *             to junit
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateClient_NotUniqueName() throws Exception {
        SaveClientDTO clientDTO = new SaveClientDTO();
        clientDTO.setName("ACME");
        clientDTO.setStatus("Inactive");
        manager.updateClient(3L, clientDTO, USER_ID);
    }

    /**
     * Verify that a client from manager contains correct information.
     *
     * @param actual
     *            the client to verify
     * @param data
     *            the correct data to compare
     * @throws Exception
     *             if any error occurs
     */
    private static void checkClient(Client actual, List<Client> data) throws Exception {
        for (Client expected : data) {
            if (expected.getId() == actual.getId()) {
                TestHelper.assertEquals(Client.class, expected, actual);
            }
        }
    }
}
