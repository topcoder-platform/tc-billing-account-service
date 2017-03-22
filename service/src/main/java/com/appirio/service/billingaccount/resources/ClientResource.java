/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.billingaccount.resources;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.appirio.supply.SupplyException;
import io.dropwizard.auth.Auth;
import io.dropwizard.jersey.PATCH;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appirio.service.billingaccount.api.Client;
import com.appirio.service.billingaccount.dto.SaveClientDTO;
import com.appirio.service.billingaccount.manager.ClientManager;
import com.appirio.service.supply.resources.MetadataApiResponseFactory;
import com.appirio.supply.ErrorHandler;
import com.appirio.tech.core.api.v3.request.PostPutRequest;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.request.annotation.APIQueryParam;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.appirio.tech.core.api.v3.response.ApiResponseFactory;
import com.appirio.tech.core.auth.AuthUser;
import com.codahale.metrics.annotation.Timed;

import java.util.Arrays;

/**
 * REST resource endpoint for clients
 *
 * @author TCSCODER
 * @version 1.0
 */
@Path("clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource extends BaseResource {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ClientResource.class);

    /**
     * The ClientManager instance to be used to access client data in the persistence.
     */
    private ClientManager clientManager;

    /**
     * Constructor to initialize client manager.
     *
     * @param clientManager
     *            manager for clients.
     */
    public ClientResource(ClientManager clientManager) {
        this.clientManager = clientManager;

    }

    /**
     * Find all clients.
     *
     * @param user
     *            the currently logged in user
     *
     * @return the api response
     */
    @GET
    public ApiResponse findAllClients(@Auth AuthUser user,
        @APIQueryParam(repClass = Client.class) QueryParameter queryParameter,
        @QueryParam("sort") String sort) {
        logger.debug("findAllClients");
        try {
            checkAdmin(user);
            prepareParameters(queryParameter, sort);
            return MetadataApiResponseFactory.createResponse(clientManager.findAllClients(queryParameter));
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }

    /**
     * Creates a new client.
     *
     * @param user
     *            The logged in user.
     * @param request
     *            The new client request to create.
     * @return api response
     */
    @POST
    @Timed
    public ApiResponse addNewClient(@Auth AuthUser user, @Valid PostPutRequest<SaveClientDTO> request) {
        logger.debug("addNewClient, request : " + request);
        try {
            checkAdmin(user);
            if (request.getParam() == null) {
                throw new IllegalArgumentException("Should provide client param");
            }
            String method = request.getMethod();
            checkMethod(method);
            if (method != null && Arrays.asList("put", "patch").contains(method.toLowerCase())) {
                return MetadataApiResponseFactory
                        .createResponse(clientManager.updateClient(request.getParam().getId(), request.getParam(), user.getUserId().getId()));
            }
            return MetadataApiResponseFactory
                    .createResponse(clientManager.addNewClient(request.getParam(), user.getUserId().getId()));
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }

    /**
     * Fetches client based on id
     *
     * @param id
     *            The client id.
     * @return api response
     */
    @GET
    @Path("/{clientId}")
    @Timed
    public ApiResponse getClientById(@Auth AuthUser user, @PathParam("clientId") Long id) {
        logger.debug("getClientById, id : " + id);
        try {
            checkAdmin(user);
            return ApiResponseFactory.createResponse(clientManager.getClientById(id));
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }

    /**
     * Updates the client with the specified input data.
     *
     * @param id
     *            The client id to update.
     * @param request
     *            The update client request.
     * @return api response
     */
    @PATCH
    @Path("/{clientId}")
    @Timed
    public ApiResponse updateClient(@Auth AuthUser user, @PathParam("clientId") Long id,
            @Valid PostPutRequest<SaveClientDTO> request) {
        logger.debug("updateClient, id = " + id + ", request = " + request);
        try {
            checkAdmin(user);
            if (request.getParam() == null) {
                throw new IllegalArgumentException("Should provide client param");
            }
            return ApiResponseFactory.createResponse(clientManager.updateClient(id, request.getParam(), user
                    .getUserId().toString()));
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }
}