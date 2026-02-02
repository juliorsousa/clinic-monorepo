package com.ifba.clinic.people.messaging.roles.models;

public record UserRoleDroppedEvent(
    String entityId,
    String role
) {
}