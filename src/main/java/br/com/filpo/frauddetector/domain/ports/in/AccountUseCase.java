package br.com.filpo.frauddetector.domain.ports.in;

import br.com.filpo.frauddetector.domain.enums.AccountType;
import br.com.filpo.frauddetector.domain.models.Account;
import java.util.List;

public interface AccountUseCase {
    Account createAccount(String holder, AccountType type);

    Account findById(String accountId);

    List<Account> findAll();

    void suspendAccount(String accountId);

    void closeAccount(String accountId);
}