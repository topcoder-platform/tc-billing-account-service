package com.appirio.service.billingaccount.dao;

import com.appirio.service.billingaccount.api.IdSequence;
import com.appirio.supply.dataaccess.DatasourceName;

import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Define;

@DatasourceName("oltp")
public interface SequenceDAO {
    @SqlQuery("SELECT <sequenceName>.nextval AS nextId FROM systables WHERE tabid = 1")
    IdSequence getIdSequence(@Define("sequenceName") String sequenceName);
}