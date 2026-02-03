package com.ifba.clinic.people.models.response.person;

import com.ifba.clinic.people.entities.Person;
import com.ifba.clinic.people.models.requests.AddressRequest;

public record GetPersonResponse(
    String id,
    String name,
    String document,
    String phone,
    AddressRequest address
) {
  public static GetPersonResponse from(Person person) {
    return new GetPersonResponse(
        person.getId(),
        person.getName(),
        person.getDocument(),
        person.getPhone(),
        AddressRequest.fromAddress(person.getAddress())
    );
  }
}
