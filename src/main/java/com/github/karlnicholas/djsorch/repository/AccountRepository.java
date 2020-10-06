package com.github.karlnicholas.djsorch.repository;

import org.springframework.data.repository.CrudRepository;

import com.github.karlnicholas.djsorch.model.Account;

public interface AccountRepository extends CrudRepository<Account, Long>  {
}
