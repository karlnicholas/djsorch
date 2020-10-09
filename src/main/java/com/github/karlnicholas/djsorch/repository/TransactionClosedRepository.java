package com.github.karlnicholas.djsorch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.github.karlnicholas.djsorch.model.TransactionClosed;
import com.github.karlnicholas.djsorch.model.TransactionType;

public interface TransactionClosedRepository extends CrudRepository<TransactionClosed, Long> {

	List<TransactionClosed> findByAccountClosedId(Long id);

	Optional<TransactionClosed> findByTypeAndAccountClosedId(TransactionType loanFunding, Long fromString);

	@Query(value = "select t.* from transactionclosed t join accountclosed ac where t.accountClosed_id = ac.id and ac.originalId = :accountClosedId and t.type = 9 and t.id = (select max(t2.id) from transactionclosed t2 join accountclosed ac2 where t2.accountClosed_id = ac2.id and ac2.originalId = :accountClosedId and t2.type = 9)")
	TransactionClosed fetchLatestBillingCycleForAccount(@Param("accountClosedId") Long accountClosedId);

}
