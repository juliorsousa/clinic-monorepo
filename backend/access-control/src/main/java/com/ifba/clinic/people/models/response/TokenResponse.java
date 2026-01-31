package com.ifba.clinic.people.models.response;


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
