/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.billingaccount.manager;

import java.util.Date;
import java.util.List;

import com.appirio.service.billingaccount.api.Client;
import com.appirio.service.billingaccount.dao.ClientDAO;
import com.appirio.service.billingaccount.dto.SaveClientDTO;
import com.appirio.supply.SupplyException;
import com.appirio.supply.dataaccess.QueryResult;
import com.appirio.supply.dataaccess.db.IdGenerator;
import com.appirio.tech.core.api.v3.request.QueryParameter;

/**
 * Manager for clients business logic.
 *
 * <p>
 *  Changes in v 1.1 Fast 48hrs!! Topcoder - Improvement For Billing Account Service
 *  -- Added sort, limit and offset when finding all clients
 *  -- Added support for filtering on name (partial match), status, start date, and end date
 * </p>
 *
 * @author TCSCODER
 * @version 1.1
 */
public class ClientManager extends BaseManager {

    /**
     * Client DAO used for accessing client data in the persistence.
     */
    private ClientDAO clientDAO;

    /**
     * Id generator to create the new clients ids.
     */
    private IdGenerator idGenerator;

    /**
     * Creates and initializes a new ClientManager instance with the specified DAO and id generator.
     *
     * @param clientDAO The client DAO.
     * @param idGenerator  The client id generator.
     */
    public ClientManager(ClientDAO clientDAO, IdGenerator idGenerator) {
        this.clientDAO = clientDAO;
        this.idGenerator = idGenerator;
    }

    /**
     * Gets all clients available in the persistence. It simply delegates the call to the underlying DAO.
     *
     * @return The list of all clients.
     */
    public QueryResult<List<Client>> findAllClients(QueryParameter queryParameter) {
        return clientDAO.findAllClients(queryParameter);
    }

    /**
     * Adds a new client.
     *
     * @param clientDTO The client data to add.
     * @param userId The id of the user who adds the client, used for auditing.
     * @return The newly added Client instance with id populated.
     *
     * @throws SupplyException If any error occurs during the operation.
     */
    public Client addNewClient(SaveClientDTO clientDTO, String userId) throws SupplyException {
        Long id = idGenerator.getNextId();
        Long activeFlag = getActiveFlagFromStatusName("client", clientDTO.getStatus());

        // Check if the provided client name already exists in the persistence.
        if (clientDAO.getClientByName(clientDTO.getName()) != null) {
            throw new IllegalArgumentException(String.format("The specified client name '%s' already exists.",
                    clientDTO.getName()));
        }

        // check the client start date, if not provided use current date.
        if (clientDTO.getStartDate() == null) {
            clientDTO.setStartDate(new Date());
        }

        // Create the client in the persistence.
        clientDAO.addNewClient(id, clientDTO.getName(), activeFlag, clientDTO.getStartDate(), clientDTO.getEndDate(),
                clientDTO.getCodeName(), userId, clientDTO.getCustomerNumber());

        // return the created client instance.
        return clientDAO.getClientById(id);
    }

    /**
     * Retrieves the Client identified by the given clientId
     *
     * @param clientId The of the client to retrieve.
     * @return The Client instance matching the specified id.
     */
    public Client getClientById(Long clientId) {
        return clientDAO.getClientById(clientId);
    }

    /**
     * Updated the client identified by the specified clientId with the supplied clientDTO data.
     *
     * @param clientId The id of the client to update.
     * @param clientDTO The client data to be set for the updated client.
     * @param userId The id of the user who updates the client data, used for auditing purpose.
     *
     * @return The updated Client instance.
     *
     * @throws SupplyException If any error occurs during the operation.
     */
    public Client updateClient(Long clientId, SaveClientDTO clientDTO, String userId) throws SupplyException {

        Long activeFlag = getActiveFlagFromStatusName("client", clientDTO.getStatus());

        // Check if the client identified by the given clientId exists.
        Client existingClient = clientDAO.getClientById(clientId);

        if (existingClient == null) {
            throw new SupplyException(String.format("The client with id - %s does not exist", clientId), 404);
        } else {
            // validate if the provided client name is unique in the persistence.
            Client clientWithInputName = clientDAO.getClientByName(clientDTO.getName());
            if (clientWithInputName != null && !clientWithInputName.getId().equals(existingClient.getId())) {
                System.out.println("ClientDTO name: " + clientDTO.getName() + " clientWithInputNameID: " + clientWithInputName.getId() + " existingClient.getId: " +  existingClient.getId());

                // There is another client different than the client to update which have the new specified name
                throw new IllegalArgumentException(String.format("The client name '%s' already exists",
                        clientDTO.getName()));
            }
        }

        // check the client start date, if not provided use current date.
        if (clientDTO.getStartDate() == null) {
            clientDTO.setStartDate(new Date());
        }

        // Update the client in the persistence
        clientDAO.updateClient(clientId, clientDTO.getName(), activeFlag, clientDTO.getStartDate(),
                clientDTO.getEndDate(), clientDTO.getCodeName(), userId, clientDTO.getCustomerNumber());

        // return the updated client instance.
        return clientDAO.getClientById(clientId);
    }
}