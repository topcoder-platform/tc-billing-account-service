update tcs_catalog\:project_info 
SET value = :fixedFee,
    modify_date = CURRENT,
    modify_user = :userId
WHERE project_info_type_id = 31 and project_id = :challengeId