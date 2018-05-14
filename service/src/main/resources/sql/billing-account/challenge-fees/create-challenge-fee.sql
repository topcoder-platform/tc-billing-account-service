INSERT INTO project_contest_fee (
   project_contest_fee_id, project_id, 
   is_studio, contest_type_id, contest_fee, 
   creation_user, creation_date, modification_user, 
   modification_date, name, is_deleted) 
VALUES (
    :id, :projectId, :isStudio, 
    :challengeTypeId, :challengeFee, :userId, 
    current, :userId, current, :name, :deleted);
    
   
    
