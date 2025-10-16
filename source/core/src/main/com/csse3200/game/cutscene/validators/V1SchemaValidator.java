package com.csse3200.game.cutscene.validators;

import com.csse3200.game.cutscene.CutsceneSchemaKeys;
import com.csse3200.game.cutscene.models.dto.*;
import com.csse3200.game.exceptions.AuthoringError;
import java.util.*;

/** Schema Validator for Version 1 of the cutscene schema. */
public class V1SchemaValidator implements SchemaValidator {

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

    if (cutsceneDocDTO.getSchemaVersion() != 1) {
      return List.of(
          new AuthoringError(
              "INVALID_SCHEMA", "doc", "The schema version is invalid, should be 1."));
    }

    if (cutsceneDocDTO.getCharacters() == null) {
      return List.of(new AuthoringError("CHARACTERS_NULL", "doc", "Characters are required"));
    }

    if (cutsceneDocDTO.getBackgrounds() == null) {
      return List.of(new AuthoringError("BACKGROUNDS_NULL", "doc", "Backgrounds are required"));
    }

    if (cutsceneDocDTO.getSounds() == null) {
      return List.of(new AuthoringError("SOUNDS_NULL", "doc", "Sounds are required"));
    }

    if (cutsceneDocDTO.getCutscene() == null) {
      return List.of(new AuthoringError("CUTSCENE_NULL", "doc", "Cutscene is required"));
    }

    errors.addAll(characterValidation(cutsceneDocDTO.getCharacters()));

    errors.addAll(backgroundValidation(cutsceneDocDTO.getBackgrounds()));

    errors.addAll(soundValidation(cutsceneDocDTO.getSounds()));

    errors.addAll(cutsceneValidation(cutsceneDocDTO.getCutscene()));

