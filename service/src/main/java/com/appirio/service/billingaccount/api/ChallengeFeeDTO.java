/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.billingaccount.api;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO to receive the fee (percentage/fixed fee) from the request body.
 * 
 * @author TCSCODER
 * @version 1.0
 */
public class ChallengeFeeDTO {

    /**
     * The value of the fee.
     */
    @Getter
    @Setter
    private Double value;
}