package com.github.karlnicholas.djsorch.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.github.karlnicholas.djsorch.model.TransactionOpen;
import com.github.karlnicholas.djsorch.model.TransactionType;

public interface TransactionOpenRepository extends CrudRepository<TransactionOpen, Long>  {
	List<TransactionOpen> findByAccountId(Long id);
	Optional<TransactionOpen> findByAccountIdAndTransactionType(Long accountId, TransactionType transactionType);
	
	@Query(value = "select t.* from transactionopen t where t.account_id = :accountId and t.type = 9 and t.id = (select max(id) from transactionopen where account_id = :accountId and type = 9)")
	TransactionOpen fetchLatestBillingCycleForAccount(@Param("accountId") Long accountId);

	@Query(value = "select t.* from transactionopen t where t.type = 9 and t.id not in (select distinct(t1.id) from transactionopen t1, transactionopen t2 where t1.account_id = t2.account_id and t1.type = t2.type and t1.type = 9 and t1.businessDate < t2.businessDate)")
	List<TransactionOpen> fetchLatestBillingCycles();

	@Query(value = "select t.* from transactionopen t where t.transactionDate = :transactionDate and t.type = 9 and t.id  = (select max(id) from transactionopen where type = 9 and transactionDate = :transactionDate)")
	List<TransactionOpen> fetchBillingCyclesForDate(@Param("transactionDate") LocalDate transactionDate);

	@Query(value = "select t.* from transactionopen t where t.account_id = :accountId and t.type = 9 and t.transactionDate = :transactionDate and t.id = (select max(id) from transactionopen where account_id = :accountId and type = 9 and transactionDate = :transactionDate )")
	Optional<TransactionOpen> fetchBillingCycleForAccountAndDate(@Param("accountId") Long accountId, @Param("transactionDate") LocalDate transactionDate);

	List<TransactionOpen> findAllByOrderByTransactionDate();

	List<TransactionOpen> findByAccountIdOrderById(Long id);
}
