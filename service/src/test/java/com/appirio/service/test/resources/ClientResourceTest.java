/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.appirio.service.billingaccount.dto.SaveClientDTO;
import com.appirio.service.billingaccount.manager.ClientManager;
import com.appirio.service.billingaccount.resources.ClientResource;
import com.appirio.service.test.BaseTest;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.exception.APIRuntimeException;
import com.appirio.tech.core.api.v3.request.PostPutRequest;
import com.appirio.tech.core.auth.AuthUser;

import org.junit.Test;

import javax.servlet.http.HttpServletResponse;


/**
 * Test ClientResource.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class ClientResourceTest extends BaseTest {
    /**
     * The manager being used for testing.
     */
    private static final ClientManager mockClientManager = mock(ClientManager.class);

    /**
     * The resource being tested.
     */
    private static final ClientResource unit = new ClientResource(mockClientManager);

    /**
     * Test ClientResource.findAllClients to verify that it delegates the process to the manager.
     */
    @Test
    public void testFindAllClients() {
        AuthUser authUser = createUser("3");
        authUser.getRoles().add("administrator");
        unit.findAllClients(authUser, createQueryParam("startDate=2019-05-19T09:00Z"), "endDate");
        unit.findAllClients(authUser, createQueryParam("endDate=2019-05-19T09:00Z"), null);
        verify(mockClientManager, times(2)).findAllClients(anyObject());
    }

    /**
     * <p>
     * Test ClientResource.findAllClients when the user does not have administrator role.
     * </p>
     * <p>
     * Expects an exception to be thrown.
     * </p>
     */
    @Test
    public void testFindAllClients_NotAdmin() {
        AuthUser authUser = createUser("3");
        try {
            unit.findAllClients(authUser, createQueryParam(""), "id");
        } catch (APIRuntimeException e) {
            assertEquals(HttpServletResponse.SC_FORBIDDEN, e.getHttpStatus());
        }
    }

    /**
     * Test ClientResource.addNewClient to verify that it delegates the process to the manager.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testAddNewClient() throws Exception {
        AuthUser authUser = createUser("3");
        authUser.getRoles().add("administrator");

        // no method, defaults to add new client
        PostPutRequest request1 = mock(PostPutRequest.class);
        when(request1.getParam()).thenReturn(mock(SaveClientDTO.class));
        when(request1.getMethod()).thenReturn(null);

        unit.addNewClient(authUser, request1);

        // method PUT or PATCH means to update client
        PostPutRequest request2 = mock(PostPutRequest.class);
        when(request2.getParam()).thenReturn(mock(SaveClientDTO.class));
        when(request2.getMethod()).thenReturn("put");

        unit.addNewClient(authUser, request2);

        // POST method same as default
        PostPutRequest request3 = mock(PostPutRequest.class);
        when(request3.getParam()).thenReturn(mock(SaveClientDTO.class));
        when(request3.getMethod()).thenReturn("post");

        unit.addNewClient(authUser, request3);

        // add new client twice
        verify(mockClientManager, times(2)).addNewClient(anyObject(), anyObject());

        // update once
        verify(mockClientManager, atLeastOnce()).updateClient(anyObject(), anyObject(), anyObject());
    }

    /**
     * Test ClientResource.addNewClient when the user does not have administrator role.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testAddNewClient_NotAdmin() throws Exception {
        AuthUser authUser = createUser("3");

        PostPutRequest request = mock(PostPutRequest.class);
        when(request.getParam()).thenReturn(mock(SaveClientDTO.class));
        when(request.getMethod()).thenReturn(null);

        try {
            unit.addNewClient(authUser, request);
        } catch (APIRuntimeException e) {
            assertEquals(HttpServletResponse.SC_FORBIDDEN, e.getHttpStatus());
        }
    }

    /**
     * Test ClientResource.addNewClient when the request does not have "param" field in the body.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testAddNewClient_NoParam() throws Exception {
        AuthUser authUser = createUser("3");
        authUser.getRoles().add("administrator");

        PostPutRequest request = mock(PostPutRequest.class);
        when(request.getParam()).thenReturn(null);
        when(request.getMethod()).thenReturn(null);

        try {
            unit.addNewClient(authUser, request);
        } catch (APIRuntimeException e) {
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getHttpStatus());
        }
    }

    /**
     * Test ClientResource.addNewClient when the request has invalid method.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testAddNewClient_InvalidMethod() throws Exception {
        AuthUser authUser = createUser("3");
        authUser.getRoles().add("administrator");

        PostPutRequest request = mock(PostPutRequest.class);
        when(request.getParam()).thenReturn(mock(SaveClientDTO.class));
        when(request.getMethod()).thenReturn("GET");

        try {
            unit.addNewClient(authUser, request);
        } catch (APIRuntimeException e) {
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getHttpStatus());
        }
    }

    /**
     * Test ClientResource.getClientById to verify that it delegates the process to the manager.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testGetClientById() {
        AuthUser authUser = createUser("3");
        authUser.getRoles().add("administrator");

        unit.getClientById(authUser, 1L);

        verify(mockClientManager).getClientById(anyLong());
    }

    /**
     * Test ClientResource.getClientById when the user does not have administrator role.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testGetClientById_NotAdmin() throws Exception {
        AuthUser authUser = createUser("3");
        try {
            unit.getClientById(authUser, 1L);
        } catch (APIRuntimeException e) {
            assertEquals(HttpServletResponse.SC_FORBIDDEN, e.getHttpStatus());
        }
    }

    /**
     * Test ClientResource.updateClient to verify that it delegates the process to the manager.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testUpdateClient() throws SupplyException {
        AuthUser authUser = createUser("3");
        authUser.getRoles().add("administrator");

        // no method, defaults to add new client
        PostPutRequest request1 = mock(PostPutRequest.class);
        when(request1.getParam()).thenReturn(mock(SaveClientDTO.class));

        unit.updateClient(authUser, 2L, request1);

        verify(mockClientManager, atLeastOnce()).updateClient(anyObject(), anyObject(), anyObject());
    }

    /**
     * Test ClientResource.updateClient when the user does not have administrator role.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testUpdateClient_NotAdmin() throws Exception {
        AuthUser authUser = createUser("3");

        PostPutRequest request = mock(PostPutRequest.class);
        when(request.getParam()).thenReturn(mock(SaveClientDTO.class));

        try {
            unit.updateClient(authUser, 2L, request);
        } catch (APIRuntimeException e) {
            assertEquals(HttpServletResponse.SC_FORBIDDEN, e.getHttpStatus());
        }
    }

    /**
     * Test ClientResource.updateClient when the request does not have "param" field in the body.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testUpdateClient_NoParam() throws Exception {
        AuthUser authUser = createUser("3");
        authUser.getRoles().add("administrator");

        PostPutRequest request = mock(PostPutRequest.class);
        when(request.getParam()).thenReturn(null);

        try {
            unit.updateClient(authUser, 2L, request);
        } catch (APIRuntimeException e) {
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getHttpStatus());
        }
    }
}
