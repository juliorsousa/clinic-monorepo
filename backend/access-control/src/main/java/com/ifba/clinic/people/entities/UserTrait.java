package com.ifba.clinic.people.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "TUSER_TRAITS")
@SQLDelete(sql = "UPDATE TUSER_TRAITS SET IN_DELETED = true WHERE ID_USER_TRAIT = ?")
@SQLRestriction("IN_DELETED = false")
public class UserTrait {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  @Column(name = "ID_USER_TRAIT")
  private String id;

  @JoinColumn(name = "ID_USER", nullable = false)
  @ManyToOne
  private User user;

  @Column(name = "VL_TRAIT", nullable = false)
  private String trait;

  @Column(name = "IN_DELETED", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean deleted = false;

}
