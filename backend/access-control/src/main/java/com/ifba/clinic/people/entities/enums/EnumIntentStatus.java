package com.ifba.clinic.people.entities.enums;

// Patient -> IMPLICIT -> (PROCESSED | ERRORED)
// Doctor -> PENDING -> (APPROVED | REJECTED) -> (PROCESSED | ERRORED)
public enum EnumIntentStatus {

  IMPLICIT,

  PENDING,
  APPROVED,
  REJECTED,

  ERRORED,
  PROCESSED;
}
