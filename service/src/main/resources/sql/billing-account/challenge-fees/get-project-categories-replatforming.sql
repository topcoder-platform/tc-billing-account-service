SELECT 
    project_category_id as challengeTypeId, 
    name as description, (project_type_id == 3) as studio
FROM tcs_catalog\:project_category_lu
WHERE display = 't'
ORDER BY display_order
