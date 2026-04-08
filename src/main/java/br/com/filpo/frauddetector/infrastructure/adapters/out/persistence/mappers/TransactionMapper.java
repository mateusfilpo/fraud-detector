package br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.mappers;

import br.com.filpo.frauddetector.domain.enums.TransactionChannel;
import br.com.filpo.frauddetector.domain.models.Device;
import br.com.filpo.frauddetector.domain.models.Location;
import br.com.filpo.frauddetector.domain.models.Transaction;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities.DeviceNode;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities.LocationNode;
import br.com.filpo.frauddetector.infrastructure.adapters.out.persistence.entities.TransactionNode;

public final class TransactionMapper {

    private TransactionMapper() {
    }

    /**
     * Converte Transaction do domínio para TransactionNode.
     * NÃO preenche os relacionamentos (@Relationship) — isso é feito no adapter.
     */
    public static TransactionNode toNode(Transaction transaction) {
        return TransactionNode.builder()
                .transactionId(transaction.getTransactionId())
                .amount(transaction.getAmount())
                .timestamp(transaction.getTimestamp())
                .channel(transaction.getChannel().name())
                .build();
    }

    /**
     * Converte TransactionNode (com relacionamentos carregados) para Transaction do
     * domínio.
     */
    public static Transaction toDomain(TransactionNode node) {
        Device device = node.getDevice() != null ? toDeviceDomain(node.getDevice()) : null;
        Location location = node.getLocation() != null ? toLocationDomain(node.getLocation()) : null;

        return new Transaction(
                node.getTransactionId(),
                node.getAmount(),
                node.getTimestamp(),
                TransactionChannel.valueOf(node.getChannel()),
                node.getSender() != null ? node.getSender().getAccountId() : null,
                node.getReceiver() != null ? node.getReceiver().getAccountId() : null,
                device,
                location,
                node.getMerchant() != null ? node.getMerchant().getMerchantId() : null);
    }

    public static DeviceNode toDeviceNode(Device device) {
        return DeviceNode.builder()
                .fingerprint(device.fingerprint())
                .ip(device.ip())
                .userAgent(device.userAgent())
                .build();
    }

    public static Device toDeviceDomain(DeviceNode node) {
        return new Device(node.getFingerprint(), node.getIp(), node.getUserAgent());
    }

    public static LocationNode toLocationNode(Location location) {
        return LocationNode.builder()
                .city(location.city())
                .country(location.country())
                .latitude(location.latitude())
                .longitude(location.longitude())
                .build();
    }

    public static Location toLocationDomain(LocationNode node) {
        return new Location(node.getCity(), node.getCountry(),
                node.getLatitude(), node.getLongitude());
    }
}