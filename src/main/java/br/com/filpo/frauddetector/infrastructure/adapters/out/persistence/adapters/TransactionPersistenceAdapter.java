package br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.adapters;

import br.com.filpo.frauddetector.domain.models.Merchant;
import br.com.filpo.frauddetector.domain.models.Transaction;
import br.com.filpo.frauddetector.domain.ports.out.MerchantRepositoryPort;
import br.com.filpo.frauddetector.domain.ports.out.TransactionRepositoryPort;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities.AccountNode;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities.DeviceNode;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities.LocationNode;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities.TransactionNode;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.mappers.MerchantMapper;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.mappers.TransactionMapper;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.repositories.SpringDataAccountRepository;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.repositories.SpringDataDeviceRepository;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.repositories.SpringDataTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TransactionPersistenceAdapter implements TransactionRepositoryPort {

    private final SpringDataTransactionRepository transactionRepository;
    private final SpringDataAccountRepository accountRepository;
    private final SpringDataDeviceRepository deviceRepository;
    private final MerchantRepositoryPort merchantRepositoryPort;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionNode node = TransactionMapper.toNode(transaction);

        AccountNode sender = accountRepository.findById(transaction.getSenderAccountId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Conta remetente não encontrada: " + transaction.getSenderAccountId()));
        node.setSender(sender);

        AccountNode receiver = accountRepository.findById(transaction.getReceiverAccountId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Conta destinatária não encontrada: " + transaction.getReceiverAccountId()));
        node.setReceiver(receiver);

        if (transaction.getDevice() != null) {
            DeviceNode deviceNode = deviceRepository
                    .findByFingerprint(transaction.getDevice().fingerprint())
                    .orElseGet(() -> TransactionMapper.toDeviceNode(transaction.getDevice()));
            node.setDevice(deviceNode);
        }

        if (transaction.getLocation() != null) {
            LocationNode locationNode = TransactionMapper.toLocationNode(transaction.getLocation());
            node.setLocation(locationNode);
        }

        if (transaction.getMerchantId() != null) {
            Merchant merchant = merchantRepositoryPort
                    .findById(transaction.getMerchantId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Merchant não encontrado: " + transaction.getMerchantId()));
            node.setMerchant(MerchantMapper.toNode(merchant));
        }

        TransactionNode saved = transactionRepository.save(node);

        return TransactionMapper.toDomain(saved);
    }

    @Override
    public Optional<Transaction> findById(String transactionId) {
        return transactionRepository.findWithRelationshipsById(transactionId)
                .map(TransactionMapper::toDomain);
    }

    @Override
    public List<Transaction> findByAccountId(String accountId) {
        List<TransactionNode> sent = transactionRepository.findBySenderAccountId(accountId);
        List<TransactionNode> received = transactionRepository.findByReceiverAccountId(accountId);

        List<TransactionNode> all = new ArrayList<>(sent);
        received.stream()
                .filter(r -> sent.stream().noneMatch(s -> s.getTransactionId().equals(r.getTransactionId())))
                .forEach(all::add);

        return all.stream()
                .map(TransactionMapper::toDomain)
                .toList();
    }
}