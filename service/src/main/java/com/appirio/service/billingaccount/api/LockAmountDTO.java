/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.billingaccount.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This DTO hold the information of a Consumed Budget information ( consume amount and unlock amount).
 * 
 * @author TCSCODER
 * @version 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
public class LockAmountDTO {

	/**
	 * The consume amount.
	 */
    @Getter
    @Setter
    private Float lockAmount;


    /**
	 * The challengeId : Required for publish message.
	 */
    @Getter
    @Setter
    private String challengeId;
    
}
