package com.github.karlnicholas.djsorch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.karlnicholas.djsorch.model.Account;
import com.github.karlnicholas.djsorch.repository.AccountRepository;

@RestController
@RequestMapping("/account")
public class AccountRestController {
//	private static final Logger logger = LoggerFactory.getLogger(AccountRestController.class);
	private final AccountRepository accountRepository;
	public AccountRestController(
			AccountRepository accountRepository 
	) {
		this.accountRepository = accountRepository;
	}
	@PostMapping
	public ResponseEntity<?> createAccount(@RequestBody Account account) {
		try {
			return ResponseEntity.accepted().body(accountRepository.save(account));
		} catch ( Exception e ) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

//		queueNewTransactionPost(transactionSubmitted.getAccount().getId(), transactionSubmitted.getId(), "transaction");

	}
	@GetMapping("count")
	public Long countTransactions() {
		return accountRepository.count();
	}
}
