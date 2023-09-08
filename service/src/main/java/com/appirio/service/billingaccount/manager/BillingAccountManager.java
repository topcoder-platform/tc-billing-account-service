/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.billingaccount.manager;

import com.appirio.service.billingaccount.api.BillingAccount;
import com.appirio.service.billingaccount.api.BillingAccountFees;
import com.appirio.service.billingaccount.api.BillingAccountUser;
import com.appirio.service.billingaccount.api.ChallengeFee;
import com.appirio.service.billingaccount.api.ChallengeFeePercentage;
import com.appirio.service.billingaccount.api.ChallengeType;
import com.appirio.service.billingaccount.api.IdDTO;
import com.appirio.service.billingaccount.api.FloatDTO;
import com.appirio.service.billingaccount.dao.BillingAccountDAO;
import com.appirio.service.billingaccount.dao.SequenceDAO;
import com.appirio.service.billingaccount.dto.TCUserDTO;
import com.appirio.service.billingaccount.api.PublisherConsumedAmount;
import com.appirio.service.billingaccount.api.PublisherSQS;
import com.appirio.supply.SupplyException;
import com.appirio.supply.dataaccess.QueryResult;
import com.appirio.supply.dataaccess.db.IdGenerator;
import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.request.FilterParameter;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.auth.AuthUser;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Manager for billing account business logic
 *
 * <p>
 * Changes in v1.1 FAST 72HRS!! - ADD APIS FOR CLIENTS AND SOME LOGIC CHANGES
 * -- Updated createBillingAccount() to pass additional parameters (description, subscriptionNumber , companyId and
 *    manual prize setting) to the billingAccountDAO.createBillingAccount().
 * -- Added validateCompanyId() to validate the input company id.
 * -- Updated the business logic to use userId instead of user handle for auditing purpose.
 * -- changed to extend from BaseResource to gain access to the helper method for converting the status to 'active' flag value.
 * -- Added userAccountIdGenerator reference to be used to generate the ids of the new user accounts.
 * </p>
 *
 * <p>
 *  Changes in v 1.2 Fast 48hrs!! Topcoder - Improvement For Billing Account Service
 *  -- Updated createBillingAccount() to support budgetAmount
 *  -- Updated createBillingAccount() and updateBillingAccount() to associate billing account to client
 *  -- Updated getBillingAccountUsers() to support limit and offset
 * </p>
 * 
 * <p>
 *  Changes in v 1.3 Topcoder - Create Challenge Fee Management APIs For Billing Accounts v1.0
 *  -- add methods to create/update/get challenge fee and challenge fee percentage for project
 * </p>
 * 
 * <p>
 * Version 1.4 - Quick 48hours! Topcoder - Update Logic For Challenge Fees Managment v1.0
 * -  update the challenge fees management logic in billing accounts
 * </p>
 * 
 * @author TCSCODER
 * @version 1.4 
 */
public class BillingAccountManager extends BaseManager {
    /**
     * The CHALLENGE_TYPE_CACHE_KEY used to cache the challenge type list
     */
    private static final String CHALLENGE_TYPE_CACHE_KEY = "CHALLENGE_TYPE_CACHE_KEY";

    /**
     * The CHALLENGE_TYPE_CACHE_EXPIRED_TIME
     */
    private static final int CHALLENGE_TYPE_CACHE_EXPIRED_TIME = 600;
    /**
     * DAO for the billing accounts
     */
    private BillingAccountDAO billingAccountDAO;

    /**
     * Id generator to create the billing account ids
     */
    private IdGenerator billingAccountIdGenerator;

    /**
     * The Id Generator to be used to generate user account ids.
     */
    private IdGenerator userAccountIdGenerator;
    
    /**
     * The sequenceDAO to generate the id from different sequence
     */
    private SequenceDAO sequenceDAO;
    
    /**
     * The cacheService to cache the data
     */
    private SimpleCacheService cacheService = new SimpleCacheService();

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(BillingAccountManager.class);

