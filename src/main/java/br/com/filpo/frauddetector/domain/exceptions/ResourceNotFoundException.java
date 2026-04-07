package br.com.filpo.frauddetector.domain.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String resourceId;

    public ResourceNotFoundException(String resourceName, String resourceId) {
        super("%s não encontrado com ID: %s".formatted(resourceName, resourceId));
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getResourceId() {
        return resourceId;
    }
}