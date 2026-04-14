package br.com.filpo.frauddetector.infrastructure.adapters.in.web.controllers;

import br.com.filpo.frauddetector.domain.enums.AlertStatus;
import br.com.filpo.frauddetector.domain.ports.in.AlertUseCase;
import br.com.filpo.frauddetector.infrastructure.adapters.in.web.dto.FraudAlertResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class FraudAlertController {

    private final AlertUseCase alertUseCase;

    @GetMapping
    public ResponseEntity<List<FraudAlertResponseDTO>> findAll() {
        return ResponseEntity.ok(alertUseCase.findAll().stream()
                .map(FraudAlertResponseDTO::from).toList());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<FraudAlertResponseDTO>> findByStatus(
            @PathVariable AlertStatus status) {
        return ResponseEntity.ok(alertUseCase.findByStatus(status).stream()
                .map(FraudAlertResponseDTO::from).toList());
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<FraudAlertResponseDTO>> findByAccountId(
            @PathVariable String accountId) {
        return ResponseEntity.ok(alertUseCase.findByAccountId(accountId).stream()
                .map(FraudAlertResponseDTO::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FraudAlertResponseDTO> findById(@PathVariable String id) {
        return ResponseEntity.ok(FraudAlertResponseDTO.from(alertUseCase.findById(id)));
    }

    @PatchMapping("/{id}/review")
    public ResponseEntity<Void> markUnderReview(@PathVariable String id) {
        alertUseCase.markUnderReview(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmFraud(@PathVariable String id) {
        alertUseCase.confirmFraud(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/dismiss")
    public ResponseEntity<Void> dismiss(@PathVariable String id) {
        alertUseCase.dismiss(id);
        return ResponseEntity.noContent().build();
    }
}