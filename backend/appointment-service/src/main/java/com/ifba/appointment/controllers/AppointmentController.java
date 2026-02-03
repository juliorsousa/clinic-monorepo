package com.ifba.appointment.controllers;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ifba.appointment.models.request.CreateAppointmentRequest;
import com.ifba.appointment.models.request.DatePeriodRequest;
import com.ifba.appointment.models.request.PageableRequest;
import com.ifba.appointment.models.response.CreateAppointmentResponse;
import com.ifba.appointment.models.response.GetAppoitmentResponse;
import com.ifba.appointment.models.response.PageResponse;

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
  @GetMapping("/list/patient")
  PageResponse<GetAppoitmentResponse> listPacientAppointments(
      @ParameterObject PageableRequest pageable
  );

  @Operation(
      summary = "Listar consultas de um médico",
      description = """
          Retorna uma lista paginada de consultas efetuadas pelo médico ordenados por data

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

  @GetMapping("/list/doctor")
  PageResponse<GetAppoitmentResponse> listDoctorAppointments(
      @ParameterObject PageableRequest pageable
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
  @PostMapping("/list/doctor/date")  // PostMapping ou usar RequestParam e passar na URL /date?start=...&finish=...
  PageResponse<GetAppoitmentResponse> listDoctorDateAppointments(
      @ParameterObject PageableRequest pageable,
      @Valid
      @RequestBody DatePeriodRequest datePeriodRequest
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
  @GetMapping("/list/all")
  PageResponse<GetAppoitmentResponse> listAllAppointments(
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

  @DeleteMapping("/cancel/{id}")
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
      summary = "apagar todas as consultas",
      description = "apaga todas as consultas de um paciente"
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

  @DeleteMapping("/delete/patient/{id}")   //Id pelo userContext
  ResponseEntity<Void> deleteAllPatientAppointment(
    @RequestParam String id
  );

    @Operation(
      summary = "apagar todas as consultas",
      description = "apaga todas as consultas de um médico"
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

  @DeleteMapping("/delete/doctor/{id}")    
  ResponseEntity<Void> deleteAllDoctorAppointment(
    @RequestParam String id
  );
}
