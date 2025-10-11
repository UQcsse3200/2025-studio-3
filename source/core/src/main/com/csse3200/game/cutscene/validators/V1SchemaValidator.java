package com.csse3200.game.cutscene.validators;

import com.csse3200.game.cutscene.models.dto.*;
import com.csse3200.game.exceptions.AuthoringError;
import java.util.*;

/** Schema Validator for Version 1 of the cutscene schema. */
public class V1SchemaValidator implements SchemaValidator {
  static final String BEATS_PATH = "doc.cutscene.beats.";
  static final String CHARACTERS_PATH = "doc.characters.";

  private Set<String> characterIds;
  private Set<String> backgroundIds;
  private Set<String> soundIds;
  private Set<String> beatIds;
  private Map<String, Set<String>> characterPoses;

  ActionValidatorRegistry actionValidatorRegistry;

  /**
   * {@inheritDoc}
   *
   * <p>Validates:
   *
   * <ul>
   *   <li>The document is valid
   *   <li>The document has the correct schema version
   *   <li>Characters exist and are valid
   *   <li>Backgrounds exist and are valid
   *   <li>Sounds exist and are valid
   *   <li>Cutscenes exist and are valid
   * </ul>
   *
   * @param cutsceneDocDTO The {@link CutsceneDocDTO} that needs to be validated
   * @return A list of {@link AuthoringError} detailing each rule infraction
   */
  @Override
  public List<AuthoringError> validate(CutsceneDocDTO cutsceneDocDTO) {
    // INITIALISE LISTS
    characterIds = new HashSet<>();
    backgroundIds = new HashSet<>();
    soundIds = new HashSet<>();
    beatIds = new HashSet<>();
    characterPoses = new HashMap<>();

    // Add pseudo "end" beat
    beatIds.add("end");

    actionValidatorRegistry = new ActionValidatorRegistry();

    List<AuthoringError> errors = new ArrayList<>();

    if (cutsceneDocDTO == null) {
      return List.of(new AuthoringError("DOC_NULL", "doc", "The json file is null or invalid."));
    }

    if (cutsceneDocDTO.schemaVersion != 1) {
      return List.of(
          new AuthoringError(
              "INVALID_SCHEMA", "doc", "The schema version is invalid, should be 1."));
    }

    if (cutsceneDocDTO.characters == null) {
      return List.of(new AuthoringError("CHARACTERS_NULL", "doc", "Characters are required"));
    }

    if (cutsceneDocDTO.backgrounds == null) {
      return List.of(new AuthoringError("BACKGROUNDS_NULL", "doc", "Backgrounds are required"));
    }

    if (cutsceneDocDTO.sounds == null) {
      return List.of(new AuthoringError("SOUNDS_NULL", "doc", "Sounds are required"));
    }

    if (cutsceneDocDTO.cutscene == null) {
      return List.of(new AuthoringError("CUTSCENE_NULL", "doc", "Cutscene is required"));
    }

    errors.addAll(characterValidation(cutsceneDocDTO.characters));

    errors.addAll(backgroundValidation(cutsceneDocDTO.backgrounds));

    errors.addAll(soundValidation(cutsceneDocDTO.sounds));

    errors.addAll(cutsceneValidation(cutsceneDocDTO.cutscene));

    return errors;
  }

  /**
   * Validates a list of {@link CharacterDTO} against the following rules:
   *
   * <ul>
   *   <li>{@link CharacterDTO#id} must not be null
   *   <li>{@link CharacterDTO#name} must not be null
   *   <li>{@link CharacterDTO#poses} must not be null or empty
   *   <li>Every entry in {@link CharacterDTO#poses} must have a non empty and non null {@link
   *       String} key and {@link String} value
   *   <li>{@link CharacterDTO#id} must be unique across all characters
   * </ul>
   *
   * @param characters A list of {@link CharacterDTO} to be validated
   * @return A list of {@link AuthoringError} detailing each rule infraction
   */
  private List<AuthoringError> characterValidation(List<CharacterDTO> characters) {
    List<AuthoringError> characterErrors = new ArrayList<>();

    for (CharacterDTO characterDTO : characters) {
      if (characterDTO.id == null)
        characterErrors.add(
            new AuthoringError("NULL_CHARACTER_ID", "doc.characters", "Character id is null"));

      if (characterDTO.name == null)
        characterErrors.add(
            new AuthoringError("NULL_CHARACTER_NAME", "doc.characters", "Character name is null"));

      if (characterDTO.poses.isEmpty())
        characterErrors.add(
            new AuthoringError(
                "NULL_CHARACTER_EMPTY_POSES",
                "doc.characters." + characterDTO.id,
                "Character must have at lest 1 pose"));
      else if (!ValidatorUtils.stringMapValid(characterDTO.poses)) {
        characterErrors.add(
            new AuthoringError(
                "EMPTY_POSE", "doc.characters." + characterDTO.id, "A pose key or value is empty"));
      }

      if (characterDTO.id != null) {
        if (characterIds.contains(characterDTO.id)) {
          characterErrors.add(
              new AuthoringError(
                  "CHARACTER_ID_TAKEN",
                  "doc.characters." + characterDTO.id,
                  "Character IDs must be unique"));
        } else {
          characterIds.add(characterDTO.id);
        }

        if (!characterDTO.poses.isEmpty()) {
          characterPoses.put(characterDTO.id, characterDTO.poses.keySet());
        }
      }
    }

    return characterErrors;
  }

