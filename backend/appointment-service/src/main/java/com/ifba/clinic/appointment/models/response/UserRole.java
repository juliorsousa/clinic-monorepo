package com.ifba.clinic.appointment.models.response;

public record UserRole(
    String role,
    String referencedEntityId
) {}
