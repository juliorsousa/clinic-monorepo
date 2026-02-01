package com.ifba.clinic.access.models;

import com.ifba.clinic.access.models.requests.profiles.ProfileIntentRequest;

public record RunProfileIntentMessage(
    String intentId,
    String userId,
    String type,
    ProfileIntentRequest body,
    String createdAt
) {}
