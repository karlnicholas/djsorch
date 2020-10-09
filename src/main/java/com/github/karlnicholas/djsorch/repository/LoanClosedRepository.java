package com.github.karlnicholas.djsorch.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.github.karlnicholas.djsorch.model.AccountClosed;
import com.github.karlnicholas.djsorch.model.LoanClosed;

public interface LoanClosedRepository extends CrudRepository<LoanClosed, Long>{
	@Modifying
	@Query("delete from LoanClosed l where l.accountClosed.id = :accountId")
	void deleteByAccountId(@Param("accountId")Long accountId);

	Optional<LoanClosed> findByAccountClosed(AccountClosed accountClosed);

	Optional<LoanClosed> findByAccountClosedOriginalId(Long originalId);

	Optional<LoanClosed> findByAccountClosedId(Long id);
}
