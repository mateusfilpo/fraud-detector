package br.com.filpo.frauddetector.domain.ports.in;

import br.com.filpo.frauddetector.domain.enums.TransactionChannel;
import br.com.filpo.frauddetector.domain.models.Device;
import br.com.filpo.frauddetector.domain.models.Location;
import br.com.filpo.frauddetector.domain.models.Transaction;
import java.math.BigDecimal;
import java.util.List;

public interface TransactionUseCase {
    Transaction createTransaction(BigDecimal amount, TransactionChannel channel,
            String senderAccountId, String receiverAccountId,
            Device device, Location location, String merchantId);

    Transaction findById(String transactionId);

    List<Transaction> findByAccountId(String accountId);
}