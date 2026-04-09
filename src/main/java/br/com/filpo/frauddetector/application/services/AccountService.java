package br.com.filpo.frauddetector.application.services;

import br.com.filpo.frauddetector.domain.enums.AccountType;
import br.com.filpo.frauddetector.domain.exceptions.ResourceNotFoundException;
import br.com.filpo.frauddetector.domain.models.Account;
import br.com.filpo.frauddetector.domain.ports.in.AccountUseCase;
import br.com.filpo.frauddetector.domain.ports.out.AccountRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService implements AccountUseCase {

    private final AccountRepositoryPort accountRepositoryPort;

    @Override
    @Transactional
    public Account createAccount(String holder, AccountType type) {
        Account account = new Account(holder, type);
        return accountRepositoryPort.save(account);
    }

    @Override
    public Account findById(String accountId) {
        return accountRepositoryPort.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
    }

    @Override
    public List<Account> findAll() {
        return accountRepositoryPort.findAll();
    }

    @Override
    @Transactional
    public void suspendAccount(String accountId) {
        Account account = findById(accountId);
        account.suspend();
        accountRepositoryPort.save(account);
    }

    @Override
    @Transactional
    public void closeAccount(String accountId) {
        Account account = findById(accountId);
        account.close();
        accountRepositoryPort.save(account);
    }
}