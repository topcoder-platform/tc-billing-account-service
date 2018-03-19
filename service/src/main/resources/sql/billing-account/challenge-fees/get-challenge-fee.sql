SELECT
     project_contest_fee_id AS id, 
     project_id AS projectId, (is_studio == 1) AS studio, 
     contest_type_id AS challengeTypeId, 
     contest_fee AS challengeFee, creation_user AS createdBy, 
     creation_date AS createdAt, modification_user AS updatedBy, 
     modification_date AS updatedAt, is_deleted AS deleted, 
     name AS name
FROM project_contest_fee 
WHERE project_id = :projectId
