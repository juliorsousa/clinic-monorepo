package com.ifba.clinic.people.entities;

import com.ifba.clinic.people.entities.enums.EnumRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "TUSER_ROLES")
@SQLDelete(sql = "UPDATE TUSER_ROLES SET IN_DELETED = true WHERE ID_USER_ROLE = ?")
@SQLRestriction("IN_DELETED = false")
public class UserRole {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  @Column(name = "ID_USER_ROLE")
  private String id;

  @JoinColumn(name = "ID_USER", nullable = false)
  @ManyToOne
  private User user;

  @Enumerated
  @Column(name = "VL_ROLE", nullable = false)
  private EnumRole role;

  @Column(name = "ID_REFERENCED_ENTITY")
  private String referencedEntityId;

  @Column(name = "IN_DELETED", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean deleted = false;

}
