package com.ifba.clinic.people.controllers.impl;

import com.ifba.clinic.people.controllers.ProfilingController;
import com.ifba.clinic.people.models.requests.profiles.ProfileIntentRequest;
import com.ifba.clinic.people.models.response.ProfileIntentResponse;
import com.ifba.clinic.people.services.ProfilingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProfilingControllerImpl implements ProfilingController {

  private final ProfilingService profilingService;

  public ResponseEntity<ProfileIntentResponse> getProfileSetupIntent() {
    return ResponseEntity
        .ok()
        .body(
            profilingService.getCurrentProfileIntent()
        );
  }

  public ResponseEntity<ProfileIntentResponse> createProfileSetupIntent(ProfileIntentRequest request) {
    var setupIntent = profilingService.createProfileIntent(request);

    return ResponseEntity
        .accepted()
        .body(setupIntent);
  }

}
