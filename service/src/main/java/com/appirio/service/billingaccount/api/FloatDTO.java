package com.appirio.service.billingaccount.api;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO to receive a Float value
 * This can be used for purpose of returning Float Value from SQL query
 * @author TCSCODER
 */
public class FloatDTO {

    /**
     * The received Flaot value
     */
    @Getter
    @Setter
    private Float floatValue;
}

