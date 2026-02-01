package com.ifba.clinic.access.models.response;


public record TokenResponse(
    String accessToken,
    Long expiresIn
) {
  public TokenResponse(String token) {
    this(
        token,
        3600L
    );
  }
}