    /**
     * Add two Float values with 2 decimal precision carefully
     */
    private static Float floatAdd(Float f1, Float f2) {
		return Math.round((f1 * 100 + f2 * 100)) / 100f;
	}

    /**
     * Subtract two Float values with 2 decimal precision carefully
     */
	private static Float floatSubtract(Float f1, Float f2) {
		return Math.round((f1 * 100 - f2 * 100)) / 100f;
	}

    /**
     * Create BillingAccountManager
     *
     * @param billingAccountDAO the billingAccountDAO to use
     * @param billingAccountIdGenerator the billingAccountIdGenerator to use
     * @param userAccountIdGenerator the userAccountIdGenerator to use
     * @param sequenceDAO the sequenceDAO to use
     */
    public BillingAccountManager(BillingAccountDAO billingAccountDAO, IdGenerator billingAccountIdGenerator,
                                 IdGenerator userAccountIdGenerator,
                                 SequenceDAO sequenceDAO) {
        this.billingAccountDAO = billingAccountDAO;
        this.billingAccountIdGenerator = billingAccountIdGenerator;
        this.userAccountIdGenerator = userAccountIdGenerator;
        this.sequenceDAO = sequenceDAO;
    }

    /**
     * Search for billing accounts
     *
     * @param queryParameter
     *            sort and filter parameters
     * @return the billing accounts
     */
    public QueryResult<List<BillingAccount>> searchBillingAccounts(QueryParameter queryParameter) {
        return billingAccountDAO.searchBillingAccounts(queryParameter);
    }

    /**
     * Search for billing accounts limited by the privileges of the logged in user
     *
     * @param userId
     *            the user id of the logged in user
     * @param queryParameter
     *            sort and filter parameters
     * @return the billing accounts
     */
    public QueryResult<List<BillingAccount>> searchMyBillingAccounts(Long userId, QueryParameter queryParameter) {
        return billingAccountDAO.searchMyBillingAccounts(userId, queryParameter);
    }

    /**
     * Create a billing account
     *
     * @param user
     *            the currently logged in user
     * @param billingAccount
     *            the billing account to create
     * @return the created billing account
     */
    public QueryResult<List<BillingAccount>> createBillingAccount(AuthUser user, BillingAccount billingAccount)
            throws SupplyException {

        // validate the specified status and get the corresponding active flag
        Long activeFlag = getActiveFlagFromStatusName("billing account", billingAccount.getStatus());

        // validate the provided companyId
        validateCompanyId(billingAccount.getCompanyId());

        // validate the provided clientId
        validateClientId(billingAccount.getClientId());

        Long id = billingAccountIdGenerator.getNextId();
        logger.debug("Next ID: " + id);
        billingAccountDAO.createBillingAccount(id, billingAccount.getBudgetAmount(),
                billingAccount.getName(), billingAccount.getPaymentTerms().getId(),
                billingAccount.getStartDate(), billingAccount.getEndDate(), activeFlag, user.getUserId().toString(),
                billingAccount.getSalesTax(), billingAccount.getPoNumber(), billingAccount.getDescription(),
                billingAccount.getSubscriptionNumber(), billingAccount.getCompanyId(),
                billingAccount.getManualPrizeSetting(), billingAccount.getClientId(), billingAccount.getBillable() != null ? billingAccount.getBillable() : false);

                // map billing account to client
                billingAccountDAO.addBillingAccountToClient(id, billingAccount.getClientId(), user.getUserId().getId());

        return billingAccountDAO.getBillingAccount(id);
    }

    /**
     * Get a billing account by id
     *
     * @param billingAccountId
     *            the id
     * @return the billing account with the given id
     */
    public QueryResult<List<BillingAccount>> getBillingAccount(Long billingAccountId) {
        return billingAccountDAO.getBillingAccount(billingAccountId);
    }

