package br.com.filpo.frauddetector.domain.models;

public record Device(
        String fingerprint,
        String ip,
        String userAgent) {
    public Device {
        if (fingerprint == null || fingerprint.isBlank()) {
            throw new IllegalArgumentException("Fingerprint do dispositivo não pode ser vazio");
        }
    }
}