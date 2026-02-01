package com.ifba.clinic.access.models.response;

import lombok.Builder;

@Builder
public record ProfileIntentProcessingResponse(
    String intentId,
    String status,
    String entityId,
    String response
) {
}