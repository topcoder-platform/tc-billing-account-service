SELECT SKIP 0 FIRST 10
       c.client_id as id,
       c.name,
       RTRIM(decode (c.status, 1, 'Active', 'Inactive')) AS status,
       c.start_date as startDate,
       c.end_date as endDate,
       c.code_name as codeName,
       c.customer_number as customerNumber
FROM client c
WHERE c.is_deleted = 0 AND 1=1
    ORDER BY c.name asc