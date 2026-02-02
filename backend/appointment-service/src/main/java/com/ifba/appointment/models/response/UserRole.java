package com.ifba.appointment.models.response;

public record UserRole(
    String role,
    String referencedEntityId
) {}
