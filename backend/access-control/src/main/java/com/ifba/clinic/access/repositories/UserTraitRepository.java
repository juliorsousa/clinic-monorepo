package com.ifba.clinic.access.repositories;

import com.ifba.clinic.access.entities.UserTrait;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTraitRepository extends JpaRepository<UserTrait, String> {
}