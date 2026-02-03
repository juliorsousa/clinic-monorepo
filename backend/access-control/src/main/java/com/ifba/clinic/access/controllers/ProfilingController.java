package com.ifba.clinic.access.controllers;

import com.ifba.clinic.access.models.requests.PageableRequest;
import com.ifba.clinic.access.models.requests.profiles.ProfileIntentRequest;
import com.ifba.clinic.access.models.response.PageResponse;
import com.ifba.clinic.access.models.response.ProfileIntentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @GetMapping("/profile-intents")
  PageResponse<ProfileIntentResponse> listProfileIntents(
      @ParameterObject PageableRequest pageable
  );

  @PostMapping("/profile-intent/{id}/reject")
  ResponseEntity<Void> rejectProfileSetupIntent(
      @PathVariable String id
  );

  @PostMapping("/profile-intent/{id}/approve")
  ResponseEntity<Void> approveProfileSetupIntent(
      @PathVariable String id
  );

}
