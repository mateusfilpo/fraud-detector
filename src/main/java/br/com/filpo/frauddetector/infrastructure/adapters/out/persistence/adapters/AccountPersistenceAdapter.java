package br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.adapters;

import br.com.filpo.frauddetector.domain.models.Account;
import br.com.filpo.frauddetector.domain.ports.out.AccountRepositoryPort;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities.AccountNode;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.mappers.AccountMapper;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.repositories.SpringDataAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements AccountRepositoryPort {

    private final SpringDataAccountRepository repository;

    @Override
    public Account save(Account account) {
        AccountNode node = AccountMapper.toNode(account);
        AccountNode saved = repository.save(node);
        return AccountMapper.toDomain(saved);
    }

    @Override
    public Optional<Account> findById(String accountId) {
        return repository.findById(accountId)
                .map(AccountMapper::toDomain);
    }

    @Override
    public List<Account> findAll() {
        return repository.findAll().stream()
                .map(AccountMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(String accountId) {
        return repository.existsById(accountId);
    }
}