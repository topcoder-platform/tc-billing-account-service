/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.billingaccount.api;

import com.appirio.supply.dataaccess.api.BaseModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * This class represents project contest fee percentage.
 * </p>
 * <p>
 * It's added in Topcoder - Create Challenge Fee Management APIs For Billing Accounts v1.0
 * </p>
 * 
 * <p>
 * Version 1.1 - Quick 48hours! Topcoder - Update Logic For Challenge Fees Managment v1.0
 * - change challengeFeePercentage from double to Double
 * </p>
 * 
 * @author TCCoder
 * @version 1.1 
 */
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeFeePercentage extends BaseModel {
    /**
     * <p>
     * The id.
     * </p>
     */
    @Getter
    @Setter
    private long id;

    /**
     * <p>
     * The project id.
     * </p>
     */
    @Getter
    @Setter
    private long projectId;

    /**
     * <p>
     * The contest fee percentage.
     * </p>
     */
    @Getter
    @Setter
    private Double challengeFeePercentage;

    /**
     * <p>
     * The active flag.
     * </p>
     */
    @Getter
    @Setter
    private boolean active;
}
