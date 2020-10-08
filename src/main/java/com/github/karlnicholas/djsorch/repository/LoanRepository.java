package com.github.karlnicholas.djsorch.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.github.karlnicholas.djsorch.model.Loan;

public interface LoanRepository extends CrudRepository<Loan, Long>  {
	Optional<Loan> findByAccountId(Long accountId);
}