    /**
     * Update a billing account
     *
     * @param user
     *            the currently logged in user
     * @param billingAccount
     *            the billing account to update
     * @return the updated billing account
     */
    public QueryResult<List<BillingAccount>> updateBillingAccount(AuthUser user, BillingAccount billingAccount)
            throws SupplyException {

        // Validate the billing account status and get the corresponding active flag value.
        Long activeFlag = getActiveFlagFromStatusName("billing account", billingAccount.getStatus());

        // validate the provided companyId
        validateCompanyId(billingAccount.getCompanyId());

        // validate the provided clientId
        validateClientId(billingAccount.getClientId());

        billingAccountDAO.updateBillingAccount(billingAccount.getId(), billingAccount.getBudgetAmount(),
                billingAccount.getName(), billingAccount.getPaymentTerms().getId(), billingAccount.getStartDate(),
                billingAccount.getEndDate(), activeFlag, user.getUserId().getId(), billingAccount.getSalesTax(),
                billingAccount.getPoNumber(), billingAccount.getDescription(), billingAccount.getSubscriptionNumber(),
                billingAccount.getCompanyId(), billingAccount.getManualPrizeSetting(), billingAccount.getClientId(), billingAccount.getBillable() != null ? billingAccount.getBillable() : false);

                // remove the old mapping record between client and billing account
                billingAccountDAO.removeBillingAccountFromClient(billingAccount.getId());

                // add new mapping record between client and billing account
                billingAccountDAO.addBillingAccountToClient(billingAccount.getId(), billingAccount.getClientId(), user.getUserId().getId());

        return billingAccountDAO.getBillingAccount(billingAccount.getId());
    }

    /**
     * Get users for given billing account
     *
     * @param billingAccountId
     *            the billing account id
     * @param queryParameter
     *            sort and filter parameters
     * @return return the users associated with the billing account
     */
    public QueryResult<List<BillingAccountUser>> getBillingAccountUsers(Long billingAccountId, QueryParameter queryParameter) {
        return billingAccountDAO.getBillingAccountUsers(billingAccountId, queryParameter);
    }

    /**
     * Add user to a billing account
     *
     * @param billingAccountId
     *            the billing account id
     * @param userId
     *            the user id
     * @param user
     *            the user (for audit purposes only)
     */
    public QueryResult<List<BillingAccount>> addUserToBillingAccount(AuthUser user, Long billingAccountId, Long userId)
            throws SupplyException {

        // Check if the user id exists
        TCUserDTO tcUser = billingAccountDAO.getTCUserById(userId);
        if (tcUser == null) {
            throw new SupplyException("User with the following id doesn't exist " + userId, 404);
        }

        // Check if the user has a user account.
        IdDTO id = billingAccountDAO.checkUserExists(tcUser.getHandle());
        Long userAccountId;

        if (id == null) { // The user does not have a user account, then we should create one.
            userAccountId = userAccountIdGenerator.getNextId();
            billingAccountDAO.createUserAccount(userAccountId, tcUser.getHandle(), user.getUserId().toString());
        } else { // The user has a user account, we need to check if he already belongs to the billing account.
            userAccountId = id.getId();
            IdDTO projectManagerId = billingAccountDAO.checkUserBelongsToBillingAccount(billingAccountId,
                    userAccountId);
            if (projectManagerId != null) {
                throw new SupplyException("The user { 'id': " + userId + ", 'handle' : " + tcUser.getHandle() + "} "
                        + "already belongs to this billing account", 400);
            }
        }
        billingAccountDAO.addUserToBillingAccount(billingAccountId, userAccountId, user.getUserId().toString());
        return billingAccountDAO.getBillingAccount(billingAccountId);
    }

