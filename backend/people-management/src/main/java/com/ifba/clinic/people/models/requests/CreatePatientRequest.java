package com.ifba.clinic.people.models.requests;

import com.ifba.clinic.people.utils.Messages;
import com.ifba.clinic.people.utils.validation.annotations.CPF;
import com.ifba.clinic.people.utils.validation.annotations.Phone;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreatePatientRequest(

    @NotBlank(message = Messages.NAME_REQUIRED)
    @Size(max = 100, message = Messages.NAME_MAX_LENGTH)
    String name,

    @NotBlank(message = Messages.DOCUMENT_REQUIRED)
    @Size(max = 20, message = Messages.DOCUMENT_MAX_LENGTH)
    @CPF
    String document,

    @NotBlank(message = Messages.PHONE_REQUIRED)
    @Size(max = 15, message = Messages.PHONE_MAX_LENGTH)
    @Phone
    String phone,

    @Valid
    AddressRequest address
) {
}