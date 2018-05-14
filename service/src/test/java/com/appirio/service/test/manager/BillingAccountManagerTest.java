/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */

package com.appirio.service.test.manager;

import com.appirio.service.billingaccount.api.BillingAccount;
import com.appirio.service.billingaccount.api.BillingAccountUser;
import com.appirio.service.billingaccount.api.IdDTO;
import com.appirio.service.billingaccount.api.PaymentTermsDTO;
import com.appirio.service.billingaccount.dao.BillingAccountDAO;
import com.appirio.service.billingaccount.dao.SequenceDAO;
import com.appirio.service.billingaccount.dto.TCUserDTO;
import com.appirio.service.billingaccount.manager.BillingAccountManager;
import com.appirio.service.test.dao.GenericDAOTest;
import com.appirio.supply.SupplyException;
import com.appirio.supply.dataaccess.QueryResult;
import com.appirio.supply.dataaccess.db.IdGenerator;
import com.appirio.tech.core.auth.AuthUser;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class BillingAccountManagerTest extends GenericDAOTest {
    private BillingAccountDAO billingAccountDAO = mock(BillingAccountDAO.class);
    private IdGenerator generator = mock(IdGenerator.class);
    private SequenceDAO sequenceDAO = mock(SequenceDAO.class);
    private BillingAccountManager unit = new BillingAccountManager(billingAccountDAO, generator, generator, sequenceDAO);

    @Test
    public void testSearchBillingAccounts() {
        QueryResult<List<BillingAccount>> expected = getListQueryResult();
        when(billingAccountDAO.searchBillingAccounts(anyObject())).thenReturn(expected);
        QueryResult<List<BillingAccount>> result = unit.searchBillingAccounts(createQueryParam(""));
        assertEquals(2, result.getData().size());
        verify(billingAccountDAO).searchBillingAccounts(anyObject());
    }

    @Test
    public void testSearchMyBillingAccounts() {
        QueryResult<List<BillingAccount>> expected = getListQueryResult();
        when(billingAccountDAO.searchMyBillingAccounts(anyObject(), anyObject())).thenReturn(expected);
        QueryResult<List<BillingAccount>> result = unit.searchMyBillingAccounts(1L, createQueryParam(""));
        assertEquals(2, result.getData().size());
        verify(billingAccountDAO).searchMyBillingAccounts(anyObject(), anyObject());
    }


    public void testCreateBillingAccount() throws SupplyException {
        QueryResult<List<BillingAccount>> expected = getListQueryResult();
        expected.getData().remove(1);

        when(billingAccountDAO.getBillingAccount(anyObject())).thenReturn(expected);
        when(billingAccountDAO.createBillingAccount(anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(), false)).thenReturn(1L);

        when(generator.getNextId()).thenReturn(1L);
        when(billingAccountDAO.checkCompanyExists(anyObject())).thenReturn(new IdDTO());

        BillingAccount test = new BillingAccount();
        test.setPaymentTerms(new PaymentTermsDTO());

        QueryResult<List<BillingAccount>> result = unit.createBillingAccount(new AuthUser(), test);
        assertEquals(1, result.getData().size());

        verify(generator).getNextId();
        verify(billingAccountDAO).createBillingAccount(anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(), false);
        verify(billingAccountDAO).getBillingAccount(anyObject());
    }

    @Test
    public void testGetBillingAccount() {
        QueryResult<List<BillingAccount>> expected = getListQueryResult();
        expected.getData().remove(1);

        when(billingAccountDAO.getBillingAccount(anyObject())).thenReturn(expected);
        QueryResult<List<BillingAccount>> result = unit.getBillingAccount(1L);
        assertEquals(1, result.getData().size());
        verify(billingAccountDAO).getBillingAccount(anyObject());
    }


    public void testUpdateBillingAccount() throws SupplyException {
        QueryResult<List<BillingAccount>> expected = getListQueryResult();
        expected.getData().remove(1);

        when(billingAccountDAO.getBillingAccount(anyObject())).thenReturn(expected);

        BillingAccount test = new BillingAccount();
        test.setPaymentTerms(new PaymentTermsDTO());
        QueryResult<List<BillingAccount>> result = unit.updateBillingAccount(new AuthUser(), test);
        assertEquals(1, result.getData().size());
        verify(billingAccountDAO).updateBillingAccount(anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(),
                anyObject(), false);
        verify(billingAccountDAO).getBillingAccount(anyObject());
    }

    @Test
    public void testGetBillingAccountUsers() {
        QueryResult<List<BillingAccountUser>> expected = new QueryResult<>(new ArrayList<>());
        when(billingAccountDAO.getBillingAccountUsers(anyObject(), anyObject())).thenReturn(expected);
        QueryResult<List<BillingAccountUser>> result = unit.getBillingAccountUsers(1L, createQueryParam(""));
        assertEquals(0, result.getData().size());
        verify(billingAccountDAO).getBillingAccountUsers(anyObject(), anyObject());
    }

    //@Test
    public void testRemoveBillingAccountUsers() throws SupplyException {
        unit.removeUserFromBillingAccount(1L, 1L);
        verify(billingAccountDAO).removeUserFromBillingAccount(anyObject(), anyObject());
    }

    public void testAddUserToBillingAccount() throws SupplyException {
        QueryResult<List<BillingAccount>> expected = getListQueryResult();
        QueryResult<List<BillingAccountUser>> expectedUsers = new QueryResult<>(new ArrayList<>());
        IdDTO expectedId = new IdDTO(1L);
        TCUserDTO expectedUser = new TCUserDTO(1L,"handle");
        expected.getData().remove(1);

        when(billingAccountDAO.getBillingAccount(anyObject())).thenReturn(expected);
        when(billingAccountDAO.checkUserExists(anyObject())).thenReturn(expectedId);
        when(billingAccountDAO.getBillingAccountUsers(anyObject(), createQueryParam(""))).thenReturn(expectedUsers);
        when(billingAccountDAO.getTCUserById(anyObject())).thenReturn(expectedUser);
        QueryResult<List<BillingAccount>> result = unit.addUserToBillingAccount(new AuthUser(), 1L, 1L);
        assertEquals(1, result.getData().size());
        verify(billingAccountDAO).addUserToBillingAccount(anyObject(), anyObject(), anyObject());
        verify(billingAccountDAO).getBillingAccount(anyObject());
        verify(billingAccountDAO).checkUserExists(anyObject());
        verify(billingAccountDAO).getBillingAccountUsers(anyObject(), createQueryParam(""));
    }

    private QueryResult<List<BillingAccount>> getListQueryResult() {
        List<BillingAccount> billingAccounts = new ArrayList<>();

        billingAccounts.add(new BillingAccount(1L, "1", "Active", new Date(), new Date(), 500.0f, 1.0f, "po1",
        		new PaymentTermsDTO(1L, "30 Days"), "description1", "subscription#1", 1L, 0L, 1L, false ));
        billingAccounts.add(new BillingAccount(2L, "2", "Active", new Date(), new Date(), 500.0f, 1.0f, "po2",
        		new PaymentTermsDTO(1L, "30 Days"), "description2", "subscription#2", 1L, 0L, 1L, false));
        QueryResult<List<BillingAccount>> expected = new QueryResult<>();
        expected.setData(billingAccounts);
        return expected;
    }
}
