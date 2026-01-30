package com.ifba.clinic.people.controllers;

import com.ifba.clinic.people.models.requests.CreatePatientRequest;
import com.ifba.clinic.people.models.requests.PageableRequest;
import com.ifba.clinic.people.models.requests.UpdatePatientRequest;
import com.ifba.clinic.people.models.response.CreatePatientResponse;
import com.ifba.clinic.people.models.response.GetPatientResponse;
import com.ifba.clinic.people.models.response.PageResponse;
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
      summary = "Criar paciente",
      description = "Cria um novo registro de paciente no sistema."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "201",
          description = "Paciente criado com sucesso",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = CreatePatientResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Corpo da requisição inválido"
      ),
      @ApiResponse(
          responseCode = "409",
          description = "Paciente já existe"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Não autorizado"
      )
  })
  @PostMapping("/")
  ResponseEntity<CreatePatientResponse> createPatient(
      @Valid
      @RequestBody CreatePatientRequest request
  );

  @Operation(
      summary = "Atualizar paciente",
      description = "Atualiza parcialmente as informações do paciente."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Paciente atualizado com sucesso"
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Corpo da requisição inválido"
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
  @PatchMapping("/{id}")
  ResponseEntity<Void> updatePatient(
      @PathVariable
      @Parameter(
          description = "ID do paciente",
          example = "a3f1a9e4-7b20-4fa3-bc1b-5e57b51fd123",
          required = true
      )
      String id,

      @Valid
      @RequestBody UpdatePatientRequest request
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
