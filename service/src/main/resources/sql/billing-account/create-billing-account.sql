INSERT INTO tt_project (project_id, company_id, name, active, sales_tax, po_box_number, payment_terms_id, description, start_date, end_date, project_status_id, creation_date, creation_user, modification_date, modification_user)
    VALUES (:projectId, 1, :name, 1, :salesTax, :poNumber, :paymentTermId, '', :startDate, :endDate, :statusId, current, :userName, current, :userName)