/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.billingaccount.resources;

import com.appirio.service.billingaccount.api.BillingAccount;
import com.appirio.service.billingaccount.api.BillingAccountFees;
import com.appirio.service.billingaccount.api.BillingAccountUpdatedDTO;
import com.appirio.service.billingaccount.api.BillingAccountUser;
import com.appirio.service.billingaccount.api.UserIdDTO;
import com.appirio.service.billingaccount.manager.BillingAccountManager;
import com.appirio.service.supply.resources.MetadataApiResponseFactory;
import com.appirio.supply.ErrorHandler;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.request.PostPutRequest;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.request.annotation.APIQueryParam;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.appirio.tech.core.api.v3.response.ApiResponseFactory;
import com.appirio.tech.core.auth.AuthUser;
import io.dropwizard.auth.Auth;
import io.dropwizard.jersey.PATCH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import java.util.Arrays;
import java.util.List;

/**
 * REST resource endpoint for billing accounts.
 *
 * <p>
 * Changes in v1.1 FAST 72HRS!! - ADD APIS FOR CLIENTS AND SOME LOGIC CHANGES
 * -- Moved the checkAdmin() method to the BaseResource class and make this class extend from it.
 * -- Changed the API paths from billingAccounts to billing-accounts.
 * </p>
 *
 * <p>
 *  Changes in v 1.2 Fast 48hrs!! Topcoder - Improvement For Billing Account Service
 *  -- Add offset and limit parameters for searchMyBillingAccounts() and getBillingAccountUsers()
 * </p>
 *
 * <p>
 *  Changes in v 1.3 Topcoder - Create Challenge Fee Management APIs For Billing Accounts v1.0
 *  -- add three endpoints to create/update/get billing account fees
 * </p>
 * @author TCSCODER, TCSCODER.
 * @version 1.3
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BillingAccountResource extends BaseResource {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(BillingAccountResource.class);

    /**
     * billing account manager
     */
    private BillingAccountManager billingAccountManager;

    /**
     * Constructor to initialize billing account manager
     *
     * @param billingAccountManager
     *            manager for billing accounts
     */
    public BillingAccountResource(BillingAccountManager billingAccountManager) {
        this.billingAccountManager = billingAccountManager;
    }

    /**
     * Search for billing accounts.
     *
     * @param user
     *            the currently logged in user
     * @param queryParameter
     *            the query parameters
     * @param sort
     *            the sort column
     * @return the api response
     */
    @GET
    @Path("billing-accounts")
    public ApiResponse searchBillingAccounts(@Auth AuthUser user,
            @APIQueryParam(repClass = BillingAccount.class) QueryParameter queryParameter,
            @QueryParam("sort") String sort) {
        try {
            checkAdmin(user);
            prepareParameters(queryParameter, sort);
            return MetadataApiResponseFactory.createResponse(billingAccountManager
                    .searchBillingAccounts(queryParameter));
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }

    /**
     * Create a new billing account.
     *
     * @param user
     *            the currently logged in user
     * @param request
     *            the billing account to create
     * @return the api response
     */
    @POST
    @Path("billing-accounts")
    public ApiResponse createBillingAccount(@Auth AuthUser user, @Valid PostPutRequest<BillingAccount> request) {
        try {
            checkAdmin(user);
            String method = request.getMethod();
            checkMethod(method);
            if (method != null && Arrays.asList("put", "patch").contains(method.toLowerCase())) {
                return MetadataApiResponseFactory
                        .createResponse(billingAccountManager.updateBillingAccount(user, request.getParam()));
            }
            return MetadataApiResponseFactory
                    .createResponse(billingAccountManager.createBillingAccount(user, request.getParam()));

        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }

    /**
     * Get a billing account by id.
     *
     * @param user
     *            the currently logged in user
     * @param billingAccountId
     *            the billing account id
     * @return the api response
     */
    @GET
    @Path("billing-accounts/{billingAccountId}")
    public ApiResponse getBillingAccountsById(@Auth AuthUser user, @PathParam("billingAccountId") Long billingAccountId) {
        try {
            checkAdmin(user);
            List<BillingAccount> response = getBillingAccounts(billingAccountId);
            return ApiResponseFactory.createResponse(response.get(0));
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }

    /**
     * Update a billing account.
     *
     * @param user
     *            the currently logged in user
     * @param billingAccountId
     *            the billing account id
     * @param request
     *            the billing account to update
     * @return the api response
     */
    @PATCH
    @Path("billing-accounts/{billingAccountId}")
    public ApiResponse updateBillingAccount(@Auth AuthUser user, @PathParam("billingAccountId") Long billingAccountId,
            @Valid PostPutRequest<BillingAccount> request) {
        try {
            checkAdmin(user);
            List<BillingAccount> originals = getBillingAccounts(billingAccountId);
            BillingAccount original = originals.get(0);
            // make sure to update the correct billing account
            request.getParam().setId(billingAccountId);
            BillingAccount updated = billingAccountManager.updateBillingAccount(user, request.getParam()).getData().get(0);
            BillingAccountUpdatedDTO response = new BillingAccountUpdatedDTO(original, updated);
            return ApiResponseFactory.createResponse(response);
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }

    /**
     * Get users for a billing account.
     *
     * @param user
     *            the currently logged in user
     * @param billingAccountId
     *            the billing account id
     * @return the api response
     */
    @GET
    @Path("billing-accounts/{billingAccountId}/users")
    public ApiResponse getBillingAccountUsers(@Auth AuthUser user,
        @PathParam("billingAccountId") Long billingAccountId,
        @APIQueryParam(repClass = BillingAccountUser.class) QueryParameter queryParameter,
        @QueryParam("sort") String sort) {
        try {
            checkAdmin(user);
            prepareParameters(queryParameter, sort);
            getBillingAccounts(billingAccountId);
            return MetadataApiResponseFactory.createResponse(billingAccountManager
                    .getBillingAccountUsers(billingAccountId, queryParameter));
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }

    /**
     * Add user to a billing account.
     *
     * @param user
     *            the currently logged in user
     * @param billingAccountId
     *            the billing account id
     * @param request
     *            the user id
     * @return the api response
     */
    @POST
    @Path("billing-accounts/{billingAccountId}/users")
    public ApiResponse addUserToBillingAccount(@Auth AuthUser user,
            @PathParam("billingAccountId") Long billingAccountId, @Valid PostPutRequest<UserIdDTO> request) {
        try {
            checkAdmin(user);
            getBillingAccounts(billingAccountId);
            return MetadataApiResponseFactory.createResponse(billingAccountManager.addUserToBillingAccount(user,
                    billingAccountId, request.getParam().getUserId()));
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }
    
    /**
     * Create billing account fees
     *
     * @param user the user to use
     * @param billingAccountId the billingAccountId to use
     * @param request the request to use
     * @param challengeFeeFixed the challengeFeeFixed to use
     * @param challengeFeePercentage the challengeFeePercentage to use
     * @param projectId the projectId to use
     * @return the ApiResponse result
     */
    @POST
    @Path("billing-accounts/{billingAccountId}/fees")
    public ApiResponse createBillingAccountFees(@Auth AuthUser user,
            @PathParam("billingAccountId") Long billingAccountId, @Valid PostPutRequest<BillingAccountFees> request) {
        try {
            checkAdmin(user);
            BillingAccountFees fees = this.billingAccountManager.createBillingAccountFees(
                    user, request.getParam(), billingAccountId);
            return MetadataApiResponseFactory.createResponse(fees);
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }
    
    /**
     * Update billing account fees
     *
     * @param user the user to use
     * @param billingAccountId the billingAccountId to use
     * @param request the request to use
     * @param projectId the projectId to use
     * @return the ApiResponse result
     */
    @PUT
    @Path("billing-accounts/{billingAccountId}/fees")
    public ApiResponse updateBillingAccountFees(@Auth AuthUser user,
            @PathParam("billingAccountId") Long billingAccountId, @Valid PostPutRequest<BillingAccountFees> request) {
        try {
            checkAdmin(user);
            BillingAccountFees fees = this.billingAccountManager.updateBillingAccountFees(
                    user, request.getParam(), billingAccountId);
            return MetadataApiResponseFactory.createResponse(fees);
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }
    
    /**
     * Get billing account fees
     *
     * @param user the user to use
     * @param billingAccountId the billingAccountId to use
     * @return the ApiResponse result
     */
    @GET
    @Path("billing-accounts/{billingAccountId}/fees")
    public ApiResponse getBillingAccountFees(@Auth AuthUser user, @PathParam("billingAccountId") Long billingAccountId) {
        try {
            checkAdmin(user);
            BillingAccountFees fees = this.billingAccountManager.getBillingAccountFees(user, billingAccountId);
            return MetadataApiResponseFactory.createResponse(fees);
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }

    /**
     * Remove user from a billing account.
     *
     * @param user
     *            the currently logged in user
     * @param billingAccountId
     *            the billing account id
     * @param userId
     *            the user id
     * @return the api response
     */
    @DELETE
    @Path("billing-accounts/{billingAccountId}/users/{userId}")
    public ApiResponse removeUserFromBillingAccount(@Auth AuthUser user,
            @PathParam("billingAccountId") Long billingAccountId, @PathParam("userId") Long userId) {
        try {
            checkAdmin(user);
            getBillingAccounts(billingAccountId);
            billingAccountManager.removeUserFromBillingAccount(billingAccountId, userId);
            ApiResponse result = new ApiResponse();
            result.setResult(true, 204, null);
            return result;
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }

    /**
     * Search for billing accounts restricted to the logged in user.
     *
     * @param user
     *            the currently logged in user
     * @param queryParameter
     *            the query parameters
     * @param sort
     *            the sort column
     * @return the api response
     */
    @GET
    @Path("me/billing-accounts")
    public ApiResponse searchMyBillingAccounts(@Auth AuthUser user,
            @APIQueryParam(repClass = BillingAccount.class) QueryParameter queryParameter,
            @QueryParam("sort") String sort) {
        try {
            prepareParameters(queryParameter, sort);
            return MetadataApiResponseFactory.createResponse(billingAccountManager.searchMyBillingAccounts(
                    Long.parseLong(user.getUserId().getId()), queryParameter));
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }

    /**
     * Get billing account by id. Can either be used to verify that an account exists or to get the original billing
     * account.
     *
     * @param billingAccountId
     *            the billing account id
     */
    private List<BillingAccount> getBillingAccounts(Long billingAccountId) throws SupplyException {
        List<BillingAccount> originals = billingAccountManager.getBillingAccount(billingAccountId).getData();
        if (originals.size() == 0) {
            throw new SupplyException("Couldn't find billing account with id " + billingAccountId, 404);
        }
        return originals;
    }
}
