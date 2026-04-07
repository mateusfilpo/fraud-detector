package br.com.filpo.frauddetector.domain.models;

import br.com.filpo.frauddetector.domain.enums.TransactionChannel;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Transaction {

    private final String transactionId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private TransactionChannel channel;

    private String senderAccountId;
    private String receiverAccountId;
    private Device device;
    private Location location;
    private String merchantId;

    public Transaction(BigDecimal amount, TransactionChannel channel,
            String senderAccountId, String receiverAccountId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transação deve ser positivo");
        }
        this.transactionId = UUID.randomUUID().toString();
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.channel = channel;
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
    }

    public Transaction(String transactionId, BigDecimal amount, LocalDateTime timestamp,
            TransactionChannel channel, String senderAccountId,
            String receiverAccountId, Device device, Location location,
            String merchantId) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.channel = channel;
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.device = device;
        this.location = location;
        this.merchantId = merchantId;
    }

    public boolean isHighValue() {
        return amount.compareTo(new BigDecimal("10000")) >= 0;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public TransactionChannel getChannel() {
        return channel;
    }

    public String getSenderAccountId() {
        return senderAccountId;
    }

    public String getReceiverAccountId() {
        return receiverAccountId;
    }

    public Device getDevice() {
        return device;
    }

    public Location getLocation() {
        return location;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        return "Transaction{id='%s', amount=%s, channel=%s, from='%s', to='%s'}"
                .formatted(transactionId, amount, channel, senderAccountId, receiverAccountId);
    }
}