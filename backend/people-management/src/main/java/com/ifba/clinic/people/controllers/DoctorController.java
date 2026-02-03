package com.ifba.clinic.people.controllers;

import com.ifba.clinic.people.entities.enums.EnumDoctorSpeciality;
import com.ifba.clinic.people.models.requests.CreateDoctorRequest;
import com.ifba.clinic.people.models.requests.PageableRequest;
import com.ifba.clinic.people.models.requests.UpdateDoctorRequest;
import com.ifba.clinic.people.models.response.GetDoctorResponse;
import com.ifba.clinic.people.models.response.GetPatientResponse;
import com.ifba.clinic.people.models.response.PageResponse;
import com.ifba.clinic.people.models.response.SummarizedDoctorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import java.time.LocalDateTime;

import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
      summary = "Buscar médico pelo ID",
      description = """
          Retorna as informações de um médico específico pelo seu ID.
          """
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Médico recuperado com sucesso",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = GetDoctorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Não autorizado"
      )
  })
  @GetMapping("/{id}")
  GetDoctorResponse getDoctorById(
      @PathVariable
      @Parameter(
          description = "ID do médico",
          example = "a3f1a9e4-7b20-4fa3-bc1b-5e57b51fd123",
          required = true
      )
      String id
  );

  @Operation(
      summary = "Buscar informações sumarizadas de médico pelo ID",
      description = """
          Retorna as informações não confidenciais de um médico específico pelo seu ID.
          """
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Médico recuperado com sucesso",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = SummarizedDoctorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Não autorizado"
      )
  })
  @GetMapping("/{id}/summary")
  SummarizedDoctorResponse getSummarizedDoctorById(
      @PathVariable
      @Parameter(
          description = "ID do médico",
          example = "a3f1a9e4-7b20-4fa3-bc1b-5e57b51fd123",
          required = true
      )
      String id
  );

  @Operation(
      summary = "Buscar informações sumarizadas de médicos pela especialidade",
      description = """
          Retorna as informações não confidenciais de médicos específicos pela especialidade.
          """
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Médicos recuperado com sucesso",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = PageResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Não autorizado"
      )
  })
  @GetMapping("/by-specialty/{specialty}/summary")
  List<SummarizedDoctorResponse> getSummarizedDoctorsBySpecialty(
      @PathVariable
      @Parameter(
          description = "Especialidade do médico",
          example = "ORTHOPEDICS",
          required = true
      )
      EnumDoctorSpeciality specialty
  );

  @Operation(
      summary = "Valida existência do médico",
      description = """
          Retorna verdadeiro caso o médico exista no sistema.

          """
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Médico validado com sucesso",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = Boolean.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Parâmetros de validação inválidos"
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
  @GetMapping("/{id}/validity")
  ResponseEntity<Boolean> validateDoctor(
        @PathVariable
        @Parameter(
            description = "ID do Médico",
            example = "a3f1a9e4-7b20-4fa3-bc1b-5e57b51fd123",
            required = true
        )
        String id
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