package com.ifba.clinic.people.messaging.intents.models.responses;

public record ProfileIntentResponse(
    String intentId,
    String status,
    String entityId,
    String response
) {}