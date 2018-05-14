UPDATE project_contest_fee_percentage SET 
    project_contest_fee_percentage_id = :id, 
    project_id = :projectId, contest_fee_percentage = :challengeFeePercentage, 
    active = :active,
    modification_user = :userId, modification_date = current 
WHERE project_contest_fee_percentage_id = :id