  /**
   * Validates a list of {@link BackgroundDTO} against the following rules:
   *
   * <ul>
   *   <li>{@link BackgroundDTO#id} must not be null
   *   <li>{@link BackgroundDTO#id} must be unique
   *   <li>{@link BackgroundDTO#image} must not be null
   * </ul>
   *
   * @param backgrounds A list of {@link BackgroundDTO} to be validated
   * @return A list of {@link AuthoringError} detailing each rule infraction
   */
  private List<AuthoringError> backgroundValidation(List<BackgroundDTO> backgrounds) {
    List<AuthoringError> backgroundErrors = new ArrayList<>();

    for (BackgroundDTO background : backgrounds) {
      if (background.id == null)
        backgroundErrors.add(
            new AuthoringError("NULL_BACKGROUND_ID", "doc.backgrounds", "Background id is null."));

      if (background.image == null)
        backgroundErrors.add(
            new AuthoringError(
                "NULL_BACKGROUND_IMAGE", "doc.backgrounds", "Background image address is null"));

      if (background.id != null) {
        if (backgroundIds.contains(background.id)) {
          backgroundErrors.add(
              new AuthoringError(
                  "BACKGROUND_ID_TAKEN",
                  "doc.backgrounds." + background.id,
                  "Background IDs must be unique"));
        } else {
          backgroundIds.add(background.id);
        }
      }
    }

    return backgroundErrors;
  }

  /**
   * Validates a list of {@link SoundDTO} against the following rules:
   *
   * <ul>
   *   <li>{@link SoundDTO#id} must not be null
   *   <li>{@link SoundDTO#file} must not be null
   *   <li>If {@link SoundDTO#id} isn't null, it must correspond to a {@link SoundDTO}
   * </ul>
   *
   * @param sounds AA list of {@link SoundDTO} to be validated
   * @return A list of {@link AuthoringError} detailing each rule infraction
   */
  private List<AuthoringError> soundValidation(List<SoundDTO> sounds) {
    List<AuthoringError> soundErrors = new ArrayList<>();

    for (SoundDTO sound : sounds) {
      if (sound.id == null)
        soundErrors.add(new AuthoringError("NULL_SOUND_ID", "doc.sounds", "Sound id is null."));

      if (sound.file == null)
        soundErrors.add(
            new AuthoringError("NULL_SOUND_FILE", "doc.sounds", "Sound file address is null"));

      if (sound.id != null) {
        if (soundIds.contains(sound.id)) {
          soundErrors.add(
              new AuthoringError(
                  "SOUND_ID_TAKEN", "doc.sounds." + sound.id, "Sound IDs must be unique"));
        } else {
          soundIds.add(sound.id);
        }
      }
    }

    return soundErrors;
  }

