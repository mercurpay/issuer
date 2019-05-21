package tech.claudioed.issuer.domain.repository;

import org.springframework.data.repository.CrudRepository;
import tech.claudioed.issuer.domain.Account;

/**
 * @author claudioed on 2019-05-20.
 * Project issuer
 */
public interface AccountRepository extends CrudRepository<Account,String> {

}
