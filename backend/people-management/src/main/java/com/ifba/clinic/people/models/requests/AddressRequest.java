package com.ifba.clinic.people.models.requests;

import com.ifba.clinic.people.entities.Address;
import com.ifba.clinic.people.entities.enums.EnumBrazilState;
import com.ifba.clinic.people.utils.Messages;
import com.ifba.clinic.people.utils.validation.annotations.CEP;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddressRequest(

    @NotBlank(message = Messages.STREET_REQUIRED)
    @Size(max = 80, message = Messages.STREET_MAX_LENGTH)
    String street,

    @Size(max = 10, message = Messages.HOUSE_MAX_LENGTH)
    String house,

    @Size(max = 50, message = Messages.COMPLEMENT_MAX_LENGTH)
    String complement,

    @NotBlank(message = Messages.NEIGHBORHOOD_REQUIRED)
    @Size(max = 50, message = Messages.NEIGHBORHOOD_MAX_LENGTH)
    String neighborhood,

    @NotBlank(message = Messages.CITY_REQUIRED)
    @Size(max = 50, message = Messages.CITY_MAX_LENGTH)
    String city,

    @NotNull(message = Messages.STATE_REQUIRED)
    EnumBrazilState state,

    @NotBlank(message = Messages.ZIP_CODE_REQUIRED)
    @CEP
    String zipCode
) {

  public static AddressRequest fromAddress(Address address) {
    if (address == null) return null;

    return new AddressRequest(
        address.getStreet(),
        address.getHouse(),
        address.getComplement(),
        address.getNeighborhood(),
        address.getCity(),
        address.getState(),
        address.getZipCode()
    );
  }

  public static AddressRequest of(
      String street,
      String house,
      String complement,
      String neighborhood,
      String city,
      EnumBrazilState state,
      String zipCode
  ) {
    return new AddressRequest(
        street,
        house,
        complement,
        neighborhood,
        city,
        state,
        zipCode
    );
  }
}

