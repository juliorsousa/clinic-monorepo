package com.ifba.clinic.access.controllers;

import com.ifba.clinic.access.models.requests.PageableRequest;
import com.ifba.clinic.access.models.requests.profiles.ProfileIntentRequest;
import com.ifba.clinic.access.models.response.PageResponse;
import com.ifba.clinic.access.models.response.ProfileIntentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/profiling")
@SecurityRequirement(name = "bearerAuth")
public interface ProfilingController {

  @Operation(
      summary = "Obter intenção de configuração de perfil",
      description = "Retorna a intenção atual de configuração de perfil do usuário autenticado."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Intenção encontrada",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ProfileIntentResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Não autorizado"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Nenhuma intenção encontrada"
      )
  })
  @GetMapping("/profile-intent")
  ResponseEntity<ProfileIntentResponse> getProfileSetupIntent();

  @Operation(
      summary = "Criar intenção de configuração de perfil",
      description = "Cria uma nova intenção de configuração de perfil para o usuário autenticado."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "201",
          description = "Intenção criada com sucesso",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ProfileIntentResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Corpo da requisição inválido"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Não autorizado"
      )
  })
  @PostMapping("/profile-intent")
  ResponseEntity<ProfileIntentResponse> createProfileSetupIntent(
      @Valid @RequestBody ProfileIntentRequest request
  );

  @Operation(
      summary = "Listar intenções de perfil",
      description = "Retorna uma lista paginada de intenções de configuração de perfil."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Lista retornada com sucesso",
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
  @GetMapping("/profile-intents")
  PageResponse<ProfileIntentResponse> listProfileIntents(
      @ParameterObject PageableRequest pageable
  );

  @Operation(
      summary = "Rejeitar intenção de perfil",
      description = "Rejeita uma intenção de configuração de perfil pelo ID."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Intenção rejeitada com sucesso"),
      @ApiResponse(responseCode = "401", description = "Não autorizado"),
      @ApiResponse(responseCode = "404", description = "Intenção não encontrada")
  })
  @PostMapping("/profile-intent/{id}/reject")
  ResponseEntity<Void> rejectProfileSetupIntent(
      @PathVariable String id
  );

  @Operation(
      summary = "Aprovar intenção de perfil",
      description = "Aprova uma intenção de configuração de perfil pelo ID."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Intenção aprovada com sucesso"),
      @ApiResponse(responseCode = "401", description = "Não autorizado"),
      @ApiResponse(responseCode = "404", description = "Intenção não encontrada")
  })
  @PostMapping("/profile-intent/{id}/approve")
  ResponseEntity<Void> approveProfileSetupIntent(
      @PathVariable String id
  );
}
