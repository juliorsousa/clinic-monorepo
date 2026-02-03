package com.ifba.clinic.people.entities;

import com.ifba.clinic.people.entities.enums.EnumDoctorSpecialty;
import com.ifba.clinic.people.models.requests.CreateDoctorRequest;
import com.ifba.clinic.people.models.requests.UpdateDoctorRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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
@Table(name = "TDOCTORS")
@SQLDelete(sql = "UPDATE TDOCTORS SET IN_DELETED = true WHERE ID_DOCTOR = ?")
@SQLRestriction("IN_DELETED = false")
public class Doctor {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  @Column(name = "ID_DOCTOR")
  private String id;

  @Column(name = "ID_USER", nullable = false, unique = true)
  private String userId;

  @JoinColumn(name = "ID_PERSON", nullable = false)
  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  private Person person;

  @Column(name = "VL_CREDENTIAL", nullable = false)
  private String credential;

  @Column(name = "VL_SPECIALITY", nullable = false)
  @Enumerated
  private EnumDoctorSpecialty specialty;

  @Column(name = "IN_DELETED", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean deleted = false;

  @Column(name = "DT_CREATED", nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void prePersist() {
    createdAt = LocalDateTime.now();
  }

  public void updateFromRequest(UpdateDoctorRequest request) {
    this.specialty = request.specialty();
  }

  public static Doctor fromCreationRequest(Person person, CreateDoctorRequest request) {
    return Doctor.builder()
        .credential(request.credential())
        .specialty(request.specialty())
        .userId(person.getUserId())
        .person(person)
        .build();
  }

}
