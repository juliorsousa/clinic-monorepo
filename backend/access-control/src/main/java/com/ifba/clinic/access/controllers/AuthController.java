package com.ifba.clinic.access.controllers;

import com.ifba.clinic.access.models.requests.CreateUserRequest;
import com.ifba.clinic.access.models.requests.LoginUserRequest;
import com.ifba.clinic.access.models.requests.ChangePasswordRequest;
import com.ifba.clinic.access.models.response.CreateUserResponse;
import com.ifba.clinic.access.models.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

  @PostMapping("/change-password")
  ResponseEntity<String> changePassword(
      @Valid
      @RequestBody ChangePasswordRequest request
  );
}
