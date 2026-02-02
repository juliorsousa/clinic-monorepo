package com.ifba.clinic.access.models.response;

import java.util.List;

public record ValidateUserResponse(
    String id,
    String email,

    List<UserRoleResponse> roles,
    List<String> traits
) {}
