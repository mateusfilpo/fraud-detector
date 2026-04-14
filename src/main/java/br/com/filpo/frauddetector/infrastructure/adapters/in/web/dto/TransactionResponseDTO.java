package br.com.filpo.frauddetector.infrastructure.adapters.in.web.dto;

import br.com.filpo.frauddetector.domain.enums.TransactionChannel;
import br.com.filpo.frauddetector.domain.models.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDTO(
        String transactionId,
        BigDecimal amount,
        LocalDateTime timestamp,
        TransactionChannel channel,
        String senderAccountId,
        String receiverAccountId,
        String merchantId) {

    public static TransactionResponseDTO from(Transaction tx) {
        return new TransactionResponseDTO(
                tx.getTransactionId(),
                tx.getAmount(),
                tx.getTimestamp(),
                tx.getChannel(),
                tx.getSenderAccountId(),
                tx.getReceiverAccountId(),
                tx.getMerchantId());
    }
}