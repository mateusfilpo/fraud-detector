package br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.mappers;

import br.com.filpo.frauddetector.domain.enums.AccountStatus;
import br.com.filpo.frauddetector.domain.enums.AccountType;
import br.com.filpo.frauddetector.domain.models.Account;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities.AccountNode;

public final class AccountMapper {

    private AccountMapper() {
    }

    public static AccountNode toNode(Account account) {
        return AccountNode.builder()
                .accountId(account.getAccountId())
                .holder(account.getHolder())
                .type(account.getType().name())
                .createdAt(account.getCreatedAt())
                .status(account.getStatus().name())
                .build();
    }

    public static Account toDomain(AccountNode node) {
        return new Account(
                node.getAccountId(),
                node.getHolder(),
                AccountType.valueOf(node.getType()),
                node.getCreatedAt(),
                AccountStatus.valueOf(node.getStatus()));
    }
}