SELECT SUM(locked_amount + consumed_amount) as floatValue
    FROM project_challenge_budget 
    WHERE project_id = :projectId 
    AND challenge_id <> :challengeId