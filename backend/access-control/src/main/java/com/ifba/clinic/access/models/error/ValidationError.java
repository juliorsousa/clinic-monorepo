package com.ifba.clinic.access.models.error;

import java.util.Map;

public record ValidationError(
    String message,
    Map<String, String> errors
) {}