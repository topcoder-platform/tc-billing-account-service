/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.appirio.service.billingaccount.api.BillingAccount;
import com.appirio.service.billingaccount.api.BillingAccountFees;
import com.appirio.service.billingaccount.api.PaymentTermsDTO;
import com.appirio.service.billingaccount.api.UserIdDTO;
import com.appirio.service.billingaccount.manager.BillingAccountManager;
import com.appirio.service.billingaccount.resources.BillingAccountResource;
import com.appirio.service.test.BaseTest;
import com.appirio.supply.dataaccess.QueryResult;
import com.appirio.tech.core.api.v3.exception.APIRuntimeException;
import com.appirio.tech.core.api.v3.request.PostPutRequest;
import com.appirio.tech.core.auth.AuthUser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;


/**
 * Test BillingAccountResource.
 * <p>
 * Added in Topcoder - Add Unit Tests For TC Billing Accounts Service v1.0
 * </p>
 *
 * @author TCSCODER
 * @version 1.0
 */
public class BillingAccountResourceTest extends BaseTest {
    /**
     * The manager being used for testing.
     */
    private BillingAccountManager mockBillingAccountManager;

    /**
     * The resource being tested.
     */
    private BillingAccountResource unit;

    /**
     * Setup the resource and manager
     */
    @Before
    public void before() {
        mockBillingAccountManager = mock(BillingAccountManager.class);
        unit = new BillingAccountResource(mockBillingAccountManager);
    }

    /**
     * Test BillingAccountResource.searchBillingAccounts to verify that it delegates the process to the manager.
     */
    @Test
    public void testSearchBillingAccounts() {
        AuthUser authUser = createUser("3");
        authUser.getRoles().add("administrator");
        unit.searchBillingAccounts(authUser, createQueryParam(""), null);
        verify(mockBillingAccountManager).searchBillingAccounts(anyObject());
    }

    /**
     * Test BillingAccountResource.searchBillingAccounts when the user does not have administrator role.
     */
    @Test
    public void testSearchBillingAccounts_NotAdmin() {
        AuthUser authUser = createUser("3");

        try {
            unit.searchBillingAccounts(authUser, createQueryParam(""), null);
        } catch (APIRuntimeException e) {
            assertEquals(HttpServletResponse.SC_FORBIDDEN, e.getHttpStatus());
        }
    }

    /**
     * Test BillingAccountResource.createBillingAccount to verify that it delegates the process to the manager.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testCreateBillingAccount() throws Exception {
        AuthUser authUser = createUser("3");
        authUser.getRoles().add("administrator");

        // no method, defaults to create new billing account
        PostPutRequest request1 = mock(PostPutRequest.class);
        when(request1.getParam()).thenReturn(mock(BillingAccount.class));
        when(request1.getMethod()).thenReturn(null);

        unit.createBillingAccount(authUser, request1);

        // method PUT or PATCH means to update billing account
        PostPutRequest request2 = mock(PostPutRequest.class);
        when(request2.getParam()).thenReturn(mock(BillingAccount.class));
        when(request2.getMethod()).thenReturn("put");

        unit.createBillingAccount(authUser, request2);

        // POST method same as default
        PostPutRequest request3 = mock(PostPutRequest.class);
        when(request3.getParam()).thenReturn(mock(BillingAccount.class));
        when(request3.getMethod()).thenReturn("post");

        unit.createBillingAccount(authUser, request3);

        // add new account twice
        verify(mockBillingAccountManager, times(2)).createBillingAccount(anyObject(), anyObject());

        // update once
        verify(mockBillingAccountManager).updateBillingAccount(anyObject(), anyObject());
    }

    /**
     * Test BillingAccountResource.createBillingAccount when the user does not have administrator role.
     */
    @Test
    public void testCreateBillingAccount_NotAdmin() {
        AuthUser authUser = createUser("3");

        try {
            unit.createBillingAccount(authUser, null);
        } catch (APIRuntimeException e) {
            assertEquals(HttpServletResponse.SC_FORBIDDEN, e.getHttpStatus());
        }
    }

