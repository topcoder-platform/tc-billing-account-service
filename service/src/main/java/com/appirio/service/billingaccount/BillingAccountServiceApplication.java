/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.billingaccount;

import com.appirio.service.BaseApplication;
import com.appirio.service.billingaccount.api.BillingAccount;
import com.appirio.service.billingaccount.api.Client;
import com.appirio.service.billingaccount.dao.BillingAccountDAO;
import com.appirio.service.billingaccount.dao.ChallengeDAO;
import com.appirio.service.billingaccount.dao.ClientDAO;
import com.appirio.service.billingaccount.manager.BillingAccountManager;
import com.appirio.service.billingaccount.manager.ChallengeManager;
import com.appirio.service.billingaccount.manager.ClientManager;
import com.appirio.service.billingaccount.resources.BillingAccountResource;
import com.appirio.service.billingaccount.resources.ChallengeFeeResource;
import com.appirio.service.billingaccount.resources.ClientResource;
import com.appirio.service.supply.resources.SupplyDatasourceFactory;
import com.appirio.supply.DAOFactory;
import com.appirio.supply.dataaccess.db.IdGenerator;

import io.dropwizard.setup.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * Starting point of the micro-service.
 * 
 * <p>
 * Changes in v1.1 FAST 72HRS!! - ADD APIS FOR CLIENTS AND SOME LOGIC CHANGES
 *  <li>
 *   -- Updated registerResources() to register the ClientResource.
 *  </li>
 * </p>
 * 
 * <p>
 * Changes in v1.2 ADMIN APP CHALLENGE FEE AND REVIEW MANAGEMENT API
 *  <li>
 *   -- Updated registerResources() to register the ChallengeFeeResource.
 *  </li>
 * </p>
 *
 * @author TCSCODER, TCSCODER
 * @version 1.2
 */
public class BillingAccountServiceApplication extends BaseApplication<BillingAccountServiceConfiguration> {

    /**
     * Refer to APIApplication
     */
    @Override
    public String getName() {
        return "billing-account-service";
    }

    /**
     * @see BaseApplication
     * @param config
     */
    @Override
    protected void logServiceSpecificConfigs(BillingAccountServiceConfiguration config) {

        logger.info("\tJDBI configuration ");
        logger.info("\t\tDatabase config name : " + config.getDatabase().getDatasourceName());
        logger.info("\t\tOLTP driver class : " + config.getDatabase().getDriverClass());
        logger.info("\t\tOLTP connection URL : " + config.getDatabase().getUrl());
        logger.info("\t\tOLTP Authentication user : " + config.getDatabase().getUser());

        logger.info("\r\n");
    }

    /**
     * Application entrypoint. See dropwizard and jetty documentation for more details
     *
     * @param args       arguments to main
     * @throws Exception Generic exception
     */
    public static void main(String[] args) throws Exception {
        new BillingAccountServiceApplication().run(args);
    }


    /**
     * Gives the subclasses an opportunity to register resources
     * @param config configuration
     * @param env Environment
     */
    @Override
    protected void registerResources(BillingAccountServiceConfiguration config, Environment env) throws Exception {
        // initialize the Billing account manager.
    	BillingAccountManager billingAccountManager = 
        		new BillingAccountManager(DAOFactory.getInstance().createDAO(BillingAccountDAO.class),
        				IdGenerator.getInstance("com.topcoder.timetracker.ProjectManager"),
        				IdGenerator.getInstance("com.topcoder.timetracker.user.User"));

    	// initialize the client manager
        ClientManager clientManager = new ClientManager(DAOFactory.getInstance().createDAO(ClientDAO.class),
                IdGenerator.getInstance("com.topcoder.timetracker.ClientManager"));
                
        // initialize the Challenge Manager
        ChallengeManager challengeManager = new ChallengeManager(DAOFactory.getInstance().createDAO(ChallengeDAO.class));
        

        // register the resources.
        env.jersey().register(new BillingAccountResource(billingAccountManager));
        env.jersey().register(new ClientResource(clientManager));
        env.jersey().register(new ChallengeFeeResource(challengeManager));

        logger.info("Services registered");
    }

    /**
     * Gives the subclasses an opportunity to prepare to run, for instance, to setup databases
     * @param config configuration
     * @param env Environment
     */
    @Override
    protected void prepare(BillingAccountServiceConfiguration config, Environment env) throws Exception {
        // get configuration from env
        List<SupplyDatasourceFactory> databases = new ArrayList<>();

        databases.add(config.getDatabase());

        configDatabases(config, databases, env);
    }
}
