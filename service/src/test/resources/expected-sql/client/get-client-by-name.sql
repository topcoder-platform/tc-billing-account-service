SELECT
     cl.client_id as id,
     cl.name,
     RTRIM(decode (cl.status, 1, 'Active', 'Inactive')) AS status,
     cl.start_date as startDate,
     cl.end_date as endDate,
     cl.code_name as codeName,
     cl.customer_number as customerNumber
FROM client cl
WHERE cl.name = :clientName