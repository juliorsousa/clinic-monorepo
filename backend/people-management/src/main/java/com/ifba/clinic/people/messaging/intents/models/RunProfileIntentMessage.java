package com.ifba.clinic.people.messaging.intents.models;

import com.ifba.clinic.people.messaging.intents.models.requests.ProfileIntentRequest;

public record RunProfileIntentMessage(
    String intentId,
    String userId,
    String type,
    ProfileIntentRequest body,
    String createdAt
) {}
