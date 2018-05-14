/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.billingaccount.api;

import com.appirio.supply.dataaccess.api.BaseModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * This class represents project challenge fee.
 * </p>
 * 
 * <p>
 * It's added in Topcoder - Create Challenge Fee Management APIs For Billing
 * Accounts v1.0
 * </p>
 * 
 * @author TCCoder
 * @version 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeFee extends BaseModel {
    /**
     * <p>
     * The id attribute.
     * </p>
     */
    @Getter
    @Setter
    private long id;


    /**
     * <p>
     * The projectId attribute.
     * </p>
     */
    @Getter
    @Setter
    private long projectId;


    /**
     * <p>
     * The challengeType attribute.
     * </p>
     */
    @Getter
    @Setter
    private long challengeTypeId;


    /**
     * <p>
     * The challenge fee attribute
     * </p>
     */
    @Getter
    @Setter
    private double challengeFee;


    /**
     * <p>
     * Represents studio attribute
     * </p>
     */
    @Getter
    @Setter
    private boolean studio;


    /**
     * <p>
     * The challenge type description attribute
     * </p>
     */
    @Getter
    @Setter
    private String challengeTypeDescription;


    /**
     * <p>
     * The deleted attribute
     * </p>
     */
    @Getter
    @Setter
    private boolean deleted;


    /**
     * <p>
     * The name attribute
     * </p>
     */
    @Getter
    @Setter
    private String name;
}
