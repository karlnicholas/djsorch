package com.github.karlnicholas.djsorch.journal;

import java.time.LocalDate;

public interface PostingFunctions {
	boolean inBillingCycle(BillingCyclePosting billingCycle);
	boolean backdated(BillingCyclePosting billingCycle);
	boolean validate();
	LocalDate retrieveTransactionDate();
}
