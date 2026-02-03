package com.ifba.clinic.people.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "TPATIENTS")
@SQLDelete(sql = "UPDATE TPATIENTS SET IN_DELETED = true WHERE ID_PATIENT = ?")
@SQLRestriction("IN_DELETED = false")
public class Patient {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  @Column(name = "ID_PATIENT")
  private String id;

  @JoinColumn(name = "ID_PERSON", nullable = false)
  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  private Person person;

  @Column(name = "DT_CREATED", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "IN_DELETED", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean deleted = false;

  @PrePersist
  protected void prePersist() {
    createdAt = LocalDateTime.now();
  }

  public static Patient from(Person person) {
    return Patient.builder()
        .person(person)
        .build();
  }

}
