package br.com.filpo.frauddetector.application.services;

import br.com.filpo.frauddetector.domain.enums.TransactionChannel;
import br.com.filpo.frauddetector.domain.exceptions.BusinessRuleException;
import br.com.filpo.frauddetector.domain.exceptions.ResourceNotFoundException;
import br.com.filpo.frauddetector.domain.models.Account;
import br.com.filpo.frauddetector.domain.models.Device;
import br.com.filpo.frauddetector.domain.models.Location;
import br.com.filpo.frauddetector.domain.models.Transaction;
import br.com.filpo.frauddetector.domain.ports.in.TransactionUseCase;
import br.com.filpo.frauddetector.domain.ports.in.FraudDetectionUseCase;
import br.com.filpo.frauddetector.domain.ports.out.AccountRepositoryPort;
import br.com.filpo.frauddetector.domain.ports.out.MerchantRepositoryPort;
import br.com.filpo.frauddetector.domain.ports.out.TransactionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService implements TransactionUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;
    private final AccountRepositoryPort accountRepositoryPort;
    private final MerchantRepositoryPort merchantRepositoryPort;
    private final FraudDetectionUseCase fraudDetectionUseCase;

    @Override
    @Transactional
    public Transaction createTransaction(BigDecimal amount, TransactionChannel channel,
            String senderAccountId, String receiverAccountId,
            Device device, Location location, String merchantId) {
        Account sender = accountRepositoryPort.findById(senderAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", senderAccountId));
        if (!sender.isActive()) {
            throw new BusinessRuleException(
                    "Conta remetente não está ativa: " + senderAccountId);
        }

        Account receiver = accountRepositoryPort.findById(receiverAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", receiverAccountId));
        if (!receiver.isActive()) {
            throw new BusinessRuleException(
                    "Conta destinatária não está ativa: " + receiverAccountId);
        }

        if (senderAccountId.equals(receiverAccountId)) {
            throw new BusinessRuleException(
                    "Remetente e destinatário não podem ser a mesma conta");
        }

        if (merchantId != null && !merchantRepositoryPort.existsById(merchantId)) {
            throw new ResourceNotFoundException("Merchant", merchantId);
        }

        Transaction transaction = new Transaction(amount, channel, senderAccountId, receiverAccountId);
        transaction.setDevice(device);
        transaction.setLocation(location);
        transaction.setMerchantId(merchantId);

        Transaction savedTransaction = transactionRepositoryPort.save(transaction);

        fraudDetectionUseCase.analyzeTransaction(savedTransaction.getTransactionId());

        return savedTransaction;
    }

    @Override
    public Transaction findById(String transactionId) {
        return transactionRepositoryPort.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", transactionId));
    }

    @Override
    public List<Transaction> findByAccountId(String accountId) {
        return transactionRepositoryPort.findByAccountId(accountId);
    }
}