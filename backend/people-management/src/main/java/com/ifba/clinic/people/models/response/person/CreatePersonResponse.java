package com.ifba.clinic.people.models.response.person;

import com.ifba.clinic.people.entities.Person;
import com.ifba.clinic.people.models.response.AddressResponse;

public record CreatePersonResponse(
    String id,
    String name,
    String document,
    String phone,
    AddressResponse address
) {
  public CreatePersonResponse(Person person) {
    this(
        person.getId(),
        person.getName(),
        person.getDocument(),
        person.getPhone(),
        new AddressResponse(person.getAddress())
    );
  }
}
