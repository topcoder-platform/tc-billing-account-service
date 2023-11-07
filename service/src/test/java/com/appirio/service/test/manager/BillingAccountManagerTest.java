/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */

package com.appirio.service.test.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.appirio.service.billingaccount.api.BillingAccount;
import com.appirio.service.billingaccount.api.BillingAccountFees;
import com.appirio.service.billingaccount.api.BillingAccountUser;
import com.appirio.service.billingaccount.api.ChallengeFee;
import com.appirio.service.billingaccount.api.ChallengeFeePercentage;
import com.appirio.service.billingaccount.api.ChallengeType;
import com.appirio.service.billingaccount.api.HarmonyPublisher;
import com.appirio.service.billingaccount.api.IdDTO;
import com.appirio.service.billingaccount.api.IdSequence;
import com.appirio.service.billingaccount.api.PaymentTermsDTO;
import com.appirio.service.billingaccount.dao.BillingAccountDAO;
import com.appirio.service.billingaccount.dao.SequenceDAO;
import com.appirio.service.billingaccount.dto.TCUserDTO;
import com.appirio.service.billingaccount.manager.BillingAccountManager;
import com.appirio.service.test.BaseTest;
import com.appirio.supply.SupplyException;
import com.appirio.supply.dataaccess.QueryResult;
import com.appirio.supply.dataaccess.db.IdGenerator;
import com.appirio.tech.core.api.v3.request.QueryParameter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;


/**
 * Test BillingAccountManager.
 * <p>
 * Changes in v1.1 (Topcoder - Add Unit Tests For TC Billing Accounts Service):
 * <ul>
 * <li>Add more tests for untested public methods.</li>
 * <li>Cover more scenario for existed tests.</li>
 * <li>Add comments for the tests.</li>
 * </ul>
 * </p>
 *
 * @author TCSCODER
 * @version 1.1
 */
public class BillingAccountManagerTest extends BaseTest {
    /**
     * The billing account DAO used for testing.
     */
    private BillingAccountDAO billingAccountDAO = mock(BillingAccountDAO.class);

    /**
     * The id generator.
     */
    private IdGenerator generator = mock(IdGenerator.class);

    /**
     * The id sequence DAO used for testing.
     */
    private SequenceDAO sequenceDAO = mock(SequenceDAO.class);

    /**
     * The Harmony publisher used for testing.
     */
    private HarmonyPublisher publisher = mock(HarmonyPublisher.class);

    /**
     * Manager being tested
     */
    private BillingAccountManager unit = new BillingAccountManager(billingAccountDAO, generator, generator,
        sequenceDAO, publisher);

    /**
     * Test BillingAccountManager.searchBillingAccounts to find all billing accounts in persistence.
     */
    @Test
    public void testSearchBillingAccounts() {
        QueryResult<List<BillingAccount>> expected = getListQueryResult();
        when(billingAccountDAO.searchBillingAccounts(anyObject())).thenReturn(expected);
        QueryResult<List<BillingAccount>> result = unit.searchBillingAccounts(createQueryParam(""));
        assertEquals(2, result.getData().size());
        verify(billingAccountDAO).searchBillingAccounts(anyObject());
    }

    /**
     * Test BillingAccountManager.searchBillingAccounts to find all billing accounts of current user.
     */
    @Test
    public void testSearchMyBillingAccounts() {
        QueryResult<List<BillingAccount>> expected = getListQueryResult();
        when(billingAccountDAO.searchMyBillingAccounts(anyObject(), anyObject())).thenReturn(expected);
        QueryResult<List<BillingAccount>> result = unit.searchMyBillingAccounts(1L, createQueryParam(""));
        assertEquals(2, result.getData().size());
        verify(billingAccountDAO).searchMyBillingAccounts(anyObject(), anyObject());
    }

