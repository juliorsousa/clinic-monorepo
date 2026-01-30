package com.ifba.clinic.people.repositories;

import com.ifba.clinic.people.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findByEmail(String email);
}