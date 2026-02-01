package com.ifba.clinic.people.entities;

import com.ifba.clinic.people.entities.enums.EnumIntentStatus;
import com.ifba.clinic.people.entities.enums.EnumRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "TPROFILE_INTENTS")
@SQLDelete(sql = "UPDATE TPROFILE_INTENTS SET IN_DELETED = true WHERE ID_PROFILE_INTENT = ?")
@SQLRestriction("IN_DELETED = false")
public class ProfileIntent {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  @Column(name = "ID_PROFILE_INTENT")
  private String id;

  @JoinColumn(name = "ID_USER", nullable = false)
  @ManyToOne
  private User user;

  @Enumerated
  @Column(name = "VL_INTENT_TYPE", nullable = false)
  private EnumRole type;

  @Column(name = "VL_BODY", nullable = false, length = 3000)
  private String body;

  @Column(name = "VL_RESPONSE")
  private String response;

  @Enumerated
  @Column(name = "VL_STATUS", nullable = false)
  private EnumIntentStatus status;

  @Column(name = "IN_DELETED", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean deleted = false;

}