    return errors;
  }

  Optional<AuthoringError> validateCharacterPoses(CharacterDTO characterDTO) {
    if (characterDTO.getId() != null) {
      if (characterIds.contains(characterDTO.getId())) {
        return Optional.of(
            new AuthoringError(
                "CHARACTER_ID_TAKEN",
                CutsceneSchemaKeys.CHARACTERS_PATH + characterDTO.getId(),
                "Character IDs must be unique"));
      } else {
        characterIds.add(characterDTO.getId());
      }

      if (!characterDTO.getPoses().isEmpty()) {
        characterPoses.put(characterDTO.getId(), characterDTO.getPoses().keySet());
      }
    }

    return Optional.empty();
  }

  /**
   * Validates a list of {@link CharacterDTO} against the following rules:
   *
   * <ul>
   *   <li>{@link CharacterDTO#getId()} must not be null
   *   <li>{@link CharacterDTO#getName()} must not be null
   *   <li>{@link CharacterDTO#getPoses()} must not be null or empty
   *   <li>Every entry in {@link CharacterDTO#getPoses()} must have a non empty and non null {@link
   *       String} key and {@link String} value
   *   <li>{@link CharacterDTO#getId()} must be unique across all characters
   * </ul>
   *
   * @param characters A list of {@link CharacterDTO} to be validated
   * @return A list of {@link AuthoringError} detailing each rule infraction
   */
  private List<AuthoringError> characterValidation(List<CharacterDTO> characters) {
    List<AuthoringError> characterErrors = new ArrayList<>();

    for (CharacterDTO characterDTO : characters) {
      if (characterDTO.getId() == null)
        characterErrors.add(
            new AuthoringError("NULL_CHARACTER_ID", "doc.characters", "Character id is null"));

      if (characterDTO.getName() == null)
        characterErrors.add(
            new AuthoringError("NULL_CHARACTER_NAME", "doc.characters", "Character name is null"));

      if (characterDTO.getPoses().isEmpty())
        characterErrors.add(
            new AuthoringError(
                "NULL_CHARACTER_EMPTY_POSES",
                CutsceneSchemaKeys.CHARACTERS_PATH + characterDTO.getId(),
                "Character must have at lest 1 pose"));
      else if (!ValidatorUtils.stringMapValid(characterDTO.getPoses())) {
        characterErrors.add(
            new AuthoringError(
                "EMPTY_POSE",
                CutsceneSchemaKeys.CHARACTERS_PATH + characterDTO.getId(),
                "A pose key or value is empty"));
      }

      validateCharacterPoses(characterDTO).ifPresent(characterErrors::add);
    }

    return characterErrors;
  }

  /**
   * Validates a list of {@link BackgroundDTO} against the following rules:
   *
   * <ul>
   *   <li>{@link BackgroundDTO#getId()} must not be null
   *   <li>{@link BackgroundDTO#getId()} must be unique
   *   <li>{@link BackgroundDTO#getImage()} must not be null
   * </ul>
   *
   * @param backgrounds A list of {@link BackgroundDTO} to be validated
   * @return A list of {@link AuthoringError} detailing each rule infraction
   */
  private List<AuthoringError> backgroundValidation(List<BackgroundDTO> backgrounds) {
    List<AuthoringError> backgroundErrors = new ArrayList<>();

    for (BackgroundDTO background : backgrounds) {
      if (background.getId() == null)
        backgroundErrors.add(
            new AuthoringError("NULL_BACKGROUND_ID", "doc.backgrounds", "Background id is null."));

      if (background.getImage() == null)
        backgroundErrors.add(
            new AuthoringError(
                "NULL_BACKGROUND_IMAGE", "doc.backgrounds", "Background image address is null"));

      if (background.getId() != null) {
        if (backgroundIds.contains(background.getId())) {
          backgroundErrors.add(
              new AuthoringError(
                  "BACKGROUND_ID_TAKEN",
                  "doc.backgrounds." + background.getId(),
                  "Background IDs must be unique"));
        } else {
          backgroundIds.add(background.getId());
        }
      }
    }

    return backgroundErrors;
  }

  /**
   * Validates a list of {@link SoundDTO} against the following rules:
   *
   * <ul>
   *   <li>{@link SoundDTO#getId()} must not be null
   *   <li>{@link SoundDTO#getFile()} must not be null
   *   <li>If {@link SoundDTO#getId()} isn't null, it must correspond to a {@link SoundDTO}
   * </ul>
   *
   * @param sounds AA list of {@link SoundDTO} to be validated
   * @return A list of {@link AuthoringError} detailing each rule infraction
   */
  private List<AuthoringError> soundValidation(List<SoundDTO> sounds) {
    List<AuthoringError> soundErrors = new ArrayList<>();

    for (SoundDTO sound : sounds) {
      if (sound.getId() == null)
        soundErrors.add(new AuthoringError("NULL_SOUND_ID", "doc.sounds", "Sound id is null."));

      if (sound.getFile() == null)
        soundErrors.add(
            new AuthoringError("NULL_SOUND_FILE", "doc.sounds", "Sound file address is null"));

      if (sound.getId() != null) {
        if (soundIds.contains(sound.getId())) {
          soundErrors.add(
              new AuthoringError(
                  "SOUND_ID_TAKEN", "doc.sounds." + sound.getId(), "Sound IDs must be unique"));
        } else {
          soundIds.add(sound.getId());
        }
      }
    }

    return soundErrors;
  }

  /**
   * Validates a {@link CutsceneDTO} against the following rules:
   *
   * <ul>
   *   <li>{@link CutsceneDTO#getId()} must not be null
   *   <li>{@link CutsceneDTO#getBeats()} must not be null
   *   <li>{@link CutsceneDTO#getBeats()} must be a list of {@link BeatDTO}
   *   <li>Each {@link BeatDTO} in {@link CutsceneDTO#getBeats()} must have a valid {@link
   *       BeatDTO#getId()}
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

    if (cutscene.getId() == null)
      cutsceneErrors.add(
          new AuthoringError("CUTSCENE_ID_NULL", "doc.cutscene", "Cutscene id must not be null"));

    if (cutscene.getBeats() == null)
      cutsceneErrors.add(
          new AuthoringError("CUTSCENE_BEATS_NULL", "doc.cutscene", "Beats must not be null"));

    // quick pass to collect all beat ids
    for (BeatDTO beat : cutscene.getBeats()) {
      if (beat.getId() == null)
        cutsceneErrors.add(
            new AuthoringError("BEAT_ID_NULL", "doc.cutscene.beats", "Beats must have a valid id"));
      else {
        if (beatIds.contains(beat.getId())) {
          cutsceneErrors.add(
              new AuthoringError(
                  "BEAT_ID_EXISTS",
                  CutsceneSchemaKeys.BEATS_PATH + beat.getId(),
                  "The beat id " + beat.getId() + " already exists"));
        } else {
          beatIds.add(beat.getId());
        }
      }
    }

    ValidationCtx validationCtx =
        new ValidationCtx(characterIds, backgroundIds, soundIds, beatIds, characterPoses);

    for (BeatDTO beat : cutscene.getBeats()) {
      cutsceneErrors.addAll(beatValidation(beat, validationCtx));
    }

    return cutsceneErrors;
  }

  Optional<AuthoringError> validateBeatAction(BeatDTO beat) {
    switch (beat.getAdvance().getMode()) {
      case null:
        return Optional.of(
            new AuthoringError(
                "BEAT_ADVANCE_MODE_NULL",
                CutsceneSchemaKeys.BEATS_PATH + beat.getId(),
                "Beat advance mode must not be null"));
      case "auto", "input":
        if (beat.getAdvance().getDelay() != null || beat.getAdvance().getSignalKey() != null)
          return Optional.of(
              new AuthoringError(
                  "BEAT_ADVANCE_MODE_AUTO_UNEXPECTED",
                  CutsceneSchemaKeys.BEATS_PATH + beat.getId(),
                  "Unexpected value in auto advance"));
        break;
      case "auto_delay":
        if (beat.getAdvance().getDelay() == null || beat.getAdvance().getSignalKey() != null)
          return Optional.of(
              new AuthoringError(
                  "BEAT_ADVANCE_MODE_AUTO_DELAY_INVALID",
                  CutsceneSchemaKeys.BEATS_PATH + beat.getId(),
                  "Invalid data for auto delay advance"));
        break;
      case "signal":
        if (beat.getAdvance().getDelay() != null || beat.getAdvance().getSignalKey() == null)
          return Optional.of(
              new AuthoringError(
                  "BEAT_ADVANCE_MODE_SIGNAL_INVALID",
                  CutsceneSchemaKeys.BEATS_PATH + beat.getId(),
                  "Invalid data for signal advance"));
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + beat.getAdvance().getMode());
    }

    return Optional.empty();
  }

  /**
   * Validates a {@link BeatDTO} against the following rules:
   *
   * <ul>
   *   <li>{@link BeatDTO#getAdvance()} must not be null
   *   <li>The {@link AdvanceDTO#getMode()} from {@link BeatDTO#getAdvance()} must not be null
   *   <li>If {@link AdvanceDTO#getMode()} is {@code "auto"} or {@code "input"}, {@link
   *       AdvanceDTO#delay} and {@link AdvanceDTO#getSignalKey()} must be null
   *   <li>If {@link AdvanceDTO#getMode()} is {@code "auto_delay"}, {@link AdvanceDTO#getDelay()}
   *       must not be null and {@link AdvanceDTO#getSignalKey()} must be null
   *   <li>If {@link AdvanceDTO#getMode()} is {@code "signal"}, {@link AdvanceDTO#getDelay()} must
   *       be null, and {@link AdvanceDTO#getSignalKey()} must not be null
   *   <li>{@link AdvanceDTO#getMode()} must not be any value than above
   *   <li>{@link BeatDTO#getActions()} must not be null or empty
   *   <li>Each action in {@link BeatDTO#getActions()} must pass its own validator
   *   <li>The {@link BeatDTO#getId()} is unique
   * </ul>
   *
   * @param beat The {@link BeatDTO} to be validated
   * @param validationCtx A {@link ValidationCtx} containing all the schema context
   * @return A like of {@link AuthoringError} detailing each rule infraction
   */
  private List<AuthoringError> beatValidation(BeatDTO beat, ValidationCtx validationCtx) {
    List<AuthoringError> beatErrors = new ArrayList<>();

    if (beat.getAdvance() == null)
      beatErrors.add(
          new AuthoringError(
              "BEAT_ADVANCE_NULL",
              CutsceneSchemaKeys.BEATS_PATH + beat.getId(),
              "Beat advance must not be null"));
    else validateBeatAction(beat).ifPresent(beatErrors::add);

    if (beat.getActions() == null || beat.getActions().isEmpty()) {
      beatErrors.add(
          new AuthoringError(
              "BEAT_ACTIONS_NULL",
              CutsceneSchemaKeys.BEATS_PATH + beat.getId(),
              "Beat actions is null or empty"));
    } else {
      for (ActionDTO action : beat.getActions()) {
        beatErrors.addAll(actionValidatorRegistry.validate(action, beat.getId(), validationCtx));
      }
    }

    return beatErrors;
  }
}
