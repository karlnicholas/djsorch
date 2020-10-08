package com.github.karlnicholas.djsorch.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.karlnicholas.djsorch.model.Loan;
import com.github.karlnicholas.djsorch.repository.LoanRepository;

@RestController
@RequestMapping("/loan")
public class LoanRestController {
	private final LoanRepository loanRepository;
	public LoanRestController(
			LoanRepository loanRepository 
	) {
		this.loanRepository = loanRepository;
	}
	@GetMapping("transactions")
	public Iterable<Loan> listTransactions() {
		return loanRepository.findAll();
	}
	@GetMapping("count")
	public Long countTransactions() {
		return loanRepository.count();
	}
}
