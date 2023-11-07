/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.billingaccount.api;

import com.appirio.supply.dataaccess.api.BaseModel;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import java.util.Date;

/**
 * Represents a billing account
 *
 * <p>
 *  Changes in v 1.1 : FAST 72HRS!! - ADD APIS FOR CLIENTS AND SOME LOGIC CHANGES
 *  -- Added new fields : description, subscriptionNumber, companyId, manualPrizeSetting
 * </p>
 *
 * <p>
 *  Changes in v 1.2 Fast 48hrs!! Topcoder - Improvement For Billing Account Service
 *  -- Added new field: clientId
 * </p>
 *
 * <p>
 *  Changes in v 1.2 :
 *  -- Added new field : billable
 * </p>
 * 
 * @author TCSCODER, TCSCODER
 * @version 1.2
 */
@NoArgsConstructor
@AllArgsConstructor
public class BillingAccount extends BaseModel {
	/**
	 * PK of the billing account
	 */
	@Getter
	@Setter
	private Long id;

	/**
	 * The name of the billing account
	 */
	@Getter
	@Setter
	@NotBlank
	private String name;

	/**
	 * The status of the billing account
	 */
	@Getter
	@Setter
	private String status;

	/**
	 * The start date of the billing account
	 */
	@Getter
	@Setter
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm'Z'", timezone="GMT")
	private Date startDate;

	/**
	 * The end date of the billing account
	 */
	@Getter
	@Setter
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm'Z'", timezone="GMT")
	private Date endDate;

	/**
	 * The challenge budget for this billing account.
	 */
	@Getter
	@Setter
	private Float budgetAmount;

	/**
	 * The sales tax.
	 */
	@Getter
	@Setter
	@Min(0)
	private Float salesTax;

	/**
	 * The po box number
	 */
	@Getter
	@Setter
	private String poNumber;

	/**
	 * The payment terms
	 */
	@Getter
	@Setter
	@NotNull
	private PaymentTermsDTO paymentTerms;

	/**
	 * The description of the billing account
	 * @since 1.1
	 */
	@Getter
	@Setter
	@NotBlank
	private String description;

	/**
	 * The billing account subscription number.
	 * @since 1.1
	 */
	@Getter
	@Setter
	private String subscriptionNumber;

	/**
	 * The id of the company to which the billing account is associated.
	 *
	 * @since 1.1
	 */
	@Getter
	@Setter
	@NotNull
	@Min(0)
	private Long companyId;

	/**
	 * The manual prize setting flag value. The default value is set to 0.
	 *
	 * @since 1.1
	 */
	@Getter
	@Setter
	private Long manualPrizeSetting;

	/**
     * Id of the client
     *
     * @since 1.2
     */
    @Getter
    @Setter
    @NotNull
    private Long clientId;

  /** 
   * The flag indicating this account is billable.
	 * @since 1.2
	 */
	@Getter
	@Setter
	private Boolean billable;
}
