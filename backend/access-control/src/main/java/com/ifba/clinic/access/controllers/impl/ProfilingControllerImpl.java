package com.ifba.clinic.access.controllers.impl;

import com.ifba.clinic.access.controllers.ProfilingController;
import com.ifba.clinic.access.models.requests.PageableRequest;
import com.ifba.clinic.access.models.requests.profiles.ProfileIntentRequest;
import com.ifba.clinic.access.models.response.PageResponse;
import com.ifba.clinic.access.models.response.ProfileIntentResponse;
import com.ifba.clinic.access.services.ProfilingService;
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

  @Override
  public PageResponse<ProfileIntentResponse> listProfileIntents(PageableRequest pageable) {
    return profilingService.listProfileIntents(pageable);
  }

  @Override
  public ResponseEntity<Void> approveProfileSetupIntent(String id) {
    profilingService.approveProfileIntent(id);

    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> rejectProfileSetupIntent(String id) {
    profilingService.rejectProfileIntent(id);

    return ResponseEntity.noContent().build();
  }
}
