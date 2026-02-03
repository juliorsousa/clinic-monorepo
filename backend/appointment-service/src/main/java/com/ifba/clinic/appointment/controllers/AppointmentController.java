package com.ifba.clinic.appointment.controllers;

import com.ifba.clinic.appointment.models.request.CreateAppointmentRequest;
import com.ifba.clinic.appointment.models.request.DatePeriodRequest;
import com.ifba.clinic.appointment.models.request.PageableRequest;
import com.ifba.clinic.appointment.models.response.CreateAppointmentResponse;
import com.ifba.clinic.appointment.models.response.GetAppointmentResponse;
import com.ifba.clinic.appointment.models.response.PageResponse;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/appointments")
@SecurityRequirement(name = "bearerAuth")
public interface AppointmentController {

  @Operation(
      summary = "Listar consultas de um paciente",
      description = """
          Retorna uma lista paginada de consultas efetuadas pelo paciente ordenados por data
          
          Suporta paginação.
          """
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Consultas recuperadas com sucesso",
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
  @GetMapping("/by-patient/{id}")
  PageResponse<GetAppointmentResponse> listPatientAppointments(
      @PathVariable
      @Parameter(
          description = "ID do médico",
          example = "a3f1a9e4-7b20-4fa3-bc1b-5e57b51fd123",
          required = true
      )
      String id,
      @ParameterObject @Valid PageableRequest pageable,
      @ParameterObject @Valid DatePeriodRequest datePeriodRequest
  );

  @Operation(
      summary = "Listar consultas de um médico em um período de tempo",
      description = """
          Retorna uma lista paginada de consultas efetuadas pelo médico em determinado período ordenados por data
          
          Suporta paginação.
          """
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Consultas recuperadas com sucesso",
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
  @GetMapping("/by-doctor/{id}")
  PageResponse<GetAppointmentResponse> listDoctorDateAppointments(
      @PathVariable
      @Parameter(
          description = "ID do médico",
          example = "a3f1a9e4-7b20-4fa3-bc1b-5e57b51fd123",
          required = true
      )
      String id,
      @ParameterObject @Valid PageableRequest pageable,
      @ParameterObject @Valid DatePeriodRequest datePeriodRequest
  );

  @Operation(
      summary = "Listar todas as consultas",
      description = """
          Retorna uma lista paginada de todas as consultas
          
          Suporta paginação.
          """
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Consultas recuperadas com sucesso",
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
  @GetMapping("/all")
  PageResponse<GetAppointmentResponse> listAllAppointments(
      @ParameterObject PageableRequest pageable
  );

  @Operation(
      summary = "Criar consulta",
      description = "Cria um novo registro de consulta do paciente no sistema."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "201",
          description = "Consulta marcada com sucesso",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = CreateAppointmentResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Corpo da requisição inválido"
      ),
      @ApiResponse(
          responseCode = "409",
          description = "Consulta já marcada na data/horário"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Não autorizado"
      )
  })
  @PostMapping("/")
  ResponseEntity<CreateAppointmentResponse> createAppointment(
      @Valid
      @RequestBody CreateAppointmentRequest request
  );

  @Operation(
      summary = "Cancelar consulta",
      description = "Cancela uma consulta pelo ID"
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "204",
          description = "Consulta excluída com sucesso"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Consulta não encontrada"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Não autorizado"
      )
  })
  @DeleteMapping("/{id}")
  ResponseEntity<Void> cancelAppointment(
      @PathVariable
      @Parameter(
          description = "ID da consulta",
          example = "a3f1a9e4-7b20-4fa3-bc1b-5e57b51fd123",
          required = true
      )
      String id
  );

  @Operation(
      summary = "Apagar todas as consultas por Paciente",
      description = "Apaga todas as consultas de um paciente"
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "204",
          description = "Consultas excluídas com sucesso"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Consultas não encontrada"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Não autorizado"
      )
  })
  @DeleteMapping("/by-patient/{id}")
  ResponseEntity<Void> deleteAllPatientAppointments(
      @PathVariable
      @Parameter(
          description = "ID do paciente",
          example = "a3f1a9e4-7b20-4fa3-bc1b-5e57b51fd123",
          required = true
      )
      String id
  );

  @Operation(
      summary = "Apagar todas as consultas por médico",
      description = "Apaga todas as consultas de um médico"
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "204",
          description = "Consultas excluídas com sucesso"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Consultas não encontradas"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Não autorizado"
      )
  })

  @DeleteMapping("/by-doctor/{id}")
  ResponseEntity<Void> deleteAllDoctorAppointments(
      @PathVariable
      @Parameter(
          description = "ID do médico",
          example = "a3f1a9e4-7b20-4fa3-bc1b-5e57b51fd123",
          required = true
      )
      String id
  );
}
