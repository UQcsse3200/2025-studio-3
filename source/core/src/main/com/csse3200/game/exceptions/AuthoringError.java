package com.csse3200.game.exceptions;

/**
 * Created when there is a check in {@link com.csse3200.game.cutscene.validators.ActionValidator}
 * fails.
 *
 * <p>This error is usually not thrown but rather contained in a list to document all the errors in
 * a schema file.
 */
public class AuthoringError extends Exception {
  private String code;
  private String path;
  private String message;

  /**
   * @param code The error code (in all capitals separated by '_')
   * @param path The path of the error in the schema file
   * @param message The detailed message describing the error with any extra helpful info
   */
  public AuthoringError(String code, String path, String message) {
    super(code + " @ " + path + " \nMessage: " + message);

    this.code = code;
    this.path = path;
    this.message = message;
  }

  public String getCode() {
    return code;
  }

  public String getPath() {
    return path;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return code + " @ " + path + " || Message: " + message;
  }
}
