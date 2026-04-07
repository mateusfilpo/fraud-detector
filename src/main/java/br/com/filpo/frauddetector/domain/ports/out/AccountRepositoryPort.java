package br.com.filpo.frauddetector.domain.ports.out;

import br.com.filpo.frauddetector.domain.models.Account;
import java.util.List;
import java.util.Optional;

public interface AccountRepositoryPort {
    Account save(Account account);

    Optional<Account> findById(String accountId);

    List<Account> findAll();

    boolean existsById(String accountId);
}