    /**
     * Test BillingAccountResource.getBillingAccountsById to verify that it delegates the process to the manager.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testGetBillingAccountsById() {
        AuthUser authUser = createUser("3");
        authUser.getRoles().add("administrator");

        // prepare mock billing accounts to return
        List<BillingAccount> billingAccounts = new ArrayList<>();

        billingAccounts.add(new BillingAccount(1l, "1", "Active", new Date(), new Date(), 500.0f, 1.0f, "po1",
            new PaymentTermsDTO(1l, "30 Days"), "description1", "subscription#1", 1l, 0l, 1l, false));

        QueryResult queryResult = mock(QueryResult.class);
        when(queryResult.getData()).thenReturn(billingAccounts);
        when(mockBillingAccountManager.getBillingAccount(1L)).thenReturn(queryResult);

        // invoke method
        unit.getBillingAccountsById(authUser, 1L);

        // verify
        verify(mockBillingAccountManager).getBillingAccount(anyLong());
    }

    /**
     * Test BillingAccountResource.getBillingAccountsById when the user does not have administrator role.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testGetBillingAccountsById_NotAdmin() {
        AuthUser authUser = createUser("3");

        try {
            unit.getBillingAccountsById(authUser, 1L);
        } catch (APIRuntimeException e) {
            assertEquals(HttpServletResponse.SC_FORBIDDEN, e.getHttpStatus());
        }
    }

    /**
     * Test BillingAccountResource.getBillingAccountsById when no billing account is found for given id.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testGetBillingAccountsById_NotFound() {
        AuthUser authUser = createUser("3");
        authUser.getRoles().add("administrator");

        QueryResult queryResult = mock(QueryResult.class);
        when(queryResult.getData()).thenReturn(Mockito.anyList());
        when(mockBillingAccountManager.getBillingAccount(2L)).thenReturn(queryResult);

        try {
            unit.getBillingAccountsById(authUser, 2L);
        } catch (APIRuntimeException e) {
            assertEquals(HttpServletResponse.SC_NOT_FOUND, e.getHttpStatus());
        }
    }

    /**
     * Test BillingAccountResource.updateBillingAccount to verify that it delegates the process to the manager.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testUpdateBillingAccount() throws Exception {
        AuthUser authUser = createUser("3");
        authUser.getRoles().add("administrator");

        // prepare mock billing accounts to return
        List<BillingAccount> billingAccounts = new ArrayList<>();

        billingAccounts.add(new BillingAccount(1l, "1", "Active", new Date(), new Date(), 500.0f, 1.0f, "po1",
            new PaymentTermsDTO(1l, "30 Days"), "description1", "subscription#1", 1l, 0l, 1l, false));

        QueryResult queryResult = mock(QueryResult.class);
        when(queryResult.getData()).thenReturn(billingAccounts);
        when(mockBillingAccountManager.getBillingAccount(1L)).thenReturn(queryResult);
        when(mockBillingAccountManager.updateBillingAccount(anyObject(), anyObject())).thenReturn(queryResult);

        PostPutRequest request1 = mock(PostPutRequest.class);
        when(request1.getParam()).thenReturn(mock(BillingAccount.class));

        unit.updateBillingAccount(authUser, 1L, request1);

        verify(mockBillingAccountManager).updateBillingAccount(anyObject(), anyObject());
    }

    /**
     * Test BillingAccountResource.updateBillingAccount when the user does not have administrator role.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testUpdateBillingAccount_NotAdmin() throws Exception {
        AuthUser authUser = createUser("3");

        PostPutRequest request1 = mock(PostPutRequest.class);
        when(request1.getParam()).thenReturn(mock(BillingAccount.class));

        try {
            unit.updateBillingAccount(authUser, 1L, request1);
        } catch (APIRuntimeException e) {
            assertEquals(HttpServletResponse.SC_FORBIDDEN, e.getHttpStatus());
        }
    }

    /**
     * Test BillingAccountResource.getBillingAccountUsers to verify that it delegates the process to the manager.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testGetBillingAccountUsers() {
        AuthUser authUser = createUser("3");
        authUser.getRoles().add("administrator");

        // prepare mock billing accounts to return
        List<BillingAccount> billingAccounts = new ArrayList<>();

        billingAccounts.add(new BillingAccount(1l, "1", "Active", new Date(), new Date(), 500.0f, 1.0f, "po1",
            new PaymentTermsDTO(1l, "30 Days"), "description1", "subscription#1", 1l, 0l, 1l, false));

        QueryResult queryResult = mock(QueryResult.class);
        when(queryResult.getData()).thenReturn(billingAccounts);
        when(mockBillingAccountManager.getBillingAccount(1L)).thenReturn(queryResult);

        // invoke method
        unit.getBillingAccountUsers(authUser, 1L, createQueryParam(""), null);

        verify(mockBillingAccountManager).getBillingAccountUsers(anyObject(), anyObject());
    }

    /**
     * Test BillingAccountResource.getBillingAccountUsers when the user does not have administrator role.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testGetBillingAccountUsers_NotAdmin() {
        AuthUser authUser = createUser("3");

        try {
            unit.getBillingAccountUsers(authUser, 1L, createQueryParam(""), null);
        } catch (APIRuntimeException e) {
            assertEquals(HttpServletResponse.SC_FORBIDDEN, e.getHttpStatus());
        }
    }

    /**
     * Test BillingAccountResource.addUserToBillingAccount to verify that it delegates the process to the manager.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testAddUserToBillingAccount() throws Exception {
        AuthUser authUser = createUser("3");
        authUser.getRoles().add("administrator");

        // prepare mock billing accounts to return
        List<BillingAccount> billingAccounts = new ArrayList<>();

        billingAccounts.add(new BillingAccount(1l, "1", "Active", new Date(), new Date(), 500.0f, 1.0f, "po1",
            new PaymentTermsDTO(1l, "30 Days"), "description1", "subscription#1", 1l, 0l, 1l, false));

        QueryResult queryResult = mock(QueryResult.class);
        when(queryResult.getData()).thenReturn(billingAccounts);
        when(mockBillingAccountManager.getBillingAccount(1L)).thenReturn(queryResult);

        UserIdDTO userIdDTO = mock(UserIdDTO.class);
        when(userIdDTO.getUserId()).thenReturn(1L);

        PostPutRequest request1 = mock(PostPutRequest.class);
        when(request1.getParam()).thenReturn(userIdDTO);

        // invoke method
        unit.addUserToBillingAccount(authUser, 1L, request1);

        verify(mockBillingAccountManager).addUserToBillingAccount(anyObject(), anyObject(), anyObject());
    }

    /**
     * Test BillingAccountResource.addUserToBillingAccount when the user does not have administrator role.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testAddUserToBillingAccount_NotAdmin() throws Exception {
        AuthUser authUser = createUser("3");
        PostPutRequest request1 = mock(PostPutRequest.class);

        try {
            unit.addUserToBillingAccount(authUser, 1L, request1);
        } catch (APIRuntimeException e) {
            assertEquals(HttpServletResponse.SC_FORBIDDEN, e.getHttpStatus());
        }
    }

    /**
     * Test BillingAccountResource.createBillingAccountFees to verify that it delegates the process to the manager.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testCreateBillingAccountFees() throws Exception {
        AuthUser authUser = createUser("3");
        authUser.getRoles().add("administrator");

        PostPutRequest request1 = mock(PostPutRequest.class);
        when(request1.getParam()).thenReturn(new BillingAccountFees());

        unit.createBillingAccountFees(authUser, 1L, request1);

        verify(mockBillingAccountManager).createBillingAccountFees(anyObject(), anyObject(), anyLong());
    }

    /**
     * Test BillingAccountResource.createBillingAccountFees when the user does not have administrator role.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testCreateBillingAccountFees_NotAdmin() throws Exception {
        AuthUser authUser = createUser("3");
        PostPutRequest request1 = mock(PostPutRequest.class);

        try {
            unit.createBillingAccountFees(authUser, 1L, request1);
        } catch (APIRuntimeException e) {
            assertEquals(HttpServletResponse.SC_FORBIDDEN, e.getHttpStatus());
        }
    }

    /**
     * Test BillingAccountResource.updateBillingAccountFees to verify that it delegates the process to the manager.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testUpdateBillingAccountFees() throws Exception {
        AuthUser authUser = createUser("3");
        authUser.getRoles().add("administrator");

        PostPutRequest request1 = mock(PostPutRequest.class);
        when(request1.getParam()).thenReturn(new BillingAccountFees());

        unit.updateBillingAccountFees(authUser, 1L, request1);

        verify(mockBillingAccountManager).updateBillingAccountFees(anyObject(), anyObject(), anyLong());
    }

    /**
     * Test BillingAccountResource.updateBillingAccountFees when the user does not have administrator role.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testUpdateBillingAccountFees_NotAdmin() throws Exception {
        AuthUser authUser = createUser("3");
        PostPutRequest request1 = mock(PostPutRequest.class);

        try {
            unit.updateBillingAccountFees(authUser, 1L, request1);
        } catch (APIRuntimeException e) {
            assertEquals(HttpServletResponse.SC_FORBIDDEN, e.getHttpStatus());
        }
    }

    /**
     * Test BillingAccountResource.getBillingAccountFees to verify that it delegates the process to the manager.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testGetBillingAccountFees() throws Exception {
        AuthUser authUser = createUser("3");
        authUser.getRoles().add("administrator");

        unit.getBillingAccountFees(authUser, 1L);

        verify(mockBillingAccountManager).getBillingAccountFees(anyLong());
    }

    /**
     * Test BillingAccountResource.getBillingAccountFees when the user does not have administrator role.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testGetBillingAccountFees_NotAdmin() throws Exception {
        AuthUser authUser = createUser("3");

        try {
            unit.getBillingAccountFees(authUser, 1L);
        } catch (APIRuntimeException e) {
            assertEquals(HttpServletResponse.SC_FORBIDDEN, e.getHttpStatus());
        }
    }

    /**
     * Test BillingAccountResource.removeUserFromBillingAccount to verify that it delegates the process to the
     * manager.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testRemoveUserFromBillingAccount() throws Exception {
        AuthUser authUser = createUser("3");
        authUser.getRoles().add("administrator");

        // prepare mock billing accounts to return
        List<BillingAccount> billingAccounts = new ArrayList<>();

        billingAccounts.add(new BillingAccount(1l, "1", "Active", new Date(), new Date(), 500.0f, 1.0f, "po1",
            new PaymentTermsDTO(1l, "30 Days"), "description1", "subscription#1", 1l, 0l, 1l, false));

        QueryResult queryResult = mock(QueryResult.class);
        when(queryResult.getData()).thenReturn(billingAccounts);
        when(mockBillingAccountManager.getBillingAccount(1L)).thenReturn(queryResult);

        // invoke method
        unit.removeUserFromBillingAccount(authUser, 1L, 1L);

        verify(mockBillingAccountManager).removeUserFromBillingAccount(anyObject(), anyObject());
    }

    /**
     * Test BillingAccountResource.removeUserFromBillingAccount when the user does not have administrator role.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testRemoveUserFromBillingAccount_NotAdmin() throws Exception {
        AuthUser authUser = createUser("3");

        try {
            unit.removeUserFromBillingAccount(authUser, 1L, 1L);
        } catch (APIRuntimeException e) {
            assertEquals(HttpServletResponse.SC_FORBIDDEN, e.getHttpStatus());
        }
    }

    /**
     * Test BillingAccountResource.searchMyBillingAccount to verify that it delegates the process to the manager.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testSearchMyBillingAccounts() {
        unit.searchMyBillingAccounts(createUser("3"), createQueryParam(""), null);
        verify(mockBillingAccountManager).searchMyBillingAccounts(anyObject(), anyObject());
    }

}
