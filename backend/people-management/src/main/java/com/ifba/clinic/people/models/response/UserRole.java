package com.ifba.clinic.people.models.response;

public record UserRole(
    String role,
    String referencedEntityId
) {}
