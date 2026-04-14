package br.com.filpo.frauddetector.infrastructure.adapters.in.web.controllers;

import br.com.filpo.frauddetector.domain.models.Device;
import br.com.filpo.frauddetector.domain.models.Location;
import br.com.filpo.frauddetector.domain.models.Transaction;
import br.com.filpo.frauddetector.domain.ports.in.FraudDetectionUseCase;
import br.com.filpo.frauddetector.domain.ports.in.TransactionUseCase;
import br.com.filpo.frauddetector.infrastructure.adapters.in.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionUseCase transactionUseCase;
    private final FraudDetectionUseCase fraudDetectionUseCase;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> create(@RequestBody TransactionRequestDTO request) {
        Device device = request.device() != null
                ? new Device(request.device().fingerprint(), request.device().ip(),
                        request.device().userAgent())
                : null;
        Location location = request.location() != null
                ? new Location(request.location().city(), request.location().country(),
                        request.location().latitude(), request.location().longitude())
                : null;

        Transaction tx = transactionUseCase.createTransaction(
                request.amount(), request.channel(),
                request.senderAccountId(), request.receiverAccountId(),
                device, location, request.merchantId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TransactionResponseDTO.from(tx));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> findById(@PathVariable String id) {
        Transaction tx = transactionUseCase.findById(id);
        return ResponseEntity.ok(TransactionResponseDTO.from(tx));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponseDTO>> findByAccountId(
            @PathVariable String accountId) {
        List<TransactionResponseDTO> txs = transactionUseCase.findByAccountId(accountId).stream()
                .map(TransactionResponseDTO::from)
                .toList();
        return ResponseEntity.ok(txs);
    }

    @GetMapping("/{id}/fraud-score")
    public ResponseEntity<FraudScoreResponseDTO> getFraudScore(@PathVariable String id) {
        return ResponseEntity.ok(
                FraudScoreResponseDTO.from(fraudDetectionUseCase.calculateFraudScore(id)));
    }
}