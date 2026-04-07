package br.com.filpo.frauddetector.domain.models;

public record Location(
        String city,
        String country,
        double latitude,
        double longitude) {
    public Location {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude deve estar entre -90 e 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude deve estar entre -180 e 180");
        }
    }

    /**
     * Calcula a distância em km até outra localização usando a fórmula de
     * Haversine.
     */
    public double distanceKmTo(Location other) {
        final double R = 6371.0; // Raio da Terra em km
        double dLat = Math.toRadians(other.latitude - this.latitude);
        double dLon = Math.toRadians(other.longitude - this.longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(this.latitude))
                        * Math.cos(Math.toRadians(other.latitude))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}