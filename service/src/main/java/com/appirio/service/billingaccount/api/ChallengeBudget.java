package com.appirio.service.billingaccount.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for challenge budget.
 * 
 * @author liuliquan
 * @version 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeBudget {

	/**
	 * The challenge Id.
	 */
	@Getter
	@Setter
	private String challengeId;

	/**
	 * The locked amount.
	 */
	@Getter
	@Setter
	private Float lockedAmount;

	/**
	 * The consumed amount.
	 */
	@Getter
	@Setter
	private Float consumedAmount;
}
