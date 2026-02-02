package com.ifba.clinic.gateway.model;

public record UserRoleResponse(
    String id,
    String role,
    String referencedEntityId
) {}
