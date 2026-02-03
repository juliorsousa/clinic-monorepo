package com.ifba.clinic.people.services;

import com.ifba.clinic.people.entities.Address;
import com.ifba.clinic.people.entities.Person;
import com.ifba.clinic.people.exceptions.ConflictException;
import com.ifba.clinic.people.exceptions.NotFoundException;
import com.ifba.clinic.people.messaging.roles.models.UserRoleDroppedEvent;
import com.ifba.clinic.people.messaging.roles.producers.UserRoleProducer;
import com.ifba.clinic.people.models.requests.PageableRequest;
import com.ifba.clinic.people.models.requests.person.CreatePersonRequest;
import com.ifba.clinic.people.models.requests.person.UpdatePersonRequest;
import com.ifba.clinic.people.models.response.PageResponse;
import com.ifba.clinic.people.models.response.person.GetPersonResponse;
import com.ifba.clinic.people.repositories.AddressRepository;
import com.ifba.clinic.people.repositories.PersonRepository;
import com.ifba.clinic.people.security.annotations.AuthRequired;
import com.ifba.clinic.people.security.annotations.RoleRestricted;
import com.ifba.clinic.people.security.components.AuthorizationComponent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static com.ifba.clinic.people.utils.Messages.PERSON_DUPLICATED;
import static com.ifba.clinic.people.utils.Messages.PERSON_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {

  private final PersonRepository personRepository;
  private final AddressRepository addressRepository;

  private final UserRoleProducer userRoleProducer;
  private final AuthorizationComponent authorizationComponent;

  @AuthRequired
  @RoleRestricted("ADMIN")
  public PageResponse<GetPersonResponse> listPersons(PageableRequest pageableRequest) {

    Pageable pageable = PageRequest.of(
        pageableRequest.page(),
        pageableRequest.size(),
        Sort.by("name").ascending()
    );

    Page<GetPersonResponse> personPage =
        personRepository.findAll(pageable)
            .map(GetPersonResponse::from);

    return PageResponse.from(personPage);
  }

  @AuthRequired
  public GetPersonResponse getPersonById(String id) {

    log.info("Fetching person with id: {}", id);

    Person person = personRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(PERSON_NOT_FOUND));

    if (!authorizationComponent.hasPermissionToManageResource(person.getUserId())) {
      throw new NotFoundException(PERSON_NOT_FOUND);
    }

    return GetPersonResponse.from(person);
  }

  @AuthRequired
  public GetPersonResponse getPersonByUserId(String userId) {
    log.info("Fetching person with userId: {}", userId);

    Person person = personRepository.findByUserId(userId)
        .orElseThrow(() -> new NotFoundException(PERSON_NOT_FOUND));

    if (!authorizationComponent.hasPermissionToManageResource(person.getUserId())) {
      throw new NotFoundException(PERSON_NOT_FOUND);
    }

    return GetPersonResponse.from(person);
  }

  @Transactional
  public void createPerson(String userId, CreatePersonRequest request) {
    log.info("Creating person with document: {} - {}", request.document(), userId);

    boolean personAlreadyExists =
        personRepository.findByDocumentOrUserId(
            request.document(), userId
        ).isPresent();

    if (personAlreadyExists) {
      throw new ConflictException(PERSON_DUPLICATED);
    }

    Address address = Address.fromCreationRequest(request.address());
    Address savedAddress = addressRepository.save(address);

    Person person = Person.fromCreationRequest(request, savedAddress);
    Person savedPerson = personRepository.save(person);

    log.info("Person created with id: {}", savedPerson.getId());

  }

  @Transactional
  @AuthRequired
  public void updatePerson(String id, UpdatePersonRequest request) {
    log.info("Updating person with id: {}", id);

    Person person = personRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(PERSON_NOT_FOUND));

    if (!authorizationComponent.hasPermissionToManageResource(person.getUserId())) {
      throw new NotFoundException(PERSON_NOT_FOUND);
    }

    person.updateFromRequest(request);
    personRepository.save(person);

    log.info("Person with id: {} updated successfully", id);
  }

  @Transactional
  @AuthRequired
  @RoleRestricted("ADMIN")
  public void deletePerson(String id) {
    log.info("Deleting person with id: {}", id);

    Person person = personRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(PERSON_NOT_FOUND));

    personRepository.delete(person);

    userRoleProducer.publishRoleDropped(
        new UserRoleDroppedEvent(person.getId(), "PERSON")
    );

    log.info("Person with id: {} deleted successfully", id);
  }

  @Transactional
  public void deleteIfNothingToParent(String personId, String deletedRole) {
    boolean hasDoctor =
        personRepository.existsDoctorById(personId)
            && !"DOCTOR".equalsIgnoreCase(deletedRole);

    boolean hasPatient =
        personRepository.existsPatientById(personId)
            && !"PATIENT".equalsIgnoreCase(deletedRole);

    if (!hasDoctor && !hasPatient) {
      Person person = personRepository.findById(personId)
          .orElseThrow(() -> new NotFoundException(PERSON_NOT_FOUND));

      personRepository.delete(person);

      userRoleProducer.publishRoleDropped(
          new UserRoleDroppedEvent(person.getId(), "PERSON")
      );

      log.info("Person with id: {} deleted successfully because isn't parenting nothing anymore.", personId);
    }
  }
}
