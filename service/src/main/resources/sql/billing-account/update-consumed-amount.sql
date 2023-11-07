UPDATE project
SET
    consumed_amount = NVL(consumed_amount, 0) + :requested_amount,
    locked_amount = NVL(locked_amount, 0) - :unlock_amount
WHERE
    project_id = :project_id;