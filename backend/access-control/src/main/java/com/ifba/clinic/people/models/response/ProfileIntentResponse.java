package com.ifba.clinic.people.models.response;

import com.ifba.clinic.people.entities.enums.EnumIntentStatus;
import com.ifba.clinic.people.entities.enums.EnumRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ProfileIntentResponse(
   String id,

   EnumRole type,
   EnumIntentStatus status
) {
}
