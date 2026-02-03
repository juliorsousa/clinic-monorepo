package com.ifba.clinic.people.controllers;

import com.ifba.clinic.people.models.requests.PageableRequest;
import com.ifba.clinic.people.models.response.GetPatientResponse;
import com.ifba.clinic.people.models.response.PageResponse;
import com.ifba.clinic.people.models.response.SummarizedPatientResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/patients")
@SecurityRequirement(name = "bearerAuth")
public interface PatientController {

  @Operation(
      summary = "Listar pacientes",
      description = """
          Retorna uma lista paginada de pacientes ordenados por nome

          Suporta paginação.
          """
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Pacientes recuperados com sucesso",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = PageResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Parâmetros de paginação inválidos"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Não autorizado"
      )
  })
  @GetMapping("/")
  PageResponse<GetPatientResponse> listPatients(
      @ParameterObject PageableRequest pageable
  );

  @Operation(
      summary = "Buscar paciente pelo ID",
      description = """
          Retorna as informações de um paciente específico pelo seu ID.
          """
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Paciente recuperado com sucesso",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = GetPatientResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Não autorizado"
      )
  })
  @GetMapping("/{id}")
  GetPatientResponse getPatientById(
      @PathVariable
      @Parameter(
          description = "ID do paciente",
          example = "a3f1a9e4-7b20-4fa3-bc1b-5e57b51fd123",
          required = true
      )
      String id
  );

  @Operation(
      summary = "Buscar informações sumarizadas de paciente pelo ID",
      description = """
          Retorna as informações não confidenciais de um paciente específico pelo seu ID.
          """
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Paciente recuperado com sucesso",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = SummarizedPatientResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Não autorizado"
      )
  })
  @GetMapping("/{id}/summary")
  SummarizedPatientResponse getSummarizedPatientById(
      @PathVariable
      @Parameter(
          description = "ID do paciente",
          example = "a3f1a9e4-7b20-4fa3-bc1b-5e57b51fd123",
          required = true
      )
      String id
  );

  @Operation(
      summary = "Excluir paciente",
      description = "Exclui um paciente pelo ID."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "204",
          description = "Paciente excluído com sucesso"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Paciente não encontrado"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Não autorizado"
      )
  })
  @DeleteMapping("/{id}")
  ResponseEntity<Void> deletePatient(
      @PathVariable
      @Parameter(
          description = "ID do paciente",
          example = "a3f1a9e4-7b20-4fa3-bc1b-5e57b51fd123",
          required = true
      )
      String id
  );
}
