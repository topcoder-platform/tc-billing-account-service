/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.billingaccount.api;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the ChallengeType entity.
 * 
 * It's added in Topcoder - Create Challenge Fee Management APIs For Billing Accounts v1.0
 * 
 * @author TCCoder
 * @version 1.0
 */
public class ChallengeType {
    /**
     * Represents the challenge type id attribute.
     */
    @Getter
    @Setter
    private long challengeTypeId;
    
    /**
     * Represents the description attribute.
     */
    @Getter
    @Setter
    private String description;
    
    /**
     * Represents the studio attribute.
     */
    @Getter
    @Setter
    private boolean studio;
    
}