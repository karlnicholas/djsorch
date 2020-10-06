package com.github.karlnicholas.djsorch.repository;

import org.springframework.data.repository.CrudRepository;

import com.github.karlnicholas.djsorch.model.TransactionOpen;

public interface TransactionOpenRepository extends CrudRepository<TransactionOpen, Long>  {
}
