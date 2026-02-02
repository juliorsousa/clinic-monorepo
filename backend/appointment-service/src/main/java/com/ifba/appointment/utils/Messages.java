package com.ifba.appointment.utils;

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

  public static final String PATIENT_DUPLICATED =
      "Já existe um paciente cadastrado com este CPF ou e-mail.";

  public static final String PATIENT_NOT_FOUND =
      "Paciente não encontrado.";

  // Generic Person messages

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
  
}