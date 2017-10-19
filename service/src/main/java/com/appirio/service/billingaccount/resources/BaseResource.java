/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.billingaccount.resources;

import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.request.OrderByQuery;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.auth.AuthUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * This is the base class of all resources defined in this service. It provides a helper method to check whether the
 * authenticated user is an administrator.
 *
 * <p>
 *  Changes in v 1.1 Fast 48hrs!! Topcoder - Improvement For Billing Account Service
 *  -- Pulled up prepareParameters() from BillingAccountResource as filtering now is also supported by ClientResource
 * </p>
 *
 * @author TCSCODER.
 * @version 1.0
 */
public class BaseResource {

    /**
     * The constant to hold the startDate String value.
     */
    static final String START_DATE = "startDate";

    /**
     * The constant to hold the endDate String value.
     */
    static final String END_DATE = "endDate";

    /**
     * Date formatter to parse the date filters
     */
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

    /**
     * Checks if the user is an administrator.
     *
     * @param user
     *            The user to check if he is an administrator.
     */
    protected void checkAdmin(AuthUser user) throws SupplyException {
        if (!user.hasRole("administrator")) {
            throw new SupplyException("Only administrators can access this resource.", 403);
        }
    }

    /**
     * Prepare date filters and sort column to make it usable by the DAO.
     *
     * @param queryParameter
     *            the query parameters
     * @param sort
     *            the sort column
     */
    protected void prepareParameters(QueryParameter queryParameter, String sort) throws ParseException {
        if (sort != null) {
            queryParameter.setOrderByQuery(OrderByQuery.instanceFromRaw(sort));
        }
        if (queryParameter.getFilter().getFields().contains(START_DATE)) {
            queryParameter.getFilter().put(START_DATE,
                formatter.parse(queryParameter.getFilter().get(START_DATE).toString()).getTime() / 1000);
        }
        if (queryParameter.getFilter().getFields().contains(END_DATE)) {
            queryParameter.getFilter().put(END_DATE,
                formatter.parse(queryParameter.getFilter().get(END_DATE).toString()).getTime() / 1000);
        }
    }

    /**
     * Check if the method is valid.
     * @param method the method
     * @throws SupplyException if the method is not PUT, POST, or PATCH
     */
    protected void checkMethod(String method) throws SupplyException {
        if (method != null && !Arrays.asList("put", "post", "patch").contains(method.toLowerCase())) {
            throw new SupplyException("method should be either POST , PUT, PATCH", 400);
        }
    }
}