UPDATE project_contest_fee SET 
    project_id = :projectId, is_studio = :isStudio, 
    contest_type_id = :challengeTypeId, contest_fee = :challengeFee, 
    modification_user = :userId, 
    modification_date = current, 
    is_deleted = :deleted, 
    name = :name 
WHERE project_contest_fee_id = :id