package com.ifba.clinic.people.models.requests;

import com.ifba.clinic.people.entities.enums.EnumDoctorSpeciality;
import com.ifba.clinic.people.utils.Messages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateDoctorRequest(

    @NotBlank(message = Messages.CREDENTIAL_REQUIRED)
    @Size(max = 20, message = Messages.CREDENTIAL_MAX_LENGTH)
    String credential,

    @NotNull(message = Messages.SPECIALITY_REQUIRED)
    EnumDoctorSpeciality speciality

) {
}