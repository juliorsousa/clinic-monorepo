package com.ifba.clinic.people.repositories;

import com.ifba.clinic.people.entities.Doctor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, String> {
  // Busca por credencial (ex: CRM)
  Optional<Doctor> findByCredential(String credential);
  
  // Busca por email
  Optional<Doctor> findByEmail(String email);

  // Busca combinada para validar duplicidade na criação/edição
  Optional<Doctor> findByCredentialOrEmail(String credential, String email);
}