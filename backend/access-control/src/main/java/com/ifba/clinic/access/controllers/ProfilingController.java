package com.ifba.clinic.access.controllers;

import com.ifba.clinic.access.models.requests.profiles.ProfileIntentRequest;
import com.ifba.clinic.access.models.response.ProfileIntentResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/profiling")
@SecurityRequirement(name = "bearerAuth")
public interface ProfilingController {

  @GetMapping("/profile-intent")
  ResponseEntity<ProfileIntentResponse> getProfileSetupIntent();

  @PostMapping("/profile-intent")
  ResponseEntity<ProfileIntentResponse> createProfileSetupIntent(@Valid @RequestBody ProfileIntentRequest request);

}