    /**
     * Remove user from billing account
     *
     * @param billingAccountId
     *            the billing account id
     * @param userId
     *            the user id
     */
    public void removeUserFromBillingAccount(Long billingAccountId, Long userId) throws SupplyException {
        // Check if the user id exists
        TCUserDTO tcUser = billingAccountDAO.getTCUserById(userId);
        if (tcUser == null) {
            throw new SupplyException("User with the following id doesn't exist " + userId, 404);
        }

        //
        IdDTO id = billingAccountDAO.checkUserExists(tcUser.getHandle());
        Long userAccountId;

        if (id == null) { // The user does not even have a user account
            throw new SupplyException(String.format("The user {'id':'%s', 'handle':'/%s'} does not even have "
                    + "a user account.", userId, tcUser.getHandle()));
        } else { // The user has a user account, we need to check if he already belongs to the billing account.
            userAccountId = id.getId();
            IdDTO projectManagerId = billingAccountDAO.checkUserBelongsToBillingAccount(billingAccountId,
                    userAccountId);
            if (projectManagerId == null) {
                throw new SupplyException("The user { 'id': " + userId + ", 'handle' : " + tcUser.getHandle() + "} "
                        + "does not belong to this billing account", 400);
            }
        }

        // If all checks succeed, then remove the user from the billing account
        billingAccountDAO.removeUserFromBillingAccount(billingAccountId, userAccountId);
    }

    /**
     * Create billing account fees
     *
     * @param user the user to use
     * @param billingAccountFees the billingAccountFees to use
     * @param projectId the projectId to use
     * @throws SupplyException if any error occurs
     * @return the BillingAccountFees result
     */
    public BillingAccountFees createBillingAccountFees(AuthUser user, BillingAccountFees billingAccountFees, long projectId) throws SupplyException {
        if (projectId <= 0) {
            throw new SupplyException("The billing account id must be positive", 400);
        }
        this.validateAccountChallengeFees(billingAccountFees);
        
        long userId = Long.parseLong(user.getUserId().getId());
        try {
            IdDTO idDto = this.billingAccountDAO.checkBillingAccountExists(projectId);
            if (idDto == null || idDto.getId() <= 0) {
                throw new SupplyException("The billing account does not exist with the id:" + projectId, 404);
            }
            
            if (billingAccountFees.isChallengeFeeFixed()) {
                idDto = this.billingAccountDAO.checkChallengeFeeExists(projectId);
                if (idDto.getId() > 0) {
                    throw new SupplyException("The challenge fee was created for the billing account", 400);
                }
                
                this.setChallengeTypeForChallengeFee(billingAccountFees.getChallengeFees());
                for (ChallengeFee fee : billingAccountFees.getChallengeFees()) {
                    long id = this.sequenceDAO.getIdSequence("project_contest_fee_seq").getNextId();
                    fee.setId(id);
                    fee.setProjectId(projectId);
                    Date date = new Date();
                    fee.setCreatedAt(date);
                    fee.setUpdatedAt(date);
                    fee.setCreatedBy(user.getUserId().getId());
                    fee.setUpdatedBy(user.getUserId().getId());
                    this.billingAccountDAO.createChallengeFee(id, fee.getProjectId(), fee.isStudio() ? 1 : 0, fee.getChallengeTypeId(), fee.getChallengeFee(),
                            userId, fee.getName(), fee.isDeleted());
                }
            }
            
            // create the percentage fee
            ChallengeFeePercentage percentage = this.billingAccountDAO.getChallengeFeePercentage(projectId);
            if (percentage != null) {
                throw new SupplyException("The challenge fee was created for the billing account", 400);
            }
            percentage = new ChallengeFeePercentage();
            percentage.setId(this.sequenceDAO.getIdSequence("project_contest_fee_percentage_seq").getNextId());
            percentage.setProjectId(projectId);        
            percentage.setActive(!billingAccountFees.isChallengeFeeFixed());
            percentage.setChallengeFeePercentage(billingAccountFees.getChallengeFeePercentage());
            this.billingAccountDAO.createChallengeFeePercentage(percentage.getId(), percentage.getProjectId(), 
                    percentage.getChallengeFeePercentage(), percentage.isActive(), userId);
            
        } catch (SupplyException se) { 
            throw se;
        } catch (Exception exp) {
            SupplyException se = new SupplyException("Internal server error", exp);
            se.setStatusCode(500);
            throw se;
        }
        return billingAccountFees;
    }
    
