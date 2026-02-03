package com.ifba.clinic.people.repositories;

import com.ifba.clinic.people.entities.Doctor;
import com.ifba.clinic.people.entities.Person;
import com.ifba.clinic.people.entities.enums.EnumDoctorSpecialty;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, String> {
  Optional<Doctor> findByCredential(String credential);
  Optional<Doctor> findByCredentialOrPerson(String credential, Person person);

  List<Doctor> findAllBySpecialty(EnumDoctorSpecialty specialty);
}