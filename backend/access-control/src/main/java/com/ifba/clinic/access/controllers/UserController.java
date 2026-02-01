package com.ifba.clinic.access.controllers;

import com.ifba.clinic.access.models.response.CurrentUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@SecurityRequirement(name = "bearerAuth")
public interface UserController {

  @Operation(
      summary = "Busca informações do usuário autenticado",
      description = "Retorna as informações do usuário atualmente autenticado no sistema."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Informações do usuário retornadas com sucesso"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Não autorizado"
      )
  })
  @GetMapping("/me")
  ResponseEntity<CurrentUserResponse> getCurrentUser();

}
