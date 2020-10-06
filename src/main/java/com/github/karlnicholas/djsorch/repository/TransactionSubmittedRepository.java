package com.github.karlnicholas.djsorch.repository;

import org.springframework.data.repository.CrudRepository;

import com.github.karlnicholas.djsorch.model.TransactionSubmitted;

public interface TransactionSubmittedRepository extends CrudRepository<TransactionSubmitted, Long>  {
}
