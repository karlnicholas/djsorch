package com.github.karlnicholas.djsorch.client;

import java.util.Observer;

import com.github.karlnicholas.djsorch.model.Account;

public interface AccountHolder extends Observer {
	Account getAccount();
}
