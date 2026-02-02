package com.ifba.clinic.people.entities;

import com.ifba.clinic.people.entities.enums.EnumBrazilState;
import com.ifba.clinic.people.models.requests.AddressRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
@Table(name = "TADDRESS")
@SQLDelete(sql = "UPDATE TADDRESS SET IN_DELETED = true WHERE ID_ADDRESS = ?")
@SQLRestriction("IN_DELETED = false")
public class Address {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  @Column(name = "ID_ADDRESS")
  private String id;

  @Column(name = "NM_STREET", nullable = false)
  private String street;

  @Column(name = "VL_HOUSE")
  private String house;

  @Column(name = "VL_COMPLEMENT")
  private String complement;

  @Column(name = "VL_NEIGHBORHOOD", nullable = false)
  private String neighborhood;

  @Column(name = "VL_CITY", nullable = false)
  private String city;

  @Column(name = "VL_STATE", nullable = false)
  @Enumerated
  private EnumBrazilState state;

  @Column(name = "VL_ZIPCODE", nullable = false)
  private String zipCode;

  @Column(name = "DT_CREATED", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "IN_DELETED", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean deleted = false;

  @PrePersist
  protected void prePersist() {
    createdAt = LocalDateTime.now();
  }

  public Address updateFromRequest(AddressRequest request) {
    this.street = request.street();
    this.house = request.house();
    this.complement = request.complement();
    this.neighborhood = request.neighborhood();
    this.city = request.city();
    this.state = request.state();
    this.zipCode = request.zipCode();

    return this;
  }

  public static Address fromCreationRequest(AddressRequest request) {
    return Address.builder()
        .street(request.street())
        .house(request.house())
        .complement(request.complement())
        .neighborhood(request.neighborhood())
        .city(request.city())
        .state(request.state())
        .zipCode(request.zipCode())
        .build();
  }

}