    /**
     * Update billing account fees
     *
     * @param user the user to use
     * @param billingAccountFees the billingAccountFees to use
     * @param projectId the projectId to use
     * @throws SupplyException if any error occurs
     * @return the BillingAccountFees result
     */
    public BillingAccountFees updateBillingAccountFees(AuthUser user, BillingAccountFees billingAccountFees, long projectId) throws SupplyException {
        if (projectId <= 0) {
            throw new SupplyException("The billing account id must be positive", 400);
        }
        this.validateAccountChallengeFees(billingAccountFees);
        
        long userId = Long.parseLong(user.getUserId().getId());
        try {
            BillingAccountFees exists = this.getBillingAccountFees(projectId);
            List<ChallengeFee> oldChallengeFees = exists.getChallengeFees();
            if (oldChallengeFees == null) {
                oldChallengeFees = new ArrayList<>();
            }
           
            if (billingAccountFees.isChallengeFeeFixed()) {
                List<ChallengeFee> feesToUpdate = new ArrayList<>();
                for (ChallengeFee fee : billingAccountFees.getChallengeFees()) {
                    if (fee.getId() > 0) {
                        ChallengeFee hit = null;
                        for (ChallengeFee tp : oldChallengeFees) {
                            if (fee.getId() == tp.getId()) {
                                hit = tp;
                                break;
                            }
                        }
                        if (hit == null) {
                            throw new SupplyException("The challenge fee does not exists for the project(" + projectId + ") with the id:" + fee.getId(), 404);
                        } else {
                            feesToUpdate.add(hit);
                        }
                    }
                }

                List<ChallengeFee> toDelete = new ArrayList<>(oldChallengeFees);
                toDelete.removeAll(feesToUpdate);
                this.deleteChallengeFees(toDelete);
                
                this.setChallengeTypeForChallengeFee(billingAccountFees.getChallengeFees());
                for (ChallengeFee fee : billingAccountFees.getChallengeFees()) {
                    fee.setProjectId(projectId);
                    Date date = new Date();
                    fee.setUpdatedAt(date);
                    fee.setUpdatedBy(user.getUserId().getId());
                    if (fee.getId() <= 0) {
                        long id = this.sequenceDAO.getIdSequence("project_contest_fee_seq").getNextId();
                        fee.setId(id);
                        fee.setCreatedBy(user.getUserId().getId());
                        fee.setUpdatedBy(user.getUserId().getId());
                        this.billingAccountDAO.createChallengeFee(id, fee.getProjectId(), fee.isStudio() ? 1 : 0, 
                                fee.getChallengeTypeId(), fee.getChallengeFee(), userId, fee.getName(), fee.isDeleted());
                    } else {
                        this.billingAccountDAO.updateChallengeFee(fee.getId(), fee.getProjectId(), fee.isStudio() ? 1 : 0, 
                                fee.getChallengeTypeId(), fee.getChallengeFee(), userId, fee.getName(), fee.isDeleted());
                    }
                }
            } else {
                this.deleteChallengeFees(oldChallengeFees);
            }
            
            // update the percentage fee
            ChallengeFeePercentage percentage = this.billingAccountDAO.getChallengeFeePercentage(projectId);
            if (percentage == null) {
                throw new SupplyException("The percentage fee does not exist for the project(" + projectId + ")", 404);
            }

            percentage.setActive(!billingAccountFees.isChallengeFeeFixed());
            percentage.setChallengeFeePercentage(billingAccountFees.getChallengeFeePercentage());

            this.billingAccountDAO.updateChallengeFeePercentage(percentage.getId(), percentage.getProjectId(), percentage.getChallengeFeePercentage(),
                    percentage.isActive(), userId);
            
        } catch (SupplyException se) { 
            throw se;
        } catch (Exception exp) {
            SupplyException se = new SupplyException("Internal server error", exp);
            se.setStatusCode(500);
            throw se;
        }
        return billingAccountFees;
    }
    
