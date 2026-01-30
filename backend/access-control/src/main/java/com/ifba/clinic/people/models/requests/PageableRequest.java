package com.ifba.clinic.people.models.requests;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;

public record PageableRequest(
    @Min(value = 0L, message = "A página deve ser maior ou igual a 0")
    @Parameter(description = "Número da página (inicia em 0)", example = "0")
    @RequestParam(defaultValue = "0")
    int page,
    @Min(value = 1L, message = "O tamanho da página deve ser maior ou igual a 1")
    @Parameter(description = "Tamanho da página", example = "10")
    @RequestParam(defaultValue = "10")
    int size
) {
  public static PageableRequest of(int page, int size) {
    return new PageableRequest(page, size);
  }

  public static PageRequest toPageable(PageableRequest pageableRequest) {
    return PageRequest.of(pageableRequest.page(), pageableRequest.size());
  }
}
