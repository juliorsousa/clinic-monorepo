package com.ifba.clinic.people.models.requests.person;

import com.ifba.clinic.people.models.requests.AddressRequest;
import com.ifba.clinic.people.utils.Messages;
import com.ifba.clinic.people.utils.validation.annotations.Phone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePersonRequest(

    @NotBlank(message = Messages.NAME_REQUIRED)
    @Size(max = 100, message = Messages.NAME_MAX_LENGTH)
    String name,

    @NotBlank(message = Messages.PHONE_REQUIRED)
    @Size(max = 15, message = Messages.PHONE_MAX_LENGTH)
    @Phone
    String phone,

    AddressRequest address
) {}