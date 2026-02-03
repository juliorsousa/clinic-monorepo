package com.ifba.clinic.people.models.requests;

import com.ifba.clinic.people.entities.enums.EnumDoctorSpeciality;
import com.ifba.clinic.people.utils.Messages;
import jakarta.validation.constraints.NotNull;

public record UpdateDoctorRequest(

    @NotNull(message = Messages.SPECIALITY_REQUIRED)
    EnumDoctorSpeciality speciality

) {
}