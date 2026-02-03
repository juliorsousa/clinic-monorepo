package com.ifba.clinic.access.repositories;

import com.ifba.clinic.access.entities.User;
import com.ifba.clinic.access.entities.UserRole;
import com.ifba.clinic.access.entities.enums.EnumRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {
  Optional<UserRole> findByReferencedEntityIdAndRole(String referencedEntityId, EnumRole role);
}