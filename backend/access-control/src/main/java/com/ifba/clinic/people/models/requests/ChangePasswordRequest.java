package com.ifba.clinic.people.models.requests;

import com.ifba.clinic.people.utils.Messages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
    @NotBlank(message = Messages.PASSWORD_REQUIRED)
    @Size(min = 6, max = 60, message = Messages.PASSWORD_LENGTH)
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$",
        message = Messages.PASSWORD_DONT_MATCH_CRITERIA
    )
    String oldPassword,

    @NotBlank(message = Messages.PASSWORD_REQUIRED)
    @Size(min = 6, max = 60, message = Messages.PASSWORD_LENGTH)
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$",
        message = Messages.PASSWORD_DONT_MATCH_CRITERIA
    )
    String newPassword
) {}