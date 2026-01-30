package com.ifba.clinic.people.repositories;

import com.ifba.clinic.people.entities.Patient;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
  Optional<Patient> findByDocument(String document);
  Optional<Patient> findByEmail(String email);

  Optional<Patient> findByDocumentOrEmail(String document, String email);
}