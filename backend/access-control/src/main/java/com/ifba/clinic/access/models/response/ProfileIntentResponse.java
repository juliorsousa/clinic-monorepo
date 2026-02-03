package com.ifba.clinic.access.models.response;

import com.ifba.clinic.access.entities.ProfileIntent;
import com.ifba.clinic.access.entities.enums.EnumIntentStatus;
import com.ifba.clinic.access.entities.enums.EnumRole;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ProfileIntentResponse(
    String id,
    String userId,

    EnumRole type,
    EnumIntentStatus status,

    String body,
    String response,

    LocalDateTime createdAt
) {
  public ProfileIntentResponse(ProfileIntent intent) {
    this(
        intent.getId(),
        intent.getUser().getId(),
        intent.getType(),
        intent.getStatus(),
        intent.getBody(),
        intent.getResponse(),
        intent.getCreatedAt()
    );
  }
}
