package br.com.filpo.frauddetector.infrastructure.adapters.in.web.dto;

import br.com.filpo.frauddetector.domain.enums.AccountType;

public record AccountRequestDTO(
        String holder,
        AccountType type) {
}
