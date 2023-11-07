UPDATE project_challenge_budget
    SET locked_amount = :lockedAmount, 
        consumed_amount = :consumedAmount
    WHERE project_id = :projectId
    AND challenge_id = :challengeId