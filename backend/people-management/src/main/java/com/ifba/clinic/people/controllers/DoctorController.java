package com.ifba.clinic.people.controllers;

import com.ifba.clinic.people.models.requests.CreateDoctorRequest;
import com.ifba.clinic.people.models.requests.PageableRequest;
import com.ifba.clinic.people.models.requests.UpdateDoctorRequest;
import com.ifba.clinic.people.models.response.CreateDoctorResponse;
import com.ifba.clinic.people.models.response.GetDoctorResponse;
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

@RequestMapping("/doctors")
@SecurityRequirement(name = "bearerAuth")
public interface DoctorController {

  @Operation(
      summary = "Listar Médicos",
      description = """
          Retorna uma lista paginada de médicos ordenados por nome.

          Suporta paginação.
          """
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Médicos recuperados com sucesso",
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
  @GetMapping
  PageResponse<GetDoctorResponse> listDoctors(
      @ParameterObject PageableRequest pageable
  );

  @Operation(
      summary = "Criar Médico",
      description = "Cria um novo registro de Médico no sistema."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "201",
          description = "Médico criado com sucesso",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = CreateDoctorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Corpo da requisição inválido"
      ),
      @ApiResponse(
          responseCode = "409",
          description = "Médico já existe"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Não autorizado"
      )
  })
  @PostMapping
  ResponseEntity<CreateDoctorResponse> createDoctor(
      @Valid
      @RequestBody CreateDoctorRequest request
  );

  @Operation(
      summary = "Atualizar Médico",
      description = "Atualiza parcialmente as informações do Médico."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Médico atualizado com sucesso"
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Corpo da requisição inválido"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Médico não encontrado"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Não autorizado"
      )
  })
  @PutMapping("/{id}") //
  ResponseEntity<Void> updateDoctor(
      @PathVariable
      @Parameter(
          description = "ID do Médico",
          example = "a3f1a9e4-7b20-4fa3-bc1b-5e57b51fd123",
          required = true
      )
      String id,

      @Valid
      @RequestBody UpdateDoctorRequest request
  );

  @Operation(
      summary = "Excluir Médico",
      description = "Exclui um Médico pelo ID."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "204",
          description = "Médico excluído com sucesso"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Médico não encontrado"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Não autorizado"
      )
  })
  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteDoctor(
      @PathVariable
      @Parameter(
          description = "ID do médico",
          example = "a3f1a9e4-7b20-4fa3-bc1b-5e57b51fd123",
          required = true
      )
      String id
  );
}