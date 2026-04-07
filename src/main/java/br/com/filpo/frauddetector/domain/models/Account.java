package br.com.filpo.frauddetector.domain.models;

import br.com.filpo.frauddetector.domain.enums.AccountStatus;
import br.com.filpo.frauddetector.domain.enums.AccountType;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Account {

    private final String accountId;
    private String holder;
    private AccountType type;
    private LocalDateTime createdAt;
    private AccountStatus status;

    public Account(String holder, AccountType type) {
        this.accountId = UUID.randomUUID().toString();
        this.holder = holder;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.status = AccountStatus.ACTIVE;
    }

    public Account(String accountId, String holder, AccountType type,
            LocalDateTime createdAt, AccountStatus status) {
        this.accountId = accountId;
        this.holder = holder;
        this.type = type;
        this.createdAt = createdAt;
        this.status = status;
    }

    public void suspend() {
        if (this.status == AccountStatus.CLOSED) {
            throw new IllegalStateException("Não é possível suspender uma conta encerrada");
        }
        this.status = AccountStatus.SUSPENDED;
    }

    public void close() {
        this.status = AccountStatus.CLOSED;
    }

    public void reactivate() {
        if (this.status == AccountStatus.CLOSED) {
            throw new IllegalStateException("Não é possível reativar uma conta encerrada");
        }
        this.status = AccountStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == AccountStatus.ACTIVE;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getHolder() {
        return holder;
    }

    public AccountType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public AccountStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Account account = (Account) o;
        return Objects.equals(accountId, account.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId);
    }

    @Override
    public String toString() {
        return "Account{accountId='%s', holder='%s', type=%s, status=%s}"
                .formatted(accountId, holder, type, status);
    }
}