    /**
     * Get billing account fees
     *
     * @param projectId the projectId to use
     * @throws SupplyException if any error occurs
     * @return the BillingAccountFees result
     */
    public BillingAccountFees getBillingAccountFees(long projectId) throws SupplyException {
        if (projectId <= 0) {
            throw new SupplyException("The billing account id must be positive", 400);
        }
        try {
            IdDTO idDto = this.billingAccountDAO.checkBillingAccountExists(projectId);
            if (idDto == null || idDto.getId() <= 0) {
                throw new SupplyException("The billing account does not exist with the id:" + projectId, 404);
            }
            
            BillingAccountFees bf = new BillingAccountFees();
            ChallengeFeePercentage percentage = this.billingAccountDAO.getChallengeFeePercentage(projectId);
            if (percentage != null && percentage.isActive()) {
                bf.setChallengeFeeFixed(false);
                bf.setChallengeFeePercentage(percentage.getChallengeFeePercentage());
            } else {
                List<ChallengeFee> fees = this.billingAccountDAO.getChallengeFee(projectId);
                if (fees == null || fees.size() == 0) {
                    throw new SupplyException("The challenge fee was not created for the billing account", 404);
                }
                List<ChallengeFee> notDeletedFees = new ArrayList<>();
                for (ChallengeFee fee : fees) {
                    if (!fee.isDeleted()) {
                        notDeletedFees.add(fee);
                    }
                }
                if (notDeletedFees.isEmpty()) {
                    throw new SupplyException("No active challenge fee found for the billing account", 404);
                }
                
                bf.setChallengeFees(notDeletedFees);
                this.setChallengeTypeForChallengeFee(bf.getChallengeFees());
                
                bf.setChallengeFeeFixed(true);
            }
            
            return bf;
        } catch (SupplyException se) { 
            throw se;
        } catch (Exception exp) {
            SupplyException se = new SupplyException("Internal server error", exp);
            se.setStatusCode(500);
            throw se;
        }
    }
    
    /**
     * Set challenge type for challenge fee
     *
     * @param challengeFees the challengeFees to use
     */
    private void setChallengeTypeForChallengeFee(List<ChallengeFee> challengeFees) {
        if (challengeFees == null) {
            return;
        }
        List<ChallengeType> types = this.getChallengeTypes();
        for (ChallengeFee fee : challengeFees) { 
            for (ChallengeType type : types) { 
                if (type.getChallengeTypeId() == fee.getChallengeTypeId()) {
                    fee.setChallengeTypeDescription(type.getDescription());
                    fee.setStudio(type.isStudio());
                }
            }
        }
    }
    
    /**
     * Validate account challenge fees
     *
     * @param billingAccountFees the billingAccountFees to validate
     * @throws SupplyException if any error occurs
     */
    private void validateAccountChallengeFees(BillingAccountFees billingAccountFees) throws SupplyException {
        if (billingAccountFees.isChallengeFeeFixed()) {
            if (billingAccountFees.getChallengeFees() == null || billingAccountFees.getChallengeFees().size() == 0) {
                throw new SupplyException("The challenge fee data should be provided", 400);
            }
            if (billingAccountFees.getChallengeFeePercentage() != null) {
                throw new SupplyException("The challenge fee percentage should not be provided", 400);
            }
        } else {
            if (billingAccountFees.getChallengeFees() != null) {
                throw new SupplyException("The challenge fee data should not be provided", 400);
            }
            if (billingAccountFees.getChallengeFeePercentage() == null) {
                throw new SupplyException("The challenge fee percentage should be provided", 400);
            }
        }
    }
    
