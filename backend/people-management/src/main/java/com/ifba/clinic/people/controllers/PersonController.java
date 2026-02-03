package com.ifba.clinic.people.controllers;

import com.ifba.clinic.people.models.requests.PageableRequest;
import com.ifba.clinic.people.models.requests.person.CreatePersonRequest;
import com.ifba.clinic.people.models.requests.person.UpdatePersonRequest;
import com.ifba.clinic.people.models.response.PageResponse;
import com.ifba.clinic.people.models.response.person.CreatePersonResponse;
import com.ifba.clinic.people.models.response.person.GetPersonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/persons")
@SecurityRequirement(name = "bearerAuth")
public interface PersonController {

  @Operation(
      summary = "Listar pessoas",
      description = """
          Retorna uma lista paginada de pessoas ordenadas por nome.
          
          Suporta paginação.
          """
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Pessoas recuperadas com sucesso",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = PageResponse.class)
          )
      ),
      @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos"),
      @ApiResponse(responseCode = "401", description = "Não autorizado")
  })
  @GetMapping("/")
  PageResponse<GetPersonResponse> listPersons(
      @ParameterObject PageableRequest pageable
  );

  @Operation(
      summary = "Buscar pessoa pelo ID",
      description = "Retorna as informações de uma pessoa específica pelo seu ID."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Pessoa recuperada com sucesso",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = GetPersonResponse.class)
          )
      ),
      @ApiResponse(responseCode = "401", description = "Não autorizado")
  })
  @GetMapping("/{id}")
  GetPersonResponse getPersonById(
      @PathVariable
      @Parameter(
          description = "ID da pessoa",
          example = "a3f1a9e4-7b20-4fa3-bc1b-5e57b51fd123",
          required = true
      )
      String id
  );

  @Operation(
      summary = "Buscar pessoa pelo ID de usuário",
      description = "Retorna as informações de uma pessoa específica pelo seu ID de usuário associado"
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Pessoa recuperada com sucesso",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = GetPersonResponse.class)
          )
      ),
      @ApiResponse(responseCode = "401", description = "Não autorizado")
  })
  @GetMapping("/by-user/{id}")
  GetPersonResponse getPersonByUserId(
      @PathVariable
      @Parameter(
          description = "ID do usuário",
          example = "a3f1a9e4-7b20-4fa3-bc1b-5e57b51fd123",
          required = true
      )
      String id
  );

  @Operation(
      summary = "Atualizar pessoa",
      description = "Atualiza parcialmente as informações da pessoa."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Pessoa atualizada com sucesso"),
      @ApiResponse(responseCode = "400", description = "Corpo da requisição inválido"),
      @ApiResponse(responseCode = "404", description = "Pessoa não encontrada"),
      @ApiResponse(responseCode = "401", description = "Não autorizado")
  })
  @PutMapping("/{id}")
  ResponseEntity<Void> updatePerson(
      @PathVariable
      @Parameter(
          description = "ID da pessoa",
          example = "a3f1a9e4-7b20-4fa3-bc1b-5e57b51fd123",
          required = true
      )
      String id,

      @Valid @RequestBody UpdatePersonRequest request
  );

  @Operation(
      summary = "Excluir pessoa",
      description = "Exclui uma pessoa pelo ID."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Pessoa excluída com sucesso"),
      @ApiResponse(responseCode = "404", description = "Pessoa não encontrada"),
      @ApiResponse(responseCode = "401", description = "Não autorizado")
  })
  @DeleteMapping("/{id}")
  ResponseEntity<Void> deletePerson(
      @PathVariable
      @Parameter(
          description = "ID da pessoa",
          example = "a3f1a9e4-7b20-4fa3-bc1b-5e57b51fd123",
          required = true
      )
      String id
  );
}
