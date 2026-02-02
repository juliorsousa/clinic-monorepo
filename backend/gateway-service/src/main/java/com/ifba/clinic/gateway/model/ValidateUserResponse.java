package com.ifba.clinic.gateway.model;

import java.util.List;

public record ValidateUserResponse(
    String id,
    String email,

    List<UserRoleResponse> roles,
    List<String> traits
) {}

