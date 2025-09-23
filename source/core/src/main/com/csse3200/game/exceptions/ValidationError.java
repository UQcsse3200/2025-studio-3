package com.csse3200.game.exceptions;

/**
 * Thrown when any {@link AuthoringError} is present after running a {@link
 * com.csse3200.game.cutscene.validators.SchemaValidator}
 */
public class ValidationError extends RuntimeException {
  public ValidationError() {
    super();
  }

  public ValidationError(String message) {
    super(message);
  }
}
