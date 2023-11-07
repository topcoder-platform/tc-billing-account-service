/*
 * Copyright (C) 2023 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.billingaccount.api;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a billing account with challenge budget details.
 * 
 * @author liuliquan
 * @version 1.0
 */
@NoArgsConstructor
public class BillingAccountBudget extends BillingAccount {

	public BillingAccountBudget(BillingAccount ba) {
		this.setId(ba.getId());
		this.setName(ba.getName());
		this.setStatus(ba.getStatus());
		this.setClientId(ba.getClientId());
		this.setCompanyId(ba.getCompanyId());
		this.setBudgetAmount(ba.getBudgetAmount());
		this.setDescription(ba.getDescription());
		this.setStartDate(ba.getStartDate());
		this.setEndDate(ba.getEndDate());
		this.setBillable(ba.getBillable());
		this.setPoNumber(ba.getPoNumber());
		this.setSalesTax(ba.getSalesTax());
		this.setPaymentTerms(ba.getPaymentTerms());
		this.setSubscriptionNumber(ba.getSubscriptionNumber());
		this.setManualPrizeSetting(ba.getManualPrizeSetting());
		this.setCreatedAt(ba.getCreatedAt());
		this.setCreatedBy(ba.getCreatedBy());
		this.setUpdatedAt(ba.getUpdatedAt());
		this.setUpdatedBy(ba.getUpdatedBy());
	}

	/**
	 * The available budget.
	 */
	@Getter
	@Setter
	private Float availableBudget;

	/**
	 * The challenges budget details.
	 */
	@Getter
	@Setter
	private List<ChallengeBudget> challengeBudgets;
}
