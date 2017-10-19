/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.billingaccount.manager;

import com.appirio.service.billingaccount.api.IdDTO;
import com.appirio.service.billingaccount.dao.ChallengeDAO;
import com.appirio.supply.SupplyException;

/**
 * Manager for challenge fee business logic.
 *
 * @author TCSCODER
 * @version 1.0
 */
public class ChallengeManager {

    /**
     * Challenge DAO used for managing challenge data in the persistence.
     */
    private ChallengeDAO challengeDAO;

    /**
     * Creates a new instance of ChallengeManager and initializes the challengeDAO field.
     * 
     * @param challengeDAO The ChallengeDAO instance to be used to manage challenge data in the persistence.
     */
    public ChallengeManager(ChallengeDAO challengeDAO) {
        this.challengeDAO = challengeDAO;
    }

    /**
     * Updates the fixed fee for the challenge identified by the specified challengeId.
     * 
     * @param challengeId The id of the challenge for which to update the fixed fee.
     * @param fixedFee The fixed fee value.
     * @param userId The id of the user who updates the fixed fee, it is used for auditing purpose.
     * 
     * @throws SupplyException If any error occurs during the operation.
     */
    public void updateFixedChallengeFee(Long challengeId, double fixedFee, Long userId) throws SupplyException {
        // Set the percentage fee to 0
        challengeDAO.setChallengePercentageFee(challengeId, 0, userId);

        // Update the fixed fee in the persistence.
        challengeDAO.updateFixedChallengeFee(challengeId, fixedFee, userId);
    }

    /**
     * Updates the percentage fee for the challenge identified bu the given challengeId.
     * If a percentage fee already exists for the given challenge, it is updated; otherwise a new entry is created.
     * 
     * @param challengeId The id of the challenge for which to update the percentage fee.
     * @param percentageFee The percentage fee value to set.
     * @param userId the id of the user who performs the operation, used for auditing purpose.
     * 
     * @throws SupplyException If any error occurs during the operation.
     */
    public void updatePercentageChallengeFee(Long challengeId, double percentageFee, Long userId)
            throws SupplyException {
        // if the percentage fee does not already exist in the persitence, we create a new entry for it.
        if (challengeDAO.checkPercentageChallengeFeeExists(challengeId) != null) {
            challengeDAO.setChallengePercentageFee(challengeId, percentageFee, userId);
        } else {
            challengeDAO.insertChallengePercentageFee(challengeId, percentageFee, userId);
        }
    }

    /**
     * Gets a challenge IdDTO by the given id. returns null if such challenge does not exist.
     * 
     * @param challengeId Id of the challenge to retrieve.
     * 
     * @return The retrieved challenge id wrapped in IdDTO.
     * 
     * @throws SupplyException If any error occurs during the operation.
     */
    public IdDTO getChallengeIdDTO(Long challengeId) throws SupplyException {
        return challengeDAO.getChallengeIdDTO(challengeId);
    }
}