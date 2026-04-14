package br.com.filpo.frauddetector.infrastructure.adapters.in.web.controllers;

import br.com.filpo.frauddetector.domain.models.Account;
import br.com.filpo.frauddetector.domain.ports.in.AccountUseCase;
import br.com.filpo.frauddetector.infrastructure.adapters.in.web.dto.AccountRequestDTO;
import br.com.filpo.frauddetector.infrastructure.adapters.in.web.dto.AccountResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountUseCase accountUseCase;

    @PostMapping
    public ResponseEntity<AccountResponseDTO> create(@RequestBody AccountRequestDTO request) {
        Account account = accountUseCase.createAccount(request.holder(), request.type());
        return ResponseEntity.status(HttpStatus.CREATED).body(AccountResponseDTO.from(account));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> findById(@PathVariable String id) {
        Account account = accountUseCase.findById(id);
        return ResponseEntity.ok(AccountResponseDTO.from(account));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> findAll() {
        List<AccountResponseDTO> accounts = accountUseCase.findAll().stream()
                .map(AccountResponseDTO::from)
                .toList();
        return ResponseEntity.ok(accounts);
    }

    @PatchMapping("/{id}/suspend")
    public ResponseEntity<Void> suspend(@PathVariable String id) {
        accountUseCase.suspendAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<Void> close(@PathVariable String id) {
        accountUseCase.closeAccount(id);
        return ResponseEntity.noContent().build();
    }
}