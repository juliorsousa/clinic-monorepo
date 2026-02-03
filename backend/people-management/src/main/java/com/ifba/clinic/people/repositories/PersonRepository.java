package com.ifba.clinic.people.repositories;

import com.ifba.clinic.people.entities.Patient;
import com.ifba.clinic.people.entities.Person;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, String> {
  Optional<Person> findByDocument(String document);
  Optional<Person> findByUserId(String userId);

  Optional<Person> findByDocumentOrUserId(String document, String userId);

  boolean existsDoctorById(String id);
  boolean existsPatientById(String id);
}