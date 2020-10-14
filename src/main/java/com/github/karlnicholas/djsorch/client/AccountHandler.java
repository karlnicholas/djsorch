package com.github.karlnicholas.djsorch.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Observable;
import java.util.Observer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.github.karlnicholas.djsorch.journal.BillingCyclePosting;
import com.github.karlnicholas.djsorch.journal.LoanFundingPosting;
import com.github.karlnicholas.djsorch.journal.PaymentCreditPosting;
import com.github.karlnicholas.djsorch.model.Account;
import com.github.karlnicholas.djsorch.model.TransactionOpen;
import com.github.karlnicholas.djsorch.model.TransactionSubmitted;
import com.github.karlnicholas.djsorch.model.TransactionType;
import com.github.karlnicholas.djsorch.service.PostingReader;

public class AccountHandler extends Observable implements Observer {
	private final RestTemplate restTemplate;
	private final ObjectMapperWrapper objectMapper;
	private final PostingReader postingReader;
	private AccountHolder accountHolder;
	public AccountHandler(RestTemplate restTemplate, ObjectMapperWrapper objectMapper, PostingReader postingReader) {
		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;
		this.postingReader = postingReader;
	}

	public void addAccount(AccountHolder accountHolder) {
		this.accountHolder = accountHolder;
		addObserver(accountHolder);
	}
	@Override
	public void update(Observable o, Object arg) {
		NotificationParameters notificationParameters = (NotificationParameters)arg;
		notificationParameters.setAccountHandler(this);
		setChanged();
		notifyObservers(arg);
	}

	protected Account makeAccount(LocalDate openDate, Integer months, BigDecimal principal, BigDecimal rate) {

		Account account = Account.builder().openDate(openDate).build();
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Account> accountResponse = restTemplate.postForEntity("http://localhost:8080/account", account, Account.class);
		if ( accountResponse.getStatusCode() != HttpStatus.CREATED ) {
			throw new IllegalStateException(accountResponse.toString());
		}
		account = accountResponse.getBody();

		LoanFundingPosting loanFundingPosting = new LoanFundingPosting();
		loanFundingPosting.setTermMonths(months);
		loanFundingPosting.setInterestRate(rate);
		loanFundingPosting.setPrincipal(principal);
		loanFundingPosting.setInceptionDate(openDate);
		TransactionSubmitted transactionSubmitted;
			transactionSubmitted = TransactionSubmitted.builder()
					.accountId(account.getId())
					.version(1L)
					.transactionType(TransactionType.LOAN_FUNDING)
					.transactionDate(openDate)
					.payload(objectMapper.writeValueAsString(loanFundingPosting))
					.build();
		ResponseEntity<Void> transactionResponse = restTemplate.postForEntity("http://localhost:8080/queue/post", transactionSubmitted, Void.class);
		if ( transactionResponse.getStatusCode() != HttpStatus.ACCEPTED ) {
			throw new RuntimeException(transactionResponse.toString());
		}
		return account;
	}

	protected void makePayment(Account account, BigDecimal paymentAmount, LocalDate businessDate, LocalDate paymentDate) {

		PaymentCreditPosting paymentCreditPosting = new PaymentCreditPosting();

		if ( paymentAmount.compareTo(BigDecimal.ZERO) != 0 ) {
			paymentCreditPosting.setAmount(paymentAmount);
			paymentCreditPosting.setDate(paymentDate);
			TransactionSubmitted transactionSubmitted = TransactionSubmitted.builder()
					.accountId(account.getId())
					.version(1L)
					.transactionType(TransactionType.PAYMENT_CREDIT)
					.payload(objectMapper.writeValueAsString(paymentCreditPosting))
					.build();
			ResponseEntity<Void> transactionResponse = restTemplate.postForEntity("http://localhost:8080/queue/post", transactionSubmitted, Void.class);
			if ( transactionResponse.getStatusCode() != HttpStatus.ACCEPTED ) {
				throw new IllegalStateException(transactionResponse.toString());
			}
		}
	}
	protected BillingCyclePosting updateBillingCycle(Account account) {
		return postingReader.readValue(
			restTemplate.getForEntity("http://localhost:8080/queue/get/latestbillingcycle/{accountId}", TransactionOpen.class, account.getId()).getBody(), BillingCyclePosting.class
		);
	}

	public Account getAccount() {
		return accountHolder.getAccount();
	}
}
