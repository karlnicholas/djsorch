package com.github.karlnicholas.djsorch.repository;

import org.springframework.data.repository.CrudRepository;

import com.github.karlnicholas.djsorch.model.Transaction;

public interface TransactionRejectedRepository extends CrudRepository<Transaction	, Long> {}
