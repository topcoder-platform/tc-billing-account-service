UPDATE project
SET
    locked_amount = NVL(locked_amount, 0) + :requested_amount
WHERE
    project_id = :project_id;
