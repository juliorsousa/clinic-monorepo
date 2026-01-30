package com.ifba.clinic.people.entities.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EnumDoctorSpeciality {

  ORTHOPEDICS("Ortopedia"),
  CARDIOLOGY("Cardiologia"),
  GYNECOLOGY("Ginecologia"),
  DERMATOLOGY("Dermatologia"),;

  private final String readableName;

}
