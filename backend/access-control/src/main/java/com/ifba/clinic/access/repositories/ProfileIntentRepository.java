package com.ifba.clinic.access.repositories;

import com.ifba.clinic.access.entities.ProfileIntent;
import com.ifba.clinic.access.entities.User;
import com.ifba.clinic.access.entities.enums.EnumRole;
import java.util.Optional;
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
        END
      """)
  Optional<ProfileIntent> findCurrentByUser(
      @Param("user") User user,
      @Param("preferErrored") boolean preferErrored
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
        END
      """)
  Optional<ProfileIntent> findCurrentByUserAndType(
      @Param("user") User user,
      @Param("type") EnumRole type,
      @Param("preferErrored") boolean preferErrored
  );
}
