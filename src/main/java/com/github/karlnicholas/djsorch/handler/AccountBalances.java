package com.github.karlnicholas.djsorch.handler;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class AccountBalances {
	private BigDecimal principal;
	private BigDecimal totalCredits;
	private BigDecimal totalDebits;
	private BigDecimal dueDateCredits;
	private BigDecimal dueDateDebits;
	private BigDecimal interest;
	private BigDecimal lateFee;
}
