/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.billingaccount.dao;

import com.appirio.service.billingaccount.api.IdDTO;
import com.appirio.supply.dataaccess.DatasourceName;
import com.appirio.supply.dataaccess.SqlQueryFile;
import com.appirio.supply.dataaccess.SqlUpdateFile;

import org.skife.jdbi.v2.sqlobject.Bind;

/**
 * DAO to handle challenge data in the persistence.
 *
 * @author TCSCODER
 * @version 1.0
 */
@DatasourceName("oltp")
public interface ChallengeDAO {

    /**
     * Updates the fixed challenge fee in the persistence.
     * 
     * @param challengeId Th ide of the challenge for which to update the fixed fee.
     * @param fixedFee The value of the fixed fee to set for the challenge.
     * @param userId The id of the user who performs the update operation, used for auditing purpose.
     */
    @SqlUpdateFile("sql/challenge/update-fixed-fee.sql")
    void updateFixedChallengeFee(@Bind("challengeId") Long challengeId, @Bind("fixedFee") double fixedFee,
            @Bind("userId") Long userId);

    /**
     * Sets the challenge percentage fee of the specified challenge.
     * 
     * @param challengeId The id of the challenge for which to set the percentage fee.
     * @param percentageFee The percentage fee value to set.
     * @param userId The id of the user who is performing the operation, used for auditing purpose.
     */
    @SqlUpdateFile("sql/challenge/set-percentage-fee.sql")
    void setChallengePercentageFee(@Bind("challengeId") Long challengeId, @Bind("percentageFee") double percentageFee,
            @Bind("userId") Long userId);

    /**
     * Checks if the percentage fee already exists for the given challenge.
     * 
     * @param challengeId The id of the challenge for which to check the existince of the percentage fee.
     */
    @SqlQueryFile("sql/challenge/check-percentage-fees-exists.sql")
    IdDTO checkPercentageChallengeFeeExists(@Bind("challengeId") Long challengeId);

    /**
     * Inserts the specified percentage fee for the challenge identified by the given challengeId.
     * 
     * @param challengeId The id of the challenge for which to insert the percentage fee.
     * @param percentageFee The percenatge fee value.
     * @param userId The id of the user who performs the operation, used for auditing purpose.
     */
    @SqlUpdateFile("sql/challenge/insert-percentage-fee.sql")
    void insertChallengePercentageFee(@Bind("challengeId") Long challengeId,
            @Bind("percentageFee") double percentageFee, @Bind("userId") Long userId);

    /**
     * Gets the challenge id wrapped in an IdDTO to check if the challenge exists or not.
     * 
     * @param challengeId The id of the challenge for which to check the existince.
     */
    @SqlQueryFile("sql/challenge/get-challenge-id-dto.sql")
    IdDTO getChallengeIdDTO(@Bind("challengeId") Long challengeId);
}