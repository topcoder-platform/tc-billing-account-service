package com.appirio.service.billingaccount.api;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * BillingAccountFees entity.
 * 
 * It's added in Topcoder - Create Challenge Fee Management APIs For Billing
 * Accounts v1.0
 * 
 * Version 1.1 - Quick 48hours! Topcoder - Update Logic For Challenge Fees Managment v1.0
 * - change challengeFeePercentage's type from double to Double
 * 
 * 
 * @author TCCoder
 * @version 1.1 
 *
 */
@NoArgsConstructor
@AllArgsConstructor
public class BillingAccountFees {
    
    /**
     * The challengeFeeFixed.
     */
    @Getter
    @Setter
    private boolean challengeFeeFixed;
    
    /**
     * The challengeFeePercentage
     */
    @Getter
    @Setter
    private Double challengeFeePercentage;
    
    /**
     * The challengeFees
     */
    @Getter
    @Setter
    private List<ChallengeFee> challengeFees;
}
