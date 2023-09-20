INSERT INTO project_challenge_budget (
   project_id, 
   challenge_id,
   locked_amount,
   consumed_amount) 
VALUES (
    :projectId,
    :challengeId, 
    :lockedAmount,
    :consumedAmount)
    