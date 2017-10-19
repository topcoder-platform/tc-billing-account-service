update tcs_catalog\:project_info
SET value = :percentageFee,
    modify_date = CURRENT,
    modify_user = :userId
WHERE project_info_type_id = 57 and project_id = :challengeId