    /**
     * Test BillingAccountManager.createBillingAccount to add new billing account
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testCreateBillingAccount() throws Exception {
        QueryResult<List<BillingAccount>> expected = getListQueryResult();
        expected.getData().remove(1);

        // prepare the mock
        when(billingAccountDAO.getBillingAccount(anyObject())).thenReturn(expected);
        when(billingAccountDAO.createBillingAccount(anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
            anyObject(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
            anyObject(), anyObject(), eq(false))).thenReturn(1L);

        when(generator.getNextId()).thenReturn(1L);
        when(billingAccountDAO.checkCompanyExists(anyObject())).thenReturn(new IdDTO());
        when(billingAccountDAO.checkClientExists(anyObject())).thenReturn(new IdDTO());

        // new billing account to create
        BillingAccount test = new BillingAccount();
        test.setPaymentTerms(new PaymentTermsDTO());
        test.setStatus("Active");

        // invoke method
        QueryResult<List<BillingAccount>> result = unit.createBillingAccount(createUser("3"), test);

        // verify
        assertEquals(1, result.getData().size());

        verify(generator).getNextId();
        verify(billingAccountDAO).createBillingAccount(anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
            anyObject(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
            anyObject(), anyObject(), eq(false));
        verify(billingAccountDAO).getBillingAccount(anyObject());
    }

    /**
     * Test BillingAccountManager.createBillingAccount when company by given id does not exist
     *
     * @throws Exception
     *             to junit
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateBillingAccount_NotExistCompany() throws Exception {
        when(billingAccountDAO.checkCompanyExists(anyObject())).thenReturn(null);

        // new billing account to create
        BillingAccount test = new BillingAccount();
        test.setPaymentTerms(new PaymentTermsDTO());
        test.setStatus("Active");

        unit.createBillingAccount(createUser("3"), test);
    }

    /**
     * Test BillingAccountManager.createBillingAccount when client by given id does not exist
     *
     * @throws Exception
     *             to junit
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateBillingAccount_NotExistClient() throws Exception {
        when(billingAccountDAO.checkCompanyExists(anyObject())).thenReturn(new IdDTO());
        when(billingAccountDAO.checkClientExists(anyObject())).thenReturn(null);

        // new billing account to create
        BillingAccount test = new BillingAccount();
        test.setPaymentTerms(new PaymentTermsDTO());
        test.setStatus("Active");

        unit.createBillingAccount(createUser("3"), test);
    }

    /**
     * Test BillingAccountManager.getBillingAccount to retrieve a billing account
     */
    @Test
    public void testGetBillingAccount() {
        QueryResult<List<BillingAccount>> expected = getListQueryResult();
        expected.getData().remove(1);

        when(billingAccountDAO.getBillingAccount(anyObject())).thenReturn(expected);
        QueryResult<List<BillingAccount>> result = unit.getBillingAccount(1L);
        assertEquals(1, result.getData().size());
        verify(billingAccountDAO).getBillingAccount(anyObject());
    }

    /**
     * Test BillingAccountManager.updateBillingAccount to update a billing account
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testUpdateBillingAccount() throws Exception {
        QueryResult<List<BillingAccount>> expected = getListQueryResult();
        expected.getData().remove(1);

        when(billingAccountDAO.getBillingAccount(anyObject())).thenReturn(expected);
        when(billingAccountDAO.checkCompanyExists(anyObject())).thenReturn(new IdDTO());
        when(billingAccountDAO.checkClientExists(anyObject())).thenReturn(new IdDTO());

        BillingAccount test = new BillingAccount();
        test.setPaymentTerms(new PaymentTermsDTO());
        test.setStatus("Active");
        QueryResult<List<BillingAccount>> result = unit.updateBillingAccount(createUser("3"), test);
        assertEquals(1, result.getData().size());
        verify(billingAccountDAO).updateBillingAccount(anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
            anyObject(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject(), anyObject(),
            anyObject(), anyObject(), eq(false));
        verify(billingAccountDAO).getBillingAccount(anyObject());
    }

    /**
     * Test BillingAccountManager.updateBillingAccount when company by given id does not exist
     *
     * @throws Exception
     *             to junit
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateBillingAccount_NotExistCompany() throws Exception {
        when(billingAccountDAO.checkCompanyExists(anyObject())).thenReturn(null);

        // new billing account to create
        BillingAccount test = new BillingAccount();
        test.setPaymentTerms(new PaymentTermsDTO());
        test.setStatus("Active");

        unit.updateBillingAccount(createUser("3"), test);
    }

    /**
     * Test BillingAccountManager.updateBillingAccount when client by given id does not exist
     *
     * @throws Exception
     *             to junit
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateBillingAccount_NotExistClient() throws Exception {
        when(billingAccountDAO.checkCompanyExists(anyObject())).thenReturn(new IdDTO());
        when(billingAccountDAO.checkClientExists(anyObject())).thenReturn(null);

        // new billing account to create
        BillingAccount test = new BillingAccount();
        test.setPaymentTerms(new PaymentTermsDTO());
        test.setStatus("Active");

        unit.updateBillingAccount(createUser("3"), test);
    }

    /**
     * Test BillingAccountManager.getBillingAccountUsers to get users of a billing account.
     */
    @Test
    public void testGetBillingAccountUsers() {
        QueryResult<List<BillingAccountUser>> expected = new QueryResult<>(new ArrayList<>());
        when(billingAccountDAO.getBillingAccountUsers(anyObject(), anyObject())).thenReturn(expected);
        QueryResult<List<BillingAccountUser>> result = unit.getBillingAccountUsers(1L, createQueryParam(""));
        assertEquals(0, result.getData().size());
        verify(billingAccountDAO).getBillingAccountUsers(anyObject(), anyObject());
    }

