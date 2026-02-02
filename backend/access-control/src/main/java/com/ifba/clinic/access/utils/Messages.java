package com.ifba.clinic.access.utils;

public class Messages {

  private Messages() {}

  // Exception messages

  public static final String GENERIC_BAD_REQUEST =
      "A requisição não pôde ser concluída devido a um erro do cliente.";

  public static final String GENERIC_CONFLICT =
      "A requisição não pôde ser concluída devido a um conflito com o estado atual do recurso.";

  public static final String GENERIC_NO_CONTENT =
      "A requisição foi bem-sucedida, mas não há conteúdo para retornar.";

  public static final String GENERIC_NOT_FOUND =
      "A requisição não pôde ser concluída porque o recurso solicitado não foi encontrado.";

  public static final String GENERIC_UNAUTHORIZED =
      "A requisição não pôde ser concluída porque o cliente não está autenticado.";

  public static final String USER_DUPLICATED =
      "Já existe uma conta associada a este endereço de e-mail.";

  // Authentication messages

  public static final String AUTH_INVALID_CREDENTIALS =
      "Usuário ou senha inválidos.";

  public static final String PASSWORD_MUST_BE_DIFFERENT =
      "A nova senha deve ser diferente da senha atual.";

  // Profiling messages

  public static final String INTENT_ALREADY_EXISTS =
      "Você já possui uma intenção de perfil registrada para esse mesmo tipo";

  public static final String ROLE_ALREADY_EXISTS =
      "Você já possui um perfil deste tipo ativo.";

  // Generic User messages

  public static final String EMAIL_REQUIRED =
      "O e-mail é obrigatório.";

  public static final String EMAIL_LENGTH =
      "O e-mail deve ter no mínimo 6 e no máximo 100 caracteres.";

  public static final String EMAIL_INVALID =
      "E-mail inválido.";

  public static final String PASSWORD_REQUIRED =
      "A senha é obrigatória.";

  public static final String PASSWORD_LENGTH =
      "A senha deve ter entre 6 e 60 caracteres.";

  public static final String PASSWORD_DONT_MATCH_CRITERIA =
      "A senha deve conter pelo menos uma letra maiúscula, uma letra minúscula, um número e um caractere especial.";

  public static final String NAME_REQUIRED =
      "O nome é obrigatório.";

  public static final String NAME_MAX_LENGTH =
      "O nome deve ter no máximo 100 caracteres.";

  public static final String DOCUMENT_REQUIRED =
      "O CPF é obrigatório.";

  public static final String DOCUMENT_MAX_LENGTH =
      "O CPF deve ter no máximo 20 caracteres.";

  public static final String DOCUMENT_INVALID =
      "CPF inválido.";

  public static final String PHONE_REQUIRED =
      "O telefone é obrigatório.";

  public static final String PHONE_MAX_LENGTH =
      "O telefone deve ter no máximo 15 caracteres.";

  public static final String PHONE_INVALID =
      "Telefone inválido. Utilize o formato (00) 90000-0000 ou (00) 0000-0000.";

  // Address messages

  public static final String STREET_REQUIRED =
      "O logradouro é obrigatório.";

  public static final String STREET_MAX_LENGTH =
      "O logradouro deve ter no máximo 80 caracteres.";

  public static final String HOUSE_MAX_LENGTH =
      "O número da residência deve ter no máximo 10 caracteres.";

  public static final String COMPLEMENT_MAX_LENGTH =
      "O complemento deve ter no máximo 50 caracteres.";

  public static final String NEIGHBORHOOD_REQUIRED =
      "O bairro é obrigatório.";

  public static final String NEIGHBORHOOD_MAX_LENGTH =
      "O bairro deve ter no máximo 50 caracteres.";

  public static final String CITY_REQUIRED =
      "A cidade é obrigatória.";

  public static final String CITY_MAX_LENGTH =
      "A cidade deve ter no máximo 50 caracteres.";

  public static final String STATE_REQUIRED =
      "O estado é obrigatório.";

  public static final String ZIP_CODE_REQUIRED =
      "O CEP é obrigatório.";

  public static final String ZIP_CODE_INVALID =
      "CEP inválido. Utilize o formato 00000-000 ou 00000000.";
}
