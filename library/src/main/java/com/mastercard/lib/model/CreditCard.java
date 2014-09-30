package com.mastercard.lib.model;

/**
 * Class representing a user-provided card
 * 
 * @author sonal.agarwal
 * 
 */
public class CreditCard {

	private String ccNo;
	private String cvv;
	private String expYear;
	private String expMonth;
	private String lastFourDigits;

	/**
	 * Returns the credit card number
	 * @return the credit card number
	 */

	public String getCcNo() {
		return ccNo;
	}

	/**
	 * 
	 * @param ccNo
	 *            Sets the credit card number
	 */
	public void setCcNo(String ccNo) {
		this.ccNo = ccNo;
	}

	/**
	 * Returns the Credit Card CVV Number
	 * 
	 * @return the Credit Card CVV Number
	 */
	public String getCvv() {
		return cvv;
	}

	/**
	 * 
	 * @param cvv
	 *            sets the Credit Card CVV Number
	 */
	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	/**
	 * Returns the Credit Card Expiration year
	 * 
	 * @return the Credit Card Expiration year
	 */
	public String getExpYear() {
		return expYear;
	}

	/**
	 * 
	 * @param expYear
	 *            sets the Credit Card Expiration year
	 */
	public void setExpYear(String expYear) {
		this.expYear = expYear;
	}

	/**
	 * Returns the Credit Card expiration month
	 * 
	 * @return
	 */
	public String getExpMonth() {
		return expMonth;
	}

	/**
	 * 
	 * @param expMonth
	 *            sets the Credit Card expiration month
	 */
	public void setExpMonth(String expMonth) {
		this.expMonth = expMonth;
	}

	/**
	 * Returns the Credit Card last four digits
	 * 
	 * @return
	 */
	public String getLastFourDigits() {
		return lastFourDigits;
	}

	/**
	 * 
	 * @param lastFourDigits
	 *            sets the Credit Card last four digits
	 */
	public void setLastFourDigits(String lastFourDigits) {
		this.lastFourDigits = lastFourDigits;
	}

}
