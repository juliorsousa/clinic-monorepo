package com.ifba.clinic.people.entities;

import com.ifba.clinic.people.entities.enums.EnumDoctorSpeciality;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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
@Table(name = "TDOCTORS")
@SQLDelete(sql = "UPDATE TDOCTORS SET IN_DELETED = true WHERE ID_DOCTOR = ?")
@SQLRestriction("IN_DELETED = false")
public class Doctor {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  @Column(name = "ID_DOCTOR")
  private String id;

  @Column(name = "NM_DOCTOR", nullable = false)
  private String name;

  @Column(name = "VL_EMAIL", nullable = false, unique = true)
  private String email;

  @Column(name = "VL_PHONE", nullable = false)
  private String phone;

  @Column(name = "VL_CREDENTIAL", nullable = false, unique = true)
  private String credential;

  @Column(name = "VL_SPECIALITY", nullable = false)
  @Enumerated
  private EnumDoctorSpeciality speciality;

  @JoinColumn(name = "CD_ADDRESS", nullable = false)
  @OneToOne
  private Address address;

  @Column(name = "IN_DELETED", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean deleted = false;

}
