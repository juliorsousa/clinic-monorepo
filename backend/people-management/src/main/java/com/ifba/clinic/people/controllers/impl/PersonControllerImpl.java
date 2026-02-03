package com.ifba.clinic.people.controllers.impl;

import com.ifba.clinic.people.controllers.PersonController;
import com.ifba.clinic.people.models.requests.PageableRequest;
import com.ifba.clinic.people.models.requests.person.CreatePersonRequest;
import com.ifba.clinic.people.models.requests.person.UpdatePersonRequest;
import com.ifba.clinic.people.models.response.PageResponse;
import com.ifba.clinic.people.models.response.person.CreatePersonResponse;
import com.ifba.clinic.people.models.response.person.GetPersonResponse;
import com.ifba.clinic.people.services.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PersonControllerImpl implements PersonController {

  private final PersonService personService;

  @Override
  public PageResponse<GetPersonResponse> listPersons(PageableRequest pageable) {
    return personService.listPersons(pageable);
  }

  @Override
  public GetPersonResponse getPersonById(String id) {
    return personService.getPersonById(id);
  }

  @Override
  public GetPersonResponse getPersonByUserId(String id) {
    return personService.getPersonByUserId(id);
  }

  @Override
  public ResponseEntity<Void> updatePerson(String id, UpdatePersonRequest request) {
    personService.updatePerson(id, request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .build();
  }

  @Override
  public ResponseEntity<Void> deletePerson(String id) {
    personService.deletePerson(id);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }
}
