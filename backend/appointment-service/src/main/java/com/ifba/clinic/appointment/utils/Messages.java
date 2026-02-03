package com.ifba.clinic.appointment.utils;

public class Messages {

  private Messages() {}

  // Exception messages

  public static final String GENERIC_CONFLICT =
      "A requisição não pôde ser concluída devido a um conflito com o estado atual do recurso.";

  public static final String GENERIC_FORBIDDEN =
      "A requisição foi recusada devido a falta de permissões adequadas para acessar o recurso solicitado.";

  public static final String GENERIC_NOT_FOUND =
      "A requisição não pôde ser concluída porque o recurso solicitado não foi encontrado.";

  public static final String GENERIC_UNAUTHORIZED =
      "A requisição não pôde ser concluída devido a falha na autenticação do usuário.";

  // Generic Appoint messages

  public static final String ID_PATIENT_REQUIRED =
      "O id do paciente é obrigatório";

  public static final String ID_PATIENT_INVALID =
      "O id do paciente é inválido";

  public static final String ID_DOCTOR_REQUIRED =
      "O id do médico é obrigatório";

  public static final String ID_DOCTOR_INVALID =
      "O id do médico é inválido";

  public static final String DATE_TIME_NULL =
      "A data é obrigatória";

  public static final String DATE_TIME_PAST =
      "A data da consulta tem que ser no futuro.";

  public static final String APPOINTMENT_NOT_FOUND =
      "A consulta não foi encontrada";

  public static final String PATIENT_ALREADY_HAS_APPOINTMENT =
      "O paciente já possui uma consulta agendada para este dia.";

  public static final String DOCTOR_NOT_AVAILABLE =
      "O médico não está disponível neste dia e horário.";

  public static final String DOCTOR_SPECIALTY_MISMATCH =
      "A especialidade do médico não corresponde à especialidade requerida para a consulta.";

  public static final String NO_DOCTORS_FOR_SPECIALTY =
      "Não há médicos disponíveis para a especialidade selecionada.";

  public static final String CANT_CANCEL_APPOINTMENT =
      "Não é possível cancelar a consulta com menos de 24 horas de antecedência.";

  public static final String CANT_CANCEL_PAST_APPOINTMENT =
      "Não é possível cancelar uma consulta que já ocorreu ou foi cancelada.";
  
}