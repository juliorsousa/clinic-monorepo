package com.ifba.clinic.access.models.response;

import com.ifba.clinic.access.entities.ProfileIntent;
import com.ifba.clinic.access.entities.enums.EnumIntentStatus;
import com.ifba.clinic.access.entities.enums.EnumRole;
import lombok.Builder;

@Builder
public record ProfileIntentResponse(
   String id,

   EnumRole type,
   EnumIntentStatus status,

   String response
) {
  public ProfileIntentResponse(ProfileIntent intent) {
    this(
        intent.getId(),
        intent.getType(),
        intent.getStatus(),
        intent.getResponse()
    );
  }
}
