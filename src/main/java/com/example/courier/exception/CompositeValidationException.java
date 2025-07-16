package com.example.courier.exception;

import com.example.courier.dto.ApiResponseDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CompositeValidationException extends RuntimeException {
  private final List<ApiResponseDTO> errors;

  public CompositeValidationException(List<ApiResponseDTO> errors) {
    super("Validation failed: " + errors.stream()
            .map(ApiResponseDTO::message)
            .collect(Collectors.joining(", ")));
    this.errors = Collections.unmodifiableList(errors);
  }

  public List<ApiResponseDTO> getErrors() { return errors; }

}
