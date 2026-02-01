package com.ifba.clinic.people.models.requests.profiles;

import com.ifba.clinic.people.entities.enums.EnumRole;
import com.ifba.clinic.people.utils.validation.groups.DoctorGroup;
import com.ifba.clinic.people.utils.validation.annotations.ValidProfileSpecific;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@ValidProfileSpecific
public record ProfileIntentRequest(
    @Valid
    Profile profile,

    @Valid
    Personal personal,

    @Valid
    Specific specific
) {

  public record Profile(
      @NotBlank(message = "O perfil é obrigatório.")
      @Pattern(
          regexp = "^(PATIENT|DOCTOR)$",
          message = "O perfil deve ser 'PATIENT' ou 'DOCTOR'."
      )
      String value
  ) {
    public EnumRole toEnum() {
      return EnumRole.valueOf(this.value);
    }
  }

  public record Personal(

      @Valid
      PersonalData personal,

      @Valid
      Address address
  ) {
  }

  public record PersonalData(

      @NotBlank(message = "O nome completo é obrigatório.")
      @Size(max = 100, message = "O nome completo deve ter no máximo 100 caracteres.")
      String name,

      @NotBlank(message = "O documento é obrigatório.")
      @Size(max = 20, message = "O documento deve ter no máximo 20 caracteres.")
      @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter 11 dígitos numéricos.")
      String document,

      @NotBlank(message = "O telefone é obrigatório.")
      @Size(max = 15, message = "O telefone deve ter no máximo 15 caracteres.")
      @Pattern(regexp = "^\\d{2}9?\\d{8}$", message = "Telefone inválido.")
      String phone
  ) {
  }

  public record Address(

      @NotBlank(message = "A rua é obrigatória.")
      @Size(max = 100, message = "A rua deve ter no máximo 100 caracteres.")
      String street,

      @NotBlank(message = "O número da casa é obrigatório.")
      @Size(max = 10, message = "O número da casa deve ter no máximo 10 caracteres.")
      String house,

      @Size(max = 50, message = "O complemento deve ter no máximo 50 caracteres.")
      String complement,

      @NotBlank(message = "O bairro é obrigatório.")
      @Size(max = 50, message = "O bairro deve ter no máximo 50 caracteres.")
      String neighborhood,

      @NotBlank(message = "A cidade é obrigatória.")
      @Size(max = 50, message = "A cidade deve ter no máximo 50 caracteres.")
      String city,

      @NotBlank(message = "O estado é obrigatório.")
      String state,

      @NotBlank(message = "O CEP é obrigatório.")
      @Pattern(regexp = "^\\d{5}-\\d{3}$", message = "CEP inválido.")
      String zipCode
  ) {
  }

  public record Specific(

      @NotBlank(groups = DoctorGroup.class, message = "A credencial é obrigatória.")
      @Pattern(
          groups = DoctorGroup.class,
          regexp = "^CRM?\\s*[-/]?\\s*\\d{4,7}(\\s*[-\\/]\\s*[A-Z]{2})?$|^CRM\\s*[-/]?\\s*[A-Z]{2}\\s+\\d{4,7}$",
          message = "O formato da credencial é inválido."
      )
      String credential,

      @NotBlank(groups = DoctorGroup.class, message = "Selecione uma especialidade.")
      String specialty


  ) {
  }
}
