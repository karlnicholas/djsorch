package com.github.karlnicholas.djsorch.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Observable;
import java.util.Observer;

import com.github.karlnicholas.djsorch.client.NotificationParameters.ACCOUNT_ACTIONS;

import lombok.Data;

@Data
public class PaymentTestData implements Observer {
	private LocalDate businessDate;
	private LocalDate paymentDate;
	private BigDecimal amount;
	@Override
	public void update(Observable o, Object arg) {
		NotificationParameters notificationParameters = (NotificationParameters)arg;
		if ( notificationParameters.getAction().equals(ACCOUNT_ACTIONS.PAYMENT) 
				&& notificationParameters.getDate().isEqual(businessDate)
		) {
			DataDrivenTestAccount accountTest = (DataDrivenTestAccount)o;
			notificationParameters.getAccountHandler().makePayment(
				accountTest.getOpenAccount().getAccount(), 
				amount, 
				businessDate, paymentDate);
		}
	}
}
