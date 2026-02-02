package com.ifba.clinic.access.messaging.roles.models;

public record UserRoleDroppedEvent(
    String entityId,
    String role
) {}