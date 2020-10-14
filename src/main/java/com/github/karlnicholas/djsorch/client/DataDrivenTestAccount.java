package com.github.karlnicholas.djsorch.client;

import java.util.List;
import java.util.Observable;

import com.github.karlnicholas.djsorch.model.Account;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DataDrivenTestAccount extends Observable implements AccountHolder {
	private OpenAccountData openAccount;
	private List<PaymentTestData> payments;
	private Boolean disabled = false;
	public void initialize() {
		if ( disabled ) return;
		addObserver(openAccount);
		payments.stream().forEach(this::addObserver);
	}
	@Override
	public void update(Observable o, Object arg) {
		setChanged();
		notifyObservers(arg);
	}
	@Override
	public Account getAccount() {
		return openAccount.getAccount();
	}
}
