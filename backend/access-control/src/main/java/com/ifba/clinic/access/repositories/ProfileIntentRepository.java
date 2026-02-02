package com.ifba.clinic.access.repositories;

import com.ifba.clinic.access.entities.ProfileIntent;
import com.ifba.clinic.access.entities.User;
import com.ifba.clinic.access.entities.enums.EnumIntentStatus;
import com.ifba.clinic.access.entities.enums.EnumRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileIntentRepository extends JpaRepository<ProfileIntent, String> {

  @Query("""
      SELECT pi FROM ProfileIntent pi
      WHERE pi.user = :user
      AND (
           (:preferErrored = true AND pi.status = com.ifba.clinic.access.entities.enums.EnumIntentStatus.ERRORED)
           OR
           pi.status IN (
             com.ifba.clinic.access.entities.enums.EnumIntentStatus.PENDING,
             com.ifba.clinic.access.entities.enums.EnumIntentStatus.APPROVED,
             com.ifba.clinic.access.entities.enums.EnumIntentStatus.IMPLICIT,
             com.ifba.clinic.access.entities.enums.EnumIntentStatus.PROCESSED
           )
      )
      ORDER BY
        CASE
          WHEN :preferErrored = true
               AND pi.status = com.ifba.clinic.access.entities.enums.EnumIntentStatus.ERRORED
          THEN 0
          ELSE 1
        END,
             pi.createdAt DESC
      LIMIT 1
      """)
  List<ProfileIntent> findCurrentByUser(
      @Param("user") User user,
      @Param("preferErrored") boolean preferErrored,
      Pageable pageable
  );

  @Query("""
      SELECT pi FROM ProfileIntent pi
      WHERE pi.user = :user
      AND pi.type = :type
      AND (
           (:preferErrored = true AND pi.status = com.ifba.clinic.access.entities.enums.EnumIntentStatus.ERRORED)
           OR
           pi.status IN (
             com.ifba.clinic.access.entities.enums.EnumIntentStatus.PENDING,
             com.ifba.clinic.access.entities.enums.EnumIntentStatus.APPROVED,
             com.ifba.clinic.access.entities.enums.EnumIntentStatus.IMPLICIT,
             com.ifba.clinic.access.entities.enums.EnumIntentStatus.PROCESSED
           )
      )
      ORDER BY
        CASE
          WHEN :preferErrored = true
               AND pi.status = com.ifba.clinic.access.entities.enums.EnumIntentStatus.ERRORED
          THEN 0
          ELSE 1
        END,
             pi.createdAt DESC
      LIMIT 1
      """)
  List<ProfileIntent> findCurrentByUserAndType(
      @Param("user") User user,
      @Param("type") EnumRole type,
      @Param("preferErrored") boolean preferErrored,
      Pageable pageable
  );

  List<ProfileIntent> findByUserAndTypeAndStatus(
      User user,
      EnumRole type,
      EnumIntentStatus status
  );
}
