INSERT INTO project_contest_fee_percentage (
    project_contest_fee_percentage_id, project_id, 
    contest_fee_percentage, active, creation_user, 
    creation_date, modification_user, modification_date
    ) 
VALUES (
    :id, 
    :projectId, :challengeFeePercentage, 
    :active, :userId, current, :userId, current);