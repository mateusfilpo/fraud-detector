package br.com.filpo.frauddetector.infrastructure.adapters.in.web.dto;

import br.com.filpo.frauddetector.domain.enums.AccountStatus;
import br.com.filpo.frauddetector.domain.enums.AccountType;
import br.com.filpo.frauddetector.domain.models.Account;
import java.time.LocalDateTime;

public record AccountResponseDTO(
        String accountId,
        String holder,
        AccountType type,
        LocalDateTime createdAt,
        AccountStatus status) {

    public static AccountResponseDTO from(Account account) {
        return new AccountResponseDTO(
                account.getAccountId(),
                account.getHolder(),
                account.getType(),
                account.getCreatedAt(),
                account.getStatus());
    }
}