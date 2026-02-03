package com.ifba.clinic.people.entities;

import com.ifba.clinic.access.entities.ProfileIntent;
import com.ifba.clinic.people.models.requests.CreateDoctorRequest;
import com.ifba.clinic.people.models.requests.UpdateDoctorRequest;
import com.ifba.clinic.people.models.requests.person.CreatePersonRequest;
import com.ifba.clinic.people.models.requests.person.UpdatePersonRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
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
@Table(name = "TPERSONS")
@SQLDelete(sql = "UPDATE TPERSONS SET IN_DELETED = true WHERE ID_PERSON = ?")
@SQLRestriction("IN_DELETED = false")
public class Person {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  @Column(name = "ID_PERSON")
  private String id;

  @Column(name = "ID_USER", nullable = false, unique = true)
  private String userId;

  @Column(name = "NM_DOCTOR", nullable = false)
  private String name;

  @Column(name = "VL_PHONE", nullable = false)
  private String phone;

  @Column(name = "VL_DOCUMENT", nullable = false)
  private String document;

  @JoinColumn(name = "CD_ADDRESS", nullable = false)
  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  private Address address;

  @OneToOne(
      mappedBy = "person",
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}
  )
  private Patient patient;

  @OneToOne(
      mappedBy = "person",
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}
  )
  private Doctor doctor;

  @Column(name = "IN_DELETED", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean deleted = false;

  @Column(name = "DT_CREATED", nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void prePersist() {
    createdAt = LocalDateTime.now();
  }

  public void updateFromRequest(UpdatePersonRequest request) {
    this.name = request.name();
    this.phone = request.phone();

    if (request.address() != null) {
      if (this.address == null) {
        this.address = Address.fromCreationRequest(request.address());
      } else {
        this.address = this.address.updateFromRequest(request.address());
      }
    }
  }

  public static Person fromCreationRequest(CreatePersonRequest request, Address address) {
    return Person.builder()
        .name(request.name())
        .phone(request.phone())
        .document(request.document())
        .userId(request.userId())
        .address(address)
        .build();
  }

}
