package com.csse3200.game.cutscene.validators;

import com.csse3200.game.cutscene.CutsceneSchemaKeys;
import com.csse3200.game.cutscene.models.dto.ActionDTO;
import com.csse3200.game.cutscene.models.object.Transition;
import com.csse3200.game.exceptions.AuthoringError;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValidatorUtils {
  private ValidatorUtils() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
  }

  /**
   * Validates a {@link Map<String, String>} against the following rules:
   *
   * <ul>
   *   <li>For each map entry {@link Map.Entry}:
   *       <ul>
   *         <li>{@link Map.Entry#getKey()} must not be null
   *         <li>{@link Map.Entry#getKey()} must not be empty
   *         <li>{@link Map.Entry#getValue()} must not be null
   *         <li>{@link Map.Entry#getValue()} must not be empty
   *       </ul>
   * </ul>
   *
   * @param map The {@link Map<String, String>}
   * @return {@code false} if any key or value is invalid, true otherwise (The whole map is valid)
   */
  public static boolean stringMapValid(Map<String, String> map) {
    for (Map.Entry<String, String> entry : map.entrySet()) {
      if (entry.getKey() == null
          || entry.getKey().isEmpty()
          || entry.getValue() == null
          || entry.getValue().isEmpty()) {
        return false;
      }
    }

    return true;
  }

  /**
   * Takes a {@code beatId} string and {@link ActionDTO} and validates that a valid transition is
   * present in the {@link ActionDTO}
   *
   * <p>The conditions for a valid transition are as follows:
   *
   * <ul>
   *   <li>The {@code transition} field value must be a string
   *   <li>The {@code transition} field value must be in {@link Transition#displayNames()}
   *   <li>The {@code duration} field value must be an integer
   *   <li>The {@code duration} field value must be {@code >0}
   * </ul>
   *
   * @param beatId The string id of the beat being processed
   * @param action The {@link ActionDTO} that the transition is contained in
   * @return A list of {@link AuthoringError} detailing each rule infraction
   */
  public static List<AuthoringError> validateTransition(String beatId, ActionDTO action) {
    Object transitionObject = action.getFields().get("transition");

    List<AuthoringError> errors = new ArrayList<>();

    List<AuthoringError> transitionErrors =
        validateString(
            transitionObject,
            "transition",
            CutsceneSchemaKeys.BEATS_PATH + beatId + CutsceneSchemaKeys.ACTIONS_SUB);

    errors.addAll(transitionErrors);

    if (transitionErrors.isEmpty()
        && transitionObject instanceof String transition
        && !Transition.displayNames().contains(transition)) {
      errors.add(
          new AuthoringError(
              "ACTION_TRANSITION_INVALID",
              CutsceneSchemaKeys.BEATS_PATH + beatId + CutsceneSchemaKeys.ACTIONS_SUB,
              "Transition must be any of: " + String.join(", ", Transition.displayNames())));
    }

    Object durationObject = action.getFields().get("duration");

    List<AuthoringError> durationErrors =
        validateInt(
            durationObject,
            "duration",
            CutsceneSchemaKeys.BEATS_PATH + beatId + CutsceneSchemaKeys.ACTIONS_SUB);

    errors.addAll(durationErrors);

    if (durationErrors.isEmpty() && ((Long) durationObject).intValue() <= 0) {
      errors.add(
          new AuthoringError(
              "ACTION_DURATION_INVALID",
              CutsceneSchemaKeys.BEATS_PATH + beatId + CutsceneSchemaKeys.ACTIONS_SUB,
              "Duration must be greater than 0"));
    }

    return errors;
  }

  /**
   * Validates a java {@link Object} to see if it's a valid {@link String} object.
   *
   * <p>It checks that:
   *
   * <ul>
   *   <li>The {@link Object} is not null
   *   <li>The {@link Object} is an instance of {@link String}
   * </ul>
   *
   * @param object The alleged string object to be validated
   * @param key The key for error (used for tracing)
   * @param path The path of the error (used for tracing)
   * @return A list of {@link AuthoringError} detailing each rule infraction
   */
  public static List<AuthoringError> validateString(Object object, String key, String path) {
    if (object == null) {
      return List.of(
          new AuthoringError(
              CutsceneSchemaKeys.ACTION_ERROR_PREFIX
                  + key.toUpperCase()
                  + CutsceneSchemaKeys.NULL_SUFFIX,
              path,
              key + CutsceneSchemaKeys.CANNOT_BE_NULL));
    }

    if (!(object instanceof String)) {
      return List.of(
          new AuthoringError(
              CutsceneSchemaKeys.ACTION_ERROR_PREFIX + key.toUpperCase() + "_NOT_STRING",
              path,
              key + " must be a string"));
    }

    return new ArrayList<>();
  }

  /**
   * Validates a java {@link Object} to see if it's a valid {@link Integer} or {@link Long} object.
   *
   * <p>It checks that:
   *
   * <ul>
   *   <li>The {@link Object} is not null
   *   <li>The {@link Object} is an instance of {@link Integer} or {@link Long}
   * </ul>
   *
   * @param object The alleged string object to be validated
   * @param key The key for error (used for tracing)
   * @param path The path of the error (used for tracing)
   * @return A list of {@link AuthoringError} detailing each rule infraction
   */
  public static List<AuthoringError> validateInt(Object object, String key, String path) {
    if (object == null) {
      return List.of(
          new AuthoringError(
              CutsceneSchemaKeys.ACTION_ERROR_PREFIX
                  + key.toUpperCase()
                  + CutsceneSchemaKeys.NULL_SUFFIX,
              path,
              key + CutsceneSchemaKeys.CANNOT_BE_NULL));
    }

    if (!(object instanceof Integer) && !(object instanceof Long)) {
      return List.of(
          new AuthoringError(
              CutsceneSchemaKeys.ACTION_ERROR_PREFIX + key.toUpperCase() + "_NOT_INTEGER",
              path,
              key + " must be an integer"));
    }

    return new ArrayList<>();
  }

  /**
   * Validates a java {@link Object} to see if it's a valid {@link Float} or {@link Double} object.
   *
   * <p>It checks that:
   *
   * <ul>
   *   <li>The {@link Object} is not null
   *   <li>The {@link Object} is an instance of {@link Float} or {@link Double}
   * </ul>
   *
   * @param object The alleged string object to be validated
   * @param key The key for error (used for tracing)
   * @param path The path of the error (used for tracing)
   * @return A list of {@link AuthoringError} detailing each rule infraction
   */
  public static List<AuthoringError> validateFloatDouble(Object object, String key, String path) {
    if (object == null) {
      return List.of(
          new AuthoringError(
              CutsceneSchemaKeys.ACTION_ERROR_PREFIX
                  + key.toUpperCase()
                  + CutsceneSchemaKeys.NULL_SUFFIX,
              path,
              key + CutsceneSchemaKeys.CANNOT_BE_NULL));
    }

    if (!(object instanceof Float) && !(object instanceof Double)) {
      return List.of(
          new AuthoringError(
              CutsceneSchemaKeys.ACTION_ERROR_PREFIX + key.toUpperCase() + "_NOT_FLOAT",
              path,
              key + " must be a float"));
    }

    return new ArrayList<>();
  }

  /**
   * Validates a java {@link Object} to see if it's a valid {@link Float} or {@link Double} If it is
   * a valid {@link Float} or {@link Double}, it then converts it to a double and checks if it's in
   * the specified range.
   *
   * @param object The alleged string object to be validated
   * @param key The key for error (used for tracing)
   * @param path The path of the error (used for tracing)
   * @param min The minimum bound for the double
   * @param max The maximum bound for the double
   * @return A list of {@link AuthoringError} detailing each rule infraction
   */
  public static List<AuthoringError> validateDoubleWithRange(
      Object object, String key, String path, double min, double max) {
    List<AuthoringError> errors = validateFloatDouble(object, key, path);

    if (errors.isEmpty()) {
      double value = (double) object;
      errors.addAll(validateRange(value, min, max, path));
    }

    return errors;
  }

  /**
   * Validates a java {@link Object} to see if it's a valid {@link Boolean} object.
   *
   * <p>It checks that:
   *
   * <ul>
   *   <li>The {@link Object} is not null
   *   <li>The {@link Object} is an instance of {@link Boolean}
   * </ul>
   *
   * @param object The alleged string object to be validated
   * @param key The key for error (used for tracing)
   * @param path The path of the error (used for tracing)
   * @return A list of {@link AuthoringError} detailing each rule infraction
   */
  public static List<AuthoringError> validateBool(Object object, String key, String path) {
    if (object == null) {
      return List.of(
          new AuthoringError(
              CutsceneSchemaKeys.ACTION_ERROR_PREFIX
                  + key.toUpperCase()
                  + CutsceneSchemaKeys.NULL_SUFFIX,
              path,
              key + CutsceneSchemaKeys.CANNOT_BE_NULL));
    }

    if (!(object instanceof Boolean)) {
      return List.of(
          new AuthoringError(
              CutsceneSchemaKeys.ACTION_ERROR_PREFIX + key.toUpperCase() + "_NOT_BOOLEAN",
              path,
              key + " must be a boolean"));
    }

    return new ArrayList<>();
  }

  /**
   * Validates that a {@link Comparable} {@link Number} is within the inclusive range [{@code min},
   * {@code max}].
   *
   * @param number The {@link Comparable} {@link Number} that is going to be checked.
   * @param min The minimum bound
   * @param max The maximum bound
   * @param path The path of the error (used for tracing)
   * @return A list of {@link AuthoringError} detailing each rule infraction
   * @param <N> Numeric type that is {@link Number} and {@link Comparable} to itself
   */
  public static <N extends Number & Comparable<N>> List<AuthoringError> validateRange(
      N number, N min, N max, String path) {
    if (number.compareTo(min) < 0) {
      return List.of(
          new AuthoringError(
              "NUMBER_TOO_SMALL",
              path,
              "The number " + number + " is smaller than the minimum value " + min));
    }

    if (number.compareTo(max) > 0) {
      return List.of(
          new AuthoringError(
              "NUMBER_TOO_LARGE",
              path,
              "The number " + number + " is smaller than the minimum value " + max));
    }

    return new ArrayList<>();
  }

  /**
   * Takes a {@code beatId} string and {@link ActionDTO} and validates that a valid await is present
   * in the {@link ActionDTO} For a valid await, the {@link ActionDTO} must contain a valid boolean
   * value under the tag {@code "await"}
   *
   * @param beatId The string id of the beat being processed
   * @param action The {@link ActionDTO} that the transition is contained in
   * @return A list of {@link AuthoringError} detailing each rule infraction
   */
  public static List<AuthoringError> validateAwait(String beatId, ActionDTO action) {
    Object awaitObject = action.getFields().get("await");

    return validateBool(
        awaitObject,
        "await",
        CutsceneSchemaKeys.BEATS_PATH + beatId + CutsceneSchemaKeys.ACTIONS_SUB);
  }

  /**
   * Takes a {@code beatId} string and {@link ActionDTO} and validates that a valid sound ID is
   * present in the {@link ActionDTO} For a valid soundID, the {@link ActionDTO} must contain a
   * valid string value under the tag {@code "await"} AND the value of the field must be in the
   * {@link ValidationCtx#soundIds()}.
   *
   * @param action The {@link ActionDTO} that the transition is contained in
   * @param context The {@link ValidationCtx} with the document context
   * @param path The path of the error (used for tracing)
   * @return A list of {@link AuthoringError} detailing each rule infraction
   */
  public static List<AuthoringError> validateSoundId(
      ActionDTO action, ValidationCtx context, String path) {
    List<AuthoringError> soundIdErrors =
        ValidatorUtils.validateString(
            action.getFields().get(CutsceneSchemaKeys.SOUND_ID_FIELD),
            CutsceneSchemaKeys.SOUND_ID_FIELD,
            path);

    if (soundIdErrors.isEmpty()) {
      String soundId = (String) action.getFields().get(CutsceneSchemaKeys.SOUND_ID_FIELD);
      if (!context.soundIds().contains(soundId)) {
        soundIdErrors.add(
            new AuthoringError(
                "ACTION_AUDIO_PLAY_INVALID_SOUND_ID",
                path,
                "Sound ID " + soundId + " does not exist"));
      }
    }

    return soundIdErrors;
  }
}
