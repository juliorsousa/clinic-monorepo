package com.ifba.clinic.access.models.requests;

import com.ifba.clinic.access.utils.Messages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

    @NotBlank(message = Messages.EMAIL_REQUIRED)
    @Size(min = 6, max = 100, message = Messages.EMAIL_LENGTH)
    @Email(message = Messages.EMAIL_INVALID)
    String email,

    @NotBlank(message = Messages.PASSWORD_REQUIRED)
    @Size(min = 6, max = 60, message = Messages.PASSWORD_LENGTH)
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$",
        message = Messages.PASSWORD_DONT_MATCH_CRITERIA
    )
    String password
) {}