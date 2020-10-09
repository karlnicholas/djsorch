package com.github.karlnicholas.djsorch.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.github.karlnicholas.djsorch.model.AccountClosed;

public interface AccountClosedRepository extends CrudRepository<AccountClosed, Long>{

	Optional<AccountClosed> findByOriginalId(Long originalId);

}
