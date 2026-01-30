package com.ifba.clinic.people.controllers;

import com.ifba.clinic.people.models.requests.CreateUserRequest;
import com.ifba.clinic.people.models.response.CreateUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@SecurityRequirement(name = "bearerAuth")
public interface UserController {

  @Operation(
      summary = "Criar usuário",
      description = "Cria um novo registro de usuário no sistema."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "201",
          description = "Usuário criado com sucesso",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = CreateUserResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Corpo da requisição inválido"
      ),
      @ApiResponse(
          responseCode = "409",
          description = "Usuário já existente"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Não autorizado"
      )
  })
  @PostMapping("/")
  ResponseEntity<CreateUserResponse> createUser(
      @Valid
      @RequestBody CreateUserRequest request
  );

}
