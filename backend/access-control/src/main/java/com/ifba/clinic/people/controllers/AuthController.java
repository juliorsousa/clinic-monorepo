package com.ifba.clinic.people.controllers;

import com.ifba.clinic.people.models.requests.CreateUserRequest;
import com.ifba.clinic.people.models.requests.LoginUserRequest;
import com.ifba.clinic.people.models.response.CreateUserResponse;
import com.ifba.clinic.people.models.response.CurrentUserResponse;
import com.ifba.clinic.people.models.response.TokenResponse;
import com.ifba.clinic.people.security.annotations.AuthRestrictedEndpoint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/auth")
@SecurityRequirement(name = "bearerAuth")
public interface AuthController {

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
  @PostMapping("/register")
  ResponseEntity<CreateUserResponse> registerUser(
      @Valid
      @RequestBody CreateUserRequest request
  );

  @Operation(
      summary = "Realiza o login do usuário",
      description = "Autentica o usuário e retorna um token de acesso."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Login realizado com sucesso",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = TokenResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Credenciais inválidas"
      )
  })
  @PostMapping("/login")
  ResponseEntity<TokenResponse> loginUser(
      @Valid
      @RequestBody LoginUserRequest request
  );
}
