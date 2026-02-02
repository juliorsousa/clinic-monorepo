package com.ifba.appointment.models.response;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponse<T>(
  int page,
  int totalPages,
  long totalElements,
  List<T> content
) {
  public static <T> PageResponse<T> of(
    int page,
    int totalPages,
    long totalElements,
    List<T> content
  ) {
    return new PageResponse<>(page, totalPages, totalElements, content);
  }

  public static <T> PageResponse<T> from(
    Page<T> pageData
  ) {
    return new PageResponse<>(
      pageData.getNumber(),
      pageData.getTotalPages(),
      pageData.getTotalElements(),
      pageData.getContent()
    );
  }
}
