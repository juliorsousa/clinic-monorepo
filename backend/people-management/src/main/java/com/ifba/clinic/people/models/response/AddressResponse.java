package com.ifba.clinic.people.models.response;

import com.ifba.clinic.people.entities.Address;
import com.ifba.clinic.people.entities.enums.EnumBrazilState;

public record AddressResponse(
    String street,
    String house,
    String complement,
    String neighborhood,
    String city,
    EnumBrazilState state,
    String zipCode
) {
  public AddressResponse(Address address) {
    this(
        address.getStreet(),
        address.getHouse(),
        address.getComplement(),
        address.getNeighborhood(),
        address.getCity(),
        address.getState(),
        address.getZipCode()
    );
  }
}
