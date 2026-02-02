package com.ifba.clinic.people.models.requests;

import com.ifba.clinic.people.entities.enums.EnumDoctorSpeciality;
import com.ifba.clinic.people.models.requests.AddressRequest;
import com.ifba.clinic.people.utils.Messages;
import com.ifba.clinic.people.utils.validation.annotations.Phone;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateDoctorRequest(

    @NotBlank(message = Messages.NAME_REQUIRED)
    @Size(max = 100, message = Messages.NAME_MAX_LENGTH)
    String name,

    @NotBlank(message = Messages.USER_ID_REQUIRED)
    String userId,

    @NotBlank(message = Messages.PHONE_REQUIRED)
    @Size(max = 15, message = Messages.PHONE_MAX_LENGTH)
    @Phone
    String phone,

    @NotBlank(message = Messages.CREDENTIAL_REQUIRED)
    @Size(max = 20, message = Messages.CREDENTIAL_MAX_LENGTH)
    String credential,

    @NotNull(message = Messages.SPECIALITY_REQUIRED)
    EnumDoctorSpeciality speciality,

    @NotNull(message = Messages.ADDRESS_REQUIRED)
    @Valid
    AddressRequest address
) {
}