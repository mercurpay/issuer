package tech.claudioed.issuer.domain.repository;

import org.springframework.data.repository.CrudRepository;
import tech.claudioed.issuer.domain.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, String> {}
