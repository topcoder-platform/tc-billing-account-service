SELECT 
    project_contest_fee_percentage_id AS id, 
    project_id AS projectId, contest_fee_percentage AS challengeFeePercentage, 
    active AS active, creation_user AS createdBy, 
    creation_date AS createdAt, modification_user AS updatedBy, 
    modification_date AS updatedAt  
FROM project_contest_fee_percentage 
WHERE project_id = :projectId

