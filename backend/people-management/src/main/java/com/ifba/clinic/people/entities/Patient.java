package com.ifba.clinic.people.entities;

import com.ifba.clinic.people.models.requests.CreatePatientRequest;
import com.ifba.clinic.people.models.requests.UpdatePatientRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "TPATIENTS")
@SQLDelete(sql = "UPDATE TPATIENTS SET IN_DELETED = true WHERE ID_PATIENT = ?")
@SQLRestriction("IN_DELETED = false")
public class Patient {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  @Column(name = "ID_PATIENT")
  private String id;

  @Column(name = "ID_USER", nullable = false, unique = true)
  private String userId;

  @Column(name = "NM_PATIENT", nullable = false)
  private String name;

  @Column(name = "VL_DOCUMENT", nullable = false)
  private String document;

  @Column(name = "VL_PHONE", nullable = false)
  private String phone;

  @JoinColumn(name = "CD_ADDRESS", nullable = false)
  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  private Address address;

  @Column(name = "IN_DELETED", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean deleted = false;

  public void updateFromRequest(UpdatePatientRequest request) {
    this.name = request.name();
    this.phone = request.phone();

    this.address = address.updateFromRequest(request.address());
  }

  public static Patient fromCreationRequest(CreatePatientRequest request, Address address) {
    return Patient.builder()
        .name(request.name())
        .document(request.document())
        .userId(request.userId())
        .phone(request.phone())
        .address(address)
        .build();
  }

}