  /**
   * Validates a {@link CutsceneDTO} against the following rules:
   *
   * <ul>
   *   <li>{@link CutsceneDTO#id} must not be null
   *   <li>{@link CutsceneDTO#beats} must not be null
   *   <li>{@link CutsceneDTO#beats} must be a list of {@link BeatDTO}
   *   <li>Each {@link BeatDTO} in {@link CutsceneDTO#beats} must have a valid {@link BeatDTO#id}
   * </ul>
   *
   * It also constructs a {@link ValidationCtx} to pass Cutscene information to the {@link
   * V1SchemaValidator#beatValidation(BeatDTO, ValidationCtx)}
   *
   * @param cutscene The {@link CutsceneDTO} to be validated
   * @return A list of {@link AuthoringError} detailing each rule infraction
   */
  private List<AuthoringError> cutsceneValidation(CutsceneDTO cutscene) {
    List<AuthoringError> cutsceneErrors = new ArrayList<>();

    if (cutscene.id == null)
      cutsceneErrors.add(
          new AuthoringError("CUTSCENE_ID_NULL", "doc.cutscene", "Cutscene id must not be null"));

    if (cutscene.beats == null)
      cutsceneErrors.add(
          new AuthoringError("CUTSCENE_BEATS_NULL", "doc.cutscene", "Beats must not be null"));

    // quick pass to collect all beat ids
    for (BeatDTO beat : cutscene.beats) {
      if (beat.id == null)
        cutsceneErrors.add(
            new AuthoringError("BEAT_ID_NULL", "doc.cutscene.beats", "Beats must have a valid id"));
      else {
        if (beatIds.contains(beat.id)) {
          cutsceneErrors.add(
              new AuthoringError(
                  "BEAT_ID_EXISTS",
                  BEATS_PATH + beat.id,
                  "The beat id " + beat.id + " already exists"));
        } else {
          beatIds.add(beat.id);
        }
      }
    }

    ValidationCtx validationCtx =
        new ValidationCtx(characterIds, backgroundIds, soundIds, beatIds, characterPoses);

    for (BeatDTO beat : cutscene.beats) {
      cutsceneErrors.addAll(beatValidation(beat, validationCtx));
    }

    return cutsceneErrors;
  }

  /**
   * Validates a {@link BeatDTO} against the following rules:
   *
   * <ul>
   *   <li>{@link BeatDTO#advance} must not be null
   *   <li>The {@link AdvanceDTO#mode} from {@link BeatDTO#advance} must not be null
   *   <li>If {@link AdvanceDTO#mode} is {@code "auto"} or {@code "input"}, {@link AdvanceDTO#delay}
   *       and {@link AdvanceDTO#signalKey} must be null
   *   <li>If {@link AdvanceDTO#mode} is {@code "auto_delay"}, {@link AdvanceDTO#delay} must not be
   *       null and {@link AdvanceDTO#signalKey} must be null
   *   <li>If {@link AdvanceDTO#mode} is {@code "signal"}, {@link AdvanceDTO#delay} must be null,
   *       and {@link AdvanceDTO#signalKey} must not be null
   *   <li>{@link AdvanceDTO#mode} must not be any value than above
   *   <li>{@link BeatDTO#actions} must not be null or empty
   *   <li>Each action in {@link BeatDTO#actions} must pass its own validator
   *   <li>The {@link BeatDTO#id} is unique
   * </ul>
   *
   * @param beat The {@link BeatDTO} to be validated
   * @param validationCtx A {@link ValidationCtx} containing all the schema context
   * @return A like of {@link AuthoringError} detailing each rule infraction
   */
  private List<AuthoringError> beatValidation(BeatDTO beat, ValidationCtx validationCtx) {
    List<AuthoringError> beatErrors = new ArrayList<>();

    if (beat.advance == null)
      beatErrors.add(
          new AuthoringError(
              "BEAT_ADVANCE_NULL", BEATS_PATH + beat.id, "Beat advance must not be null"));
    else
      switch (beat.advance.mode) {
        case null:
          beatErrors.add(
              new AuthoringError(
                  "BEAT_ADVANCE_MODE_NULL",
                  BEATS_PATH + beat.id,
                  "Beat advance mode must not be null"));
          break;
        case "auto", "input":
          if (beat.advance.delay != null || beat.advance.signalKey != null)
            beatErrors.add(
                new AuthoringError(
                    "BEAT_ADVANCE_MODE_AUTO_UNEXPECTED",
                    BEATS_PATH + beat.id,
                    "Unexpected value in auto advance"));
          break;
        case "auto_delay":
          if (beat.advance.delay == null || beat.advance.signalKey != null)
            beatErrors.add(
                new AuthoringError(
                    "BEAT_ADVANCE_MODE_AUTO_DELAY_INVALID",
                    BEATS_PATH + beat.id,
                    "Invalid data for auto delay advance"));
          break;
        case "signal":
          if (beat.advance.delay != null || beat.advance.signalKey == null)
            beatErrors.add(
                new AuthoringError(
                    "BEAT_ADVANCE_MODE_SIGNAL_INVALID",
                    BEATS_PATH + beat.id,
                    "Invalid data for signal advance"));
          break;
        default:
          throw new IllegalStateException("Unexpected value: " + beat.advance.mode);
      }

    if (beat.actions == null || beat.actions.isEmpty()) {
      beatErrors.add(
          new AuthoringError(
              "BEAT_ACTIONS_NULL", BEATS_PATH + beat.id, "Beat actions is null or empty"));
    } else {
      for (ActionDTO action : beat.actions) {
        beatErrors.addAll(actionValidatorRegistry.validate(action, beat.id, validationCtx));
      }
    }

    return beatErrors;
  }
}
