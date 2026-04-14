package br.com.filpo.frauddetector.infrastructure.adapters.in.web.dto;

import br.com.filpo.frauddetector.domain.enums.TransactionChannel;
import java.math.BigDecimal;

public record TransactionRequestDTO(
        BigDecimal amount,
        TransactionChannel channel,
        String senderAccountId,
        String receiverAccountId,
        DeviceDTO device,
        LocationDTO location,
        String merchantId) {

    public record DeviceDTO(String fingerprint, String ip, String userAgent) {
    }

    public record LocationDTO(String city, String country, double latitude, double longitude) {
    }
}