    /**
     * Delete challenge fees
     *
     * @param challengeFees the challengeFees to use
     * @throws SupplyException if any error occurs
     */
    private void deleteChallengeFees(List<ChallengeFee> challengeFees) throws SupplyException {
        if (challengeFees == null || challengeFees.size() == 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (ChallengeFee fee : challengeFees) {
            sb.append(fee.getId()).append(",");
        }
        FilterParameter filter = new FilterParameter("projectContestFeeIds=in(" + sb.substring(0, sb.length() - 1) + ")");
        QueryParameter queryParameter = new QueryParameter(new FieldSelector());
        queryParameter.setFilter(filter);
        
        this.billingAccountDAO.deleteChallengeFee(queryParameter);
    }
    
    /**
     * Checks whether company identified by the given id exists in the persistence.
     *
     * @param companyId
     *            The id of the company to check if it exists
     */
    private void validateCompanyId(Long companyId) {
        IdDTO id = billingAccountDAO.checkCompanyExists(companyId);
        if (id == null) {
            throw new IllegalArgumentException(String.format(
                    "The company identified by id = %s, " + "does not exist.", companyId));
        }
    }

    /**
     * Checks whether client identified by the given id exists in the persistence.
     *
     * @param clientId
     *            The id of the client to check if it exists
     */
    private void validateClientId(Long clientId) {
        IdDTO id = billingAccountDAO.checkClientExists(clientId);
        if (id == null) {
            throw new IllegalArgumentException(String.format(
                    "The client identified by id = %s, " + "does not exist.", clientId));
        }
    }
    
    /**
     * Get challenge types
     *
     * @return the List<ChallengeType> result
     */
    private List<ChallengeType> getChallengeTypes() {
        List<ChallengeType> types = (List<ChallengeType>) this.cacheService.get(CHALLENGE_TYPE_CACHE_KEY);
        if (types == null) {
            types = this.billingAccountDAO.getProjectCategoriesReplatforming();
            // cache for ten minutes
            this.cacheService.put(CHALLENGE_TYPE_CACHE_KEY, types, CHALLENGE_TYPE_CACHE_EXPIRED_TIME);
        }
        
        return types;
    }

    /**
     * Update locked amount for a Challenge of a BillingAccount"
     *
     * @return the updated lock Amount
     */
    public Float lockAmount(Long billingAccountId, String challengeId, Float requestedLockAmount)  throws SupplyException{
        //Get the Billing Account
        List<BillingAccount> originals = getBillingAccount(billingAccountId).getData();
        if (originals.size() == 0) {
            throw new SupplyException("Couldn't find billing account with id " + billingAccountId, 404);
        }

        BillingAccount billingAccount = originals.get(0);

        //Get the Total budget available for the Billing account
        Float budgetAmount = billingAccount.getBudgetAmount();
        budgetAmount = budgetAmount == null ? 0 : budgetAmount;

        IdDTO idDto = this.billingAccountDAO.countChallengeIdEntries(billingAccountId, challengeId);
        Long countChallengeIdEntries = idDto == null ? 0 : idDto.getId();

        FloatDTO floatDto = this.billingAccountDAO.getSumLockedConsumedAmount(billingAccountId, challengeId);
        Float sumLockedConsumed = floatDto == null || floatDto.getFloatValue() == null ? 0 : floatDto.getFloatValue();
        if(floatAdd(requestedLockAmount, sumLockedConsumed) > budgetAmount)
        {
            throw new SupplyException("Insufficient Budget amount ("+budgetAmount+") for Billing Account:" + billingAccountId+
                                      ". Requested lock amount:"+requestedLockAmount+". Sum of all locked and consumed amount:"+sumLockedConsumed, 404);
        }

        //Find if there ia an existing entry for the Challenge: Update if exists; Insert if does not exists
        if(countChallengeIdEntries == 0 )
        {
            //Insert and entry for a challaenge and billing account in table: project_challenge_budget
            logger.debug("Creating new entry for Challenge:"+challengeId+" in Billing Account:"+billingAccountId+" with Consumed amount: 0 and lockedAmount as "+requestedLockAmount);
            this.billingAccountDAO.createLockedAmount(billingAccountId, challengeId, requestedLockAmount, 0);
        }
        else if(countChallengeIdEntries == 1 )
        {
            //Update the entry for a challenge and billing account in table: project_challenge_budget
            //TODO: We are setting consumedAmount to 0 ie overwriting existing value. Revisit: If it shows wiered behavior.
            logger.debug("Updating entry for Challenge:"+challengeId+" in Billing Account:"+billingAccountId+" with Consumed amount: 0 and lockedAmount as "+requestedLockAmount);
            this.billingAccountDAO.updateConsumedAmountForChallengeid(billingAccountId, challengeId, requestedLockAmount, 0);
        }
        else
        {
            //Exception
            logger.debug("Multiple entries are found for Challenge:"+challengeId+" in Billing Account:"+billingAccountId);
            throw new SupplyException("Multiple entries are found for Challenge:"+challengeId+" in Billing Account:"+billingAccountId, 404);
        }
        return requestedLockAmount;
    }
    /**
     * Update consumed amount for a BillingAccount
     *
     * @return the updated consumed Amount
     */
    public Float consumeAmount(Long billingAccountId, String challengeId, Float requestedConsumeAmount, Float markup)  throws SupplyException{
        //Get the Billing Account
        List<BillingAccount> originals = getBillingAccount(billingAccountId).getData();
        if (originals.size() == 0) {
            throw new SupplyException("Couldn't find billing account with id " + billingAccountId, 404);
        }

        BillingAccount billingAccount = originals.get(0);

        //Get the Total budget available for the Billing account
        Float budgetAmount = billingAccount.getBudgetAmount();
        budgetAmount = budgetAmount == null ? 0 : budgetAmount;

        IdDTO idDto = this.billingAccountDAO.countChallengeIdEntries(billingAccountId, challengeId);
        Long countChallengeIdEntries = idDto == null ? 0 : idDto.getId();

        //The locked amount may not be actually used. On the other hand, consumed amount is the actually used amount.
        //So, when a challenge completes and call consume api, we just check the actually amount, kinda of 
        //loose the condition, and let that challenge finishes without problem.
        FloatDTO floatDto = this.billingAccountDAO.getSumConsumedAmount(billingAccountId, challengeId);
        Float sumConsumed = floatDto == null || floatDto.getFloatValue() == null ? 0 : floatDto.getFloatValue();
        if(floatAdd(requestedConsumeAmount, sumConsumed) > budgetAmount)
        {
            throw new SupplyException("Insufficient Budget amount ("+budgetAmount+") for Billing Account:" + billingAccountId+
                                      ". Requested consume amount:"+requestedConsumeAmount+". Sum of all consumed amount:"+sumConsumed, 404);
        }

        //Find if there ia an existing entry for the Challenge: Update if exists; Insert if does not exists
        if(countChallengeIdEntries == 0 )
        {
            //Insert and entry for a challaenge and billing account in table: project_challenge_budget
            logger.debug("Creating new entry for Challenge:"+challengeId+" in Billing Account:"+billingAccountId+" with Consumed amount:"+requestedConsumeAmount+" and lockedAmount as 0.");
            this.billingAccountDAO.createLockedAmount(billingAccountId, challengeId, 0, requestedConsumeAmount);
        }
        else if(countChallengeIdEntries == 1 )
        {
            //Update the entry for a challenge and billing account in table: project_challenge_budget
            logger.debug("Updating entry for Challenge:"+challengeId+" in Billing Account:"+billingAccountId+" with Consumed amount:"+requestedConsumeAmount+" and lockedAmount as 0.");
            this.billingAccountDAO.updateConsumedAmountForChallengeid(billingAccountId, challengeId, 0, requestedConsumeAmount);
        }
        else
        {
            //Exception
            logger.debug("Multiple entries are found for Challenge:"+challengeId+" in Billing Account:"+billingAccountId);
            throw new SupplyException("Multiple entries are found for Challenge:"+challengeId+" in Billing Account:"+billingAccountId, 404);
        }

        JSONObject json = new JSONObject();
        json.put("billingAccountId", billingAccountId);
        json.put("actualSpent", requestedConsumeAmount);
        json.put("challengeId", challengeId);
        json.put("markup", markup);
        /**
        * Publish message JSON format:
        * {
        * "billingAccountId": id,
        * "actualSpent": 200,
        * "challengeId": uuid,
        * "markup": 200,
        * }
        */
        PublisherConsumedAmount sqsPublisher = new PublisherSQS();
        sqsPublisher.publish(json.toString());

        return requestedConsumeAmount;
    }
}