    /**
     * Test BillingAccountManager.removeUserFromBillingAccount to remove a user from a billing account.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testRemoveUserFromBillingAccount() throws Exception {
        when(billingAccountDAO.getTCUserById(anyLong())).thenReturn(new TCUserDTO());
        when(billingAccountDAO.checkUserExists(anyString())).thenReturn(new IdDTO());
        when(billingAccountDAO.checkUserBelongsToBillingAccount(anyLong(), anyLong())).thenReturn(new IdDTO());
        unit.removeUserFromBillingAccount(1L, 1L);
        verify(billingAccountDAO).removeUserFromBillingAccount(anyObject(), anyObject());
    }

    /**
     * Test BillingAccountManager.removeUserFromBillingAccount when user does not have account.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testRemoveUserFromBillingAccount_NoUserAccount() throws Exception {
        when(billingAccountDAO.getTCUserById(anyLong())).thenReturn(new TCUserDTO());
        when(billingAccountDAO.checkUserExists(anyString())).thenReturn(null);
        try {
            unit.removeUserFromBillingAccount(1L, 1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getStatusCode());
        }

    }

    /**
     * Test BillingAccountManager.removeUserFromBillingAccount when removing a user who does not belong to the
     * billing account
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testRemoveUserFromBillingAccount_NotBelong() throws Exception {
        IdDTO expectedId = new IdDTO(1L);
        TCUserDTO expectedUser = new TCUserDTO(1L, "handle");

        when(billingAccountDAO.checkUserExists(anyObject())).thenReturn(expectedId);
        when(billingAccountDAO.getTCUserById(anyObject())).thenReturn(expectedUser);
        when(billingAccountDAO.checkUserBelongsToBillingAccount(anyLong(), anyLong())).thenReturn(null);
        try {
            unit.removeUserFromBillingAccount(1L, 1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.removeUserFromBillingAccount when user with given id does not exist
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testRemoveUserFromBillingAccount_UserNotExist() throws Exception {
        when(billingAccountDAO.getTCUserById(anyObject())).thenReturn(null);
        try {
            unit.removeUserFromBillingAccount(1L, 1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_NOT_FOUND, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.addUserToBillingAccount to add a user to a billing account
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testAddUserToBillingAccount() throws Exception {
        QueryResult<List<BillingAccount>> expected = getListQueryResult();
        QueryResult<List<BillingAccountUser>> expectedUsers = new QueryResult<>(new ArrayList<>());
        IdDTO expectedId = new IdDTO(1L);
        TCUserDTO expectedUser = new TCUserDTO(1L, "handle");
        expected.getData().remove(1);

        when(billingAccountDAO.getBillingAccount(anyObject())).thenReturn(expected);
        when(billingAccountDAO.checkUserExists(anyObject())).thenReturn(expectedId);
        when(billingAccountDAO.getBillingAccountUsers(anyObject(), any(QueryParameter.class))).thenReturn(
            expectedUsers);
        when(billingAccountDAO.getTCUserById(anyObject())).thenReturn(expectedUser);
        QueryResult<List<BillingAccount>> result = unit.addUserToBillingAccount(createUser("3"), 1L, 1L);
        assertEquals(1, result.getData().size());
        verify(billingAccountDAO).addUserToBillingAccount(anyObject(), anyObject(), anyObject());
        verify(billingAccountDAO).getBillingAccount(anyObject());
        verify(billingAccountDAO).checkUserExists(anyObject());
    }

    /**
     * Test BillingAccountManager.addUserToBillingAccount when user does not have account yet.
     * <p>
     * Verifies if user account is created first.
     * </p>
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testAddUserToBillingAccount_NoUserAccount() throws Exception {
        QueryResult<List<BillingAccount>> expected = getListQueryResult();
        TCUserDTO expectedUser = new TCUserDTO(1L, "handle");
        expected.getData().remove(1);

        when(billingAccountDAO.getBillingAccount(anyObject())).thenReturn(expected);
        when(billingAccountDAO.checkUserExists(anyObject())).thenReturn(null);
        when(billingAccountDAO.getTCUserById(anyObject())).thenReturn(expectedUser);
        when(generator.getNextId()).thenReturn(1L);
        QueryResult<List<BillingAccount>> result = unit.addUserToBillingAccount(createUser("3"), 1L, 1L);
        assertEquals(1, result.getData().size());
        verify(billingAccountDAO).addUserToBillingAccount(anyObject(), anyObject(), anyObject());
        verify(billingAccountDAO).getBillingAccount(anyObject());
        verify(billingAccountDAO).checkUserExists(anyObject());
        verify(billingAccountDAO).createUserAccount(anyLong(), anyString(), anyString());
    }

    /**
     * Test BillingAccountManager.addUserToBillingAccount when adding a user who already belongs to the billing
     * account
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testAddUserToBillingAccount_AlreadyBelong() throws Exception {
        QueryResult<List<BillingAccount>> expected = getListQueryResult();
        QueryResult<List<BillingAccountUser>> expectedUsers = new QueryResult<>(new ArrayList<>());
        IdDTO expectedId = new IdDTO(1L);
        TCUserDTO expectedUser = new TCUserDTO(1L, "handle");
        expected.getData().remove(1);

        when(billingAccountDAO.getBillingAccount(anyObject())).thenReturn(expected);
        when(billingAccountDAO.checkUserExists(anyObject())).thenReturn(expectedId);
        when(billingAccountDAO.getBillingAccountUsers(anyObject(), any(QueryParameter.class))).thenReturn(
            expectedUsers);
        when(billingAccountDAO.getTCUserById(anyObject())).thenReturn(expectedUser);
        when(billingAccountDAO.checkUserBelongsToBillingAccount(anyLong(), anyLong())).thenReturn(new IdDTO());
        try {
            unit.addUserToBillingAccount(createUser("3"), 1L, 1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.addUserToBillingAccount when user with given id does not exist
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testAddUserToBillingAccount_UserNotExist() throws Exception {
        when(billingAccountDAO.getTCUserById(anyObject())).thenReturn(null);
        try {
            unit.addUserToBillingAccount(createUser("3"), 1L, 1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_NOT_FOUND, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.getBillingAccountFees when project has an active challenge fee percentage.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testGetBillingAccountFees_ActivePercentage() throws Exception {
        IdDTO expectedId = new IdDTO(1L);
        ChallengeFeePercentage percentage = new ChallengeFeePercentage(1L, 1L, 5.0, true);

        when(billingAccountDAO.checkBillingAccountExists(anyLong())).thenReturn(expectedId);
        when(billingAccountDAO.getChallengeFeePercentage(anyLong())).thenReturn(percentage);

        BillingAccountFees fees = unit.getBillingAccountFees(1L);
        assertFalse(fees.isChallengeFeeFixed());
        assertEquals(percentage.getChallengeFeePercentage(), fees.getChallengeFeePercentage());
        verify(billingAccountDAO).getChallengeFeePercentage(anyLong());
    }

    /**
     * Test BillingAccountManager.getBillingAccountFees when project has an inactive challenge fee percentage.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testGetBillingAccountFees_InactivePercentage() throws Exception {
        IdDTO expectedId = new IdDTO(1L);
        ChallengeFeePercentage percentage = new ChallengeFeePercentage(1L, 1L, 5.0, false);

        ChallengeFee fee1 = new ChallengeFee(1L, 1L, 1L, 500.0, false, "description", false, "fee1");
        ChallengeFee fee2 = new ChallengeFee(2L, 1L, 2L, 500.0, false, "description", false, "fee2");
        List<ChallengeFee> fees = new ArrayList<ChallengeFee>();
        fees.add(fee1);
        fees.add(fee2);

        ChallengeType type = new ChallengeType();
        type.setChallengeTypeId(1L);
        type.setDescription("type1");
        List<ChallengeType> types = new ArrayList<ChallengeType>();
        types.add(type);

        when(billingAccountDAO.checkBillingAccountExists(anyLong())).thenReturn(expectedId);
        when(billingAccountDAO.getChallengeFeePercentage(anyLong())).thenReturn(percentage);
        when(billingAccountDAO.getChallengeFee(anyLong())).thenReturn(fees);
        when(billingAccountDAO.getProjectCategoriesReplatforming()).thenReturn(types);

        BillingAccountFees billingAccountFees = unit.getBillingAccountFees(1L);
        assertTrue(billingAccountFees.isChallengeFeeFixed());
        assertEquals(billingAccountFees.getChallengeFees().size(), 2);
        assertEquals(billingAccountFees.getChallengeFees().get(0).getChallengeTypeDescription(), type.getDescription());
        assertEquals(billingAccountFees.getChallengeFees().get(0).isStudio(), type.isStudio());

        verify(billingAccountDAO).getChallengeFeePercentage(anyLong());
        verify(billingAccountDAO).getChallengeFee(anyLong());
    }

    /**
     * Test BillingAccountManager.getBillingAccountFees when project id is not positive.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testGetBillingAccountFees_InvalidProjectId() throws Exception {
        try {
            unit.getBillingAccountFees(0);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.getBillingAccountFees when billing account does not exist.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testGetBillingAccountFees_BillingAccountNotExist1() throws Exception {
        when(billingAccountDAO.checkBillingAccountExists(anyLong())).thenReturn(null);

        try {
            unit.getBillingAccountFees(1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_NOT_FOUND, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.getBillingAccountFees when billing account does not exist.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testGetBillingAccountFees_BillingAccountNotExist2() throws Exception {
        IdDTO expectedId = new IdDTO(0L);

        when(billingAccountDAO.checkBillingAccountExists(anyLong())).thenReturn(expectedId);

        try {
            unit.getBillingAccountFees(1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_NOT_FOUND, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.getBillingAccountFees when no challenge fee for the billing account.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testGetBillingAccountFees_NoChallengeFee1() throws Exception {
        IdDTO expectedId = new IdDTO(1L);

        when(billingAccountDAO.checkBillingAccountExists(anyLong())).thenReturn(expectedId);
        when(billingAccountDAO.getChallengeFeePercentage(anyLong())).thenReturn(null);
        when(billingAccountDAO.getChallengeFee(anyLong())).thenReturn(null);

        try {
            unit.getBillingAccountFees(1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_NOT_FOUND, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.getBillingAccountFees when no challenge fee for the billing account.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testGetBillingAccountFees_NoChallengeFee2() throws Exception {
        IdDTO expectedId = new IdDTO(1L);

        when(billingAccountDAO.checkBillingAccountExists(anyLong())).thenReturn(expectedId);
        when(billingAccountDAO.getChallengeFeePercentage(anyLong())).thenReturn(null);
        when(billingAccountDAO.getChallengeFee(anyLong())).thenReturn(new ArrayList<ChallengeFee>());

        try {
            unit.getBillingAccountFees(1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_NOT_FOUND, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.getBillingAccountFees when no active challenge fee found.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testGetBillingAccountFees_NoActiveChallengeFee() throws Exception {
        IdDTO expectedId = new IdDTO(1L);
        ChallengeFeePercentage percentage = new ChallengeFeePercentage(1L, 1L, 5.0, false);

        ChallengeFee fee = new ChallengeFee(1L, 1L, 1L, 500.0, false, "description", true, "fee1");
        List<ChallengeFee> fees = new ArrayList<ChallengeFee>();
        fees.add(fee);

        when(billingAccountDAO.checkBillingAccountExists(anyLong())).thenReturn(expectedId);
        when(billingAccountDAO.getChallengeFeePercentage(anyLong())).thenReturn(percentage);
        when(billingAccountDAO.getChallengeFee(anyLong())).thenReturn(fees);

        try {
            unit.getBillingAccountFees(1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_NOT_FOUND, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.getBillingAccountFees when an error occurs while accessing persistence.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testGetBillingAccountFees_ServerError() throws Exception {
        when(billingAccountDAO.checkBillingAccountExists(anyLong())).thenThrow(new IllegalArgumentException());

        try {
            unit.getBillingAccountFees(1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.createBillingAccountFees to create a billing account fees.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testCreateBillingAccountFees() throws Exception {
        IdDTO expectedId = new IdDTO(1L);
        IdDTO expectedFeeId = new IdDTO(0L);

        ChallengeFee fee1 = new ChallengeFee(1L, 1L, 1L, 500.0, false, "description", false, "fee1");
        ChallengeFee fee2 = new ChallengeFee(2L, 1L, 2L, 500.0, true, "description", false, "fee2");
        List<ChallengeFee> fees = new ArrayList<ChallengeFee>();
        fees.add(fee1);
        fees.add(fee2);

        ChallengeType type = new ChallengeType();
        type.setChallengeTypeId(1L);
        type.setDescription("type1");
        List<ChallengeType> types = new ArrayList<ChallengeType>();
        types.add(type);

        IdSequence idSequence = mock(IdSequence.class);
        when(idSequence.getNextId()).thenReturn(1L);

        when(sequenceDAO.getIdSequence(anyString())).thenReturn(idSequence);

        BillingAccountFees billingAccountFees = new BillingAccountFees(true, null, fees);

        when(billingAccountDAO.checkBillingAccountExists(anyLong())).thenReturn(expectedId);
        when(billingAccountDAO.checkChallengeFeeExists(anyLong())).thenReturn(expectedFeeId);
        when(billingAccountDAO.getChallengeFeePercentage(anyLong())).thenReturn(null);
        when(billingAccountDAO.getChallengeFee(anyLong())).thenReturn(fees);
        when(billingAccountDAO.getProjectCategoriesReplatforming()).thenReturn(types);

        BillingAccountFees result = unit.createBillingAccountFees(createUser("3"), billingAccountFees, 1L);
        for (ChallengeFee fee : result.getChallengeFees()) {
            assertNotNull(fee.getCreatedAt());
            assertNotNull(fee.getUpdatedAt());
            assertNotNull(fee.getCreatedBy());
            assertNotNull(fee.getUpdatedBy());
            assertEquals(fee.getProjectId(), 1L);
        }

        verify(billingAccountDAO, times(fees.size())).createChallengeFee(anyLong(), anyLong(), anyInt(), anyLong(),
            anyDouble(), anyLong(), anyString(), anyBoolean());
        verify(billingAccountDAO).createChallengeFeePercentage(anyLong(), anyLong(), anyDouble(), anyBoolean(),
            anyLong());
    }

    /**
     * Test BillingAccountManager.createBillingAccountFees when project id is not positive.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testCreateBillingAccountFees_NotPositiveProjectId() throws Exception {
        ChallengeFee fee1 = new ChallengeFee(1L, 1L, 1L, 500.0, false, "description", false, "fee1");
        ChallengeFee fee2 = new ChallengeFee(2L, 1L, 2L, 500.0, false, "description", false, "fee2");
        List<ChallengeFee> fees = new ArrayList<ChallengeFee>();
        fees.add(fee1);
        fees.add(fee2);

        BillingAccountFees billingAccountFees = new BillingAccountFees(true, null, fees);

        try {
            unit.createBillingAccountFees(createUser("3"), billingAccountFees, 0L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.createBillingAccountFees when challenge fee is fixed but challenge fees is null.
     *
     * @throws Exception
     */
    @Test
    public void testCreateBillingAccountFees_FixedNullFees() throws Exception {
        BillingAccountFees billingAccountFees = new BillingAccountFees(true, null, null);

        try {
            unit.createBillingAccountFees(createUser("3"), billingAccountFees, 1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.createBillingAccountFees when challenge fee is fixed but challenge fees is empty.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testCreateBillingAccountFees_FixedEmptyFees() throws Exception {
        BillingAccountFees billingAccountFees = new BillingAccountFees(true, null, new ArrayList<ChallengeFee>());

        try {
            unit.createBillingAccountFees(createUser("3"), billingAccountFees, 1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.createBillingAccountFees when challenge fee is fixed but fee percentage is
     * provided.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testCreateBillingAccountFees_FixedPercentageProvided() throws Exception {
        ChallengeFee fee1 = new ChallengeFee(1L, 1L, 1L, 500.0, false, "description", false, "fee1");
        ChallengeFee fee2 = new ChallengeFee(2L, 1L, 2L, 500.0, false, "description", false, "fee2");
        List<ChallengeFee> fees = new ArrayList<ChallengeFee>();
        fees.add(fee1);
        fees.add(fee2);

        BillingAccountFees billingAccountFees = new BillingAccountFees(true, 15.0, fees);

        try {
            unit.createBillingAccountFees(createUser("3"), billingAccountFees, 1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.createBillingAccountFees when challenge fee is not fixed but challenge fees is
     * provided.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testCreateBillingAccountFees_NotFixedFeesProvided() throws Exception {
        ChallengeFee fee1 = new ChallengeFee(1L, 1L, 1L, 500.0, false, "description", false, "fee1");
        ChallengeFee fee2 = new ChallengeFee(2L, 1L, 2L, 500.0, false, "description", false, "fee2");
        List<ChallengeFee> fees = new ArrayList<ChallengeFee>();
        fees.add(fee1);
        fees.add(fee2);

        BillingAccountFees billingAccountFees = new BillingAccountFees(false, 5.0, fees);

        try {
            unit.createBillingAccountFees(createUser("3"), billingAccountFees, 1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.createBillingAccountFees when challenge fee is not fixed but fee percentage is
     * not provided.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testCreateBillingAccountFees_NotFixedNoPercentage() throws Exception {
        BillingAccountFees billingAccountFees = new BillingAccountFees(false, null, null);

        try {
            unit.createBillingAccountFees(createUser("3"), billingAccountFees, 1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.createBillingAccountFees when billing account does not exist for given project
     * id.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testCreateBillingAccountFees_NotExistBillingAccount1() throws Exception {
        ChallengeFee fee1 = new ChallengeFee(1L, 1L, 1L, 500.0, false, "description", false, "fee1");
        ChallengeFee fee2 = new ChallengeFee(2L, 1L, 2L, 500.0, false, "description", false, "fee2");
        List<ChallengeFee> fees = new ArrayList<ChallengeFee>();
        fees.add(fee1);
        fees.add(fee2);

        BillingAccountFees billingAccountFees = new BillingAccountFees(true, null, fees);

        when(billingAccountDAO.checkBillingAccountExists(anyLong())).thenReturn(null);

        try {
            unit.createBillingAccountFees(createUser("3"), billingAccountFees, 1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_NOT_FOUND, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.createBillingAccountFees when billing account does not exist for given project
     * id.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testCreateBillingAccountFees_NotExistBillingAccount2() throws Exception {
        IdDTO expectedId = new IdDTO(0L);
        ChallengeFee fee1 = new ChallengeFee(1L, 1L, 1L, 500.0, false, "description", false, "fee1");
        ChallengeFee fee2 = new ChallengeFee(2L, 1L, 2L, 500.0, false, "description", false, "fee2");
        List<ChallengeFee> fees = new ArrayList<ChallengeFee>();
        fees.add(fee1);
        fees.add(fee2);

        BillingAccountFees billingAccountFees = new BillingAccountFees(true, null, fees);

        when(billingAccountDAO.checkBillingAccountExists(anyLong())).thenReturn(expectedId);

        try {
            unit.createBillingAccountFees(createUser("3"), billingAccountFees, 1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_NOT_FOUND, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.createBillingAccountFees when challenge fee is already created for the billing
     * account.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testCreateBillingAccountFees_ChallengeFeeAlreadyCreated() throws Exception {
        IdDTO expectedId = new IdDTO(1L);
        IdDTO expectedFeeId = new IdDTO(1L);

        ChallengeFee fee1 = new ChallengeFee(1L, 1L, 1L, 500.0, false, "description", false, "fee1");
        ChallengeFee fee2 = new ChallengeFee(2L, 1L, 2L, 500.0, true, "description", false, "fee2");
        List<ChallengeFee> fees = new ArrayList<ChallengeFee>();
        fees.add(fee1);
        fees.add(fee2);

        BillingAccountFees billingAccountFees = new BillingAccountFees(true, null, fees);

        when(billingAccountDAO.checkBillingAccountExists(anyLong())).thenReturn(expectedId);
        when(billingAccountDAO.checkChallengeFeeExists(anyLong())).thenReturn(expectedFeeId);

        try {
            unit.createBillingAccountFees(createUser("3"), billingAccountFees, 1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.createBillingAccountFees when challenge fee percentage was already created.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testCreateBillingAccountFees_FeePercentageAlreadyCreated() throws Exception {
        IdDTO expectedId = new IdDTO(1L);
        IdDTO expectedFeeId = new IdDTO(0L);

        BillingAccountFees billingAccountFees = new BillingAccountFees(false, 15.0, null);

        when(billingAccountDAO.checkBillingAccountExists(anyLong())).thenReturn(expectedId);
        when(billingAccountDAO.checkChallengeFeeExists(anyLong())).thenReturn(expectedFeeId);
        when(billingAccountDAO.getChallengeFeePercentage(anyLong())).thenReturn(new ChallengeFeePercentage());

        try {
            unit.createBillingAccountFees(createUser("3"), billingAccountFees, 1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.createBillingAccountFees when error on accessing persistence.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testCreateBillingAccountFees_ServerError() throws Exception {
        BillingAccountFees billingAccountFees = new BillingAccountFees(false, 15.0, null);

        when(billingAccountDAO.checkBillingAccountExists(anyLong())).thenThrow(new IllegalArgumentException());

        try {
            unit.createBillingAccountFees(createUser("3"), billingAccountFees, 1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.updateBillingAccountFees to update billing account fees.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testUpdateBillingAccountFees() throws Exception {
        IdDTO expectedId = new IdDTO(1L);
        ChallengeFeePercentage percentage = new ChallengeFeePercentage(1L, 1L, 5.0, false);

        ChallengeFee fee1 = new ChallengeFee(1L, 1L, 1L, 500.0, false, "description", false, "fee1");
        ChallengeFee fee2 = new ChallengeFee(0L, 1L, 2L, 500.0, false, "description", false, "fee2");
        List<ChallengeFee> fees = new ArrayList<ChallengeFee>();
        fees.add(fee1);
        fees.add(fee2);

        ChallengeType type = new ChallengeType();
        type.setChallengeTypeId(1L);
        type.setDescription("type1");
        List<ChallengeType> types = new ArrayList<ChallengeType>();
        types.add(type);

        IdSequence idSequence = mock(IdSequence.class);
        when(idSequence.getNextId()).thenReturn(1L);

        when(sequenceDAO.getIdSequence(anyString())).thenReturn(idSequence);

        when(billingAccountDAO.checkBillingAccountExists(anyLong())).thenReturn(expectedId);
        when(billingAccountDAO.getChallengeFeePercentage(anyLong())).thenReturn(percentage);
        when(billingAccountDAO.getChallengeFee(anyLong())).thenReturn(fees);
        when(billingAccountDAO.getProjectCategoriesReplatforming()).thenReturn(types);

        BillingAccountFees billingAccountFees = new BillingAccountFees(true, null, fees);

        BillingAccountFees result = unit.updateBillingAccountFees(createUser("3"), billingAccountFees, 1L);

        for (ChallengeFee fee : result.getChallengeFees()) {
            assertNotNull(fee.getUpdatedAt());
            assertNotNull(fee.getUpdatedBy());
        }

        verify(billingAccountDAO).updateChallengeFee(anyLong(), anyLong(), anyInt(), anyLong(), anyDouble(), anyLong(),
            anyString(), anyBoolean());
        verify(billingAccountDAO).updateChallengeFeePercentage(anyLong(), anyLong(), anyDouble(), anyBoolean(),
            anyLong());
        verify(billingAccountDAO).deleteChallengeFee(any(QueryParameter.class));
    }

    /**
     * Test BillingAccountManager.updateBillingAccountFees when a challenge fee does not exit for given project.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testUpdateBillingAccountFees_NotExistChallengeFee() throws Exception {
        IdDTO expectedId = new IdDTO(1L);
        ChallengeFeePercentage percentage = new ChallengeFeePercentage(1L, 1L, 5.0, true);

        ChallengeFee fee1 = new ChallengeFee(1L, 1L, 1L, 500.0, false, "description", false, "fee1");
        ChallengeFee fee2 = new ChallengeFee(0L, 1L, 2L, 500.0, false, "description", false, "fee2");
        List<ChallengeFee> fees = new ArrayList<ChallengeFee>();
        fees.add(fee1);
        fees.add(fee2);

        ChallengeType type = new ChallengeType();
        type.setChallengeTypeId(1L);
        type.setDescription("type1");
        List<ChallengeType> types = new ArrayList<ChallengeType>();
        types.add(type);

        IdSequence idSequence = mock(IdSequence.class);
        when(idSequence.getNextId()).thenReturn(1L);

        when(sequenceDAO.getIdSequence(anyString())).thenReturn(idSequence);

        when(billingAccountDAO.checkBillingAccountExists(anyLong())).thenReturn(expectedId);
        when(billingAccountDAO.getChallengeFeePercentage(anyLong())).thenReturn(percentage);
        when(billingAccountDAO.getChallengeFee(anyLong())).thenReturn(fees);
        when(billingAccountDAO.getProjectCategoriesReplatforming()).thenReturn(types);

        BillingAccountFees billingAccountFees = new BillingAccountFees(true, null, fees);

        try {
            unit.updateBillingAccountFees(createUser("3"), billingAccountFees, 1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_NOT_FOUND, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.updateBillingAccountFees when fee percentage does not exit for given project.
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testUpdateBillingAccountFees_NotExistFeePercentage() throws Exception {
        IdDTO expectedId = new IdDTO(1L);

        ChallengeFee fee1 = new ChallengeFee(1L, 1L, 1L, 500.0, false, "description", false, "fee1");
        ChallengeFee fee2 = new ChallengeFee(0L, 1L, 2L, 500.0, false, "description", false, "fee2");
        List<ChallengeFee> fees = new ArrayList<ChallengeFee>();
        fees.add(fee1);
        fees.add(fee2);

        ChallengeType type = new ChallengeType();
        type.setChallengeTypeId(1L);
        type.setDescription("type1");
        List<ChallengeType> types = new ArrayList<ChallengeType>();
        types.add(type);

        IdSequence idSequence = mock(IdSequence.class);
        when(idSequence.getNextId()).thenReturn(1L);

        when(sequenceDAO.getIdSequence(anyString())).thenReturn(idSequence);

        when(billingAccountDAO.checkBillingAccountExists(anyLong())).thenReturn(expectedId);
        when(billingAccountDAO.getChallengeFeePercentage(anyLong())).thenReturn(null);
        when(billingAccountDAO.getChallengeFee(anyLong())).thenReturn(fees);
        when(billingAccountDAO.getProjectCategoriesReplatforming()).thenReturn(types);

        BillingAccountFees billingAccountFees = new BillingAccountFees(false, 5.0, null);

        try {
            unit.updateBillingAccountFees(createUser("3"), billingAccountFees, 1L);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_NOT_FOUND, e.getStatusCode());
        }
    }

    /**
     * Test BillingAccountManager.updateBillingAccountFees when project id is not positive
     *
     * @throws Exception
     *             to junit
     */
    @Test
    public void testUpdateBillingAccountFees_NotPositiveProjectId() throws Exception {
        BillingAccountFees billingAccountFees = new BillingAccountFees(false, 5.0, null);

        try {
            unit.updateBillingAccountFees(createUser("3"), billingAccountFees, 0);
        } catch (SupplyException e) {
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getStatusCode());
        }
    }

    /**
     * Get QueryResult containing a list of billing accounts used for testing.
     *
     * @return query result
     */
    private QueryResult<List<BillingAccount>> getListQueryResult() {
        List<BillingAccount> billingAccounts = new ArrayList<>();

        billingAccounts.add(new BillingAccount(1L, "1", "Active", new Date(), new Date(), 500.0f, 1.0f, "po1",
            new PaymentTermsDTO(1L, "30 Days"), "description1", "subscription#1", 1L, 0L, 1L, false));
        billingAccounts.add(new BillingAccount(2L, "2", "Active", new Date(), new Date(), 500.0f, 1.0f, "po2",
            new PaymentTermsDTO(1L, "30 Days"), "description2", "subscription#2", 1L, 0L, 1L, false));
        QueryResult<List<BillingAccount>> expected = new QueryResult<>();
        expected.setData(billingAccounts);
        return expected;
    }
}
