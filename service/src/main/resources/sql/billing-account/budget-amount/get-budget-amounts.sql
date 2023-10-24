SELECT challenge_id as challengeId, locked_amount as lockedAmount, consumed_amount as consumedAmount
    FROM project_challenge_budget 
    WHERE project_id = :projectId