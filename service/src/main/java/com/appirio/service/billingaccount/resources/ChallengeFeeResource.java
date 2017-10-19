/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.billingaccount.resources;

import com.appirio.service.billingaccount.api.ChallengeFeeDTO;
import com.appirio.service.billingaccount.manager.ChallengeManager;
import com.appirio.supply.ErrorHandler;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.request.PostPutRequest;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.appirio.tech.core.api.v3.response.ApiResponseFactory;
import com.appirio.tech.core.auth.AuthUser;

import io.dropwizard.auth.Auth;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST resource endpoint for challenge fixed and percentage fees management.
 *
 * @author TCSCODER
 * @version 1.0
 */
@Path("challenge-fees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChallengeFeeResource extends BaseResource {

    /**
     * The Logger used to log debug information and exceptions.
     */
    private static final Logger logger = LoggerFactory.getLogger(ChallengeFeeResource.class);

    /**
     * The ChallengeManager instance to be used to access Challenge data in the persistence.
     */
    private ChallengeManager challengeManager;

    /**
     * Constructor to initialize Challenge Fee manager.
     *
     * @param challengeFeeManager
     *            manager for Challenge fees.
     */
    public ChallengeFeeResource(ChallengeManager challengeManager) {
        this.challengeManager = challengeManager;
    }

    /**
     * Updates fixed challenge fee.
     *
     * @param user The currently logged in user.
     * @param challengeId The id of the challenge for which to update the fixed fee.
     * @param request The fixed fee request which holds ChallengeFeeDTO containing the fee value.
     *
     * @return the api response
     */
    @POST
    @Path("{challengeId}/fixed")
    public ApiResponse updateFixedChallengeFee(@Auth AuthUser user, @PathParam("challengeId") Long challengeId,
            PostPutRequest<ChallengeFeeDTO> request) {
        logger.debug("updateFixedChallengeFee, challengeId =" + challengeId + " fixed Fee ="
                + request.getParam().getValue());

        try {
            checkAdmin(user);

            if (request.getParam().getValue() < 0) {
                throw new IllegalArgumentException("The challenge fixed fee should not be negative");
            }
            
            // check if the challenge identified by the given id exists in the persistence
            if (challengeManager.getChallengeIdDTO(challengeId) == null) {
                throw new SupplyException("The challenge identified by id = " + challengeId + " does not exist",
                        HttpServletResponse.SC_NOT_FOUND);
            }

            challengeManager.updateFixedChallengeFee(challengeId, request.getParam().getValue(),
                    Long.valueOf(user.getUserId().getId()));

            return ApiResponseFactory.createResponse(challengeId);
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }

    /**
     * Updates percentage challenge fee for the challenge identified by the given challengeId.
     *
     * @param user The currently logged in user.
     * @param challengeId The id of the challenge fow which to update the percentage fee.
     * @param request The request holding the percentage fee value to set for the challenge.
     *
     * @return the api response
     */
    @POST
    @Path("{challengeId}/percentage")
    public ApiResponse updatePercentageChallengeFee(@Auth AuthUser user, @PathParam("challengeId") Long challengeId,
            PostPutRequest<ChallengeFeeDTO> request) {
        logger.debug("updatePercentageChallengeFee, challengeId =" + challengeId + " fixed Fee ="
                + request.getParam().getValue());

        try {
            checkAdmin(user);

            if (request.getParam().getValue() < 0) {
                throw new IllegalArgumentException("The percentage fee should not be negative");
            }

            // check if the challenge identified by the given id exists in the persistence
            if (challengeManager.getChallengeIdDTO(challengeId) == null) {
                throw new SupplyException("The challenge identified by id = " + challengeId + " does not exist",
                        HttpServletResponse.SC_NOT_FOUND);
            }

            challengeManager.updatePercentageChallengeFee(challengeId, request.getParam().getValue(),
                    Long.valueOf(user.getUserId().getId()));
            return ApiResponseFactory.createResponse(challengeId);
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }
}