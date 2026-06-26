package gytis.courier.exception;

import gytis.courier.adapter.in.rest.common.ApiResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CompositeValidationException extends RuntimeException {
  private final List<ApiResponse> errors;

  public CompositeValidationException(List<ApiResponse> errors) {
    super("Validation failed: " + errors.stream()
            .map(ApiResponse::message)
            .collect(Collectors.joining(", ")));
    this.errors = Collections.unmodifiableList(errors);
  }

  public List<ApiResponse> getErrors() { return errors; }

}
