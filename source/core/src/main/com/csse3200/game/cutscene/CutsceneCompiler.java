package com.csse3200.game.cutscene;

import com.csse3200.game.cutscene.models.dto.*;
import com.csse3200.game.cutscene.models.object.*;
import com.csse3200.game.cutscene.models.object.Character;
import com.csse3200.game.cutscene.models.object.actiondata.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Cutscene compiler. */
public class CutsceneCompiler {
  // String constants for commonly used field names
  private static final String AWAIT_FIELD = "await";
  private static final String TRANSITION_FIELD = "transition";
  private static final String DURATION_FIELD = "duration";
  private static final String CHARACTER_ID_FIELD = "characterId";
  private List<Character> characters;
  private List<Background> backgrounds;
  private List<Sound> sounds;
  private List<Beat> beats;

  /**
   * Compiles a {@link CutsceneDocDTO} into a {@link Cutscene} No error checking is handled in this
   * method as it assumed that the {@link CutsceneDocDTO} has already been validated.
   *
   * @param cutsceneDocDTO The {@link CutsceneDocDTO} to be compiled
   * @return A {@link Cutscene} with all the information filled out.
   */
  public Cutscene compile(CutsceneDocDTO cutsceneDocDTO) {
    String id = cutsceneDocDTO.cutscene.id;
    characters = new ArrayList<>();
    backgrounds = new ArrayList<>();
    sounds = new ArrayList<>();
    beats = new ArrayList<>();

    for (CharacterDTO characterDTO : cutsceneDocDTO.characters) {
      characters.add(new Character(characterDTO.id, characterDTO.name, characterDTO.poses));
    }

    for (BackgroundDTO backgroundDTO : cutsceneDocDTO.backgrounds) {
      backgrounds.add(new Background(backgroundDTO.id, backgroundDTO.image));
    }

    for (SoundDTO soundDTO : cutsceneDocDTO.sounds) {
      sounds.add(new Sound(soundDTO.id, soundDTO.file));
    }

    for (BeatDTO beatDTO : cutsceneDocDTO.cutscene.beats) {
      String beatId = beatDTO.id;
      Advance advance =
          switch (beatDTO.advance.mode) {
            case "auto" -> Advance.auto();
            case "input" -> Advance.input();
            case "auto_delay" -> Advance.autoDelay(beatDTO.advance.delay);
            case "signal" -> Advance.signal(beatDTO.advance.signalKey);
            default -> Advance.auto();
          };
      List<ActionData> actions = getActions(beatDTO.actions);

      beats.add(new Beat(beatId, advance, actions));
    }

    return new Cutscene(id, characters, backgrounds, sounds, beats);
  }

  private List<ActionData> getActions(List<ActionDTO> actionsData) {
    List<ActionData> actions = new ArrayList<>();

    for (ActionDTO action : actionsData) {
      ActionData actionData = createActionData(action);
      if (actionData != null) {
        actions.add(actionData);
      }
    }

    return actions;
  }

  /**
   * Creates an action data object from an action DTO.
   *
   * @param action the action DTO
   * @return the action data object
   */
  private ActionData createActionData(ActionDTO action) {
    return switch (action.type) {
      case "audio.play" -> createAudioPlayData(action);
      case "audio.set" -> createAudioSetData(action);
      case "audio.stop" -> createAudioStopData(action);
      case "background.set" -> createBackgroundSetData(action);
      case "character.enter" -> createCharacterEnterData(action);
      case "character.exit" -> createCharacterExitData(action);
      case "choice" -> createChoiceData(action);
      case "dialogue.chorus" -> createDialogueChorusData(action);
      case "dialogue.show" -> createDialogueShowData(action);
      case "dialogue.hide" -> createDialogueHideData(action);
      case "goto" -> createGotoData(action);
      case "parallel" -> createParallelData(action);
      default -> null;
    };
  }

  /**
   * Creates an audio play data object from an action DTO.
   *
   * @param action the action DTO
   * @return the audio play data object
   */
  private AudioPlayData createAudioPlayData(ActionDTO action) {
    AudioBus bus = AudioBus.fromString((String) action.fields.get("bus"));
    Sound sound = getSound((String) action.fields.get("soundId"));
    Float volume = ((Double) action.fields.get("volume")).floatValue();

    Float pitch =
        action.fields.get("pitch") != null
            ? ((Double) action.fields.get("pitch")).floatValue()
            : null;
    Float pan =
        action.fields.get("pan") != null ? ((Double) action.fields.get("pan")).floatValue() : null;
    boolean loop = action.fields.get("loop") != null && (boolean) action.fields.get("loop");
    boolean await = (boolean) action.fields.get(AWAIT_FIELD);

    return new AudioPlayData(bus, sound, volume, pitch, pan, loop, await);
  }

  /**
   * Creates an audio set data object from an action DTO.
   *
   * @param action the action DTO
   * @return the audio set data object
   */
  private AudioSetData createAudioSetData(ActionDTO action) {
    AudioBus bus = AudioBus.fromString((String) action.fields.get("bus"));
    Float volume = ((Double) action.fields.get("volume")).floatValue();
    return new AudioSetData(bus, volume);
  }

  /**
   * Creates an audio stop data object from an action DTO.
   *
   * @param action the action DTO
   * @return the audio stop data object
   */
  private AudioStopData createAudioStopData(ActionDTO action) {
    AudioBus bus = AudioBus.fromString((String) action.fields.get("bus"));
    int fadeMs = ((Long) action.fields.get("fadeMs")).intValue();
    boolean await = (boolean) action.fields.get(AWAIT_FIELD);
    return new AudioStopData(bus, fadeMs, await);
  }

  /**
   * Creates a background set data object from an action DTO.
   *
   * @param action the action DTO
   * @return the background set data object
   */
  private BackgroundSetData createBackgroundSetData(ActionDTO action) {
    Background background = getBackground((String) action.fields.get("backgroundId"));
    Transition transition = Transition.fromString((String) action.fields.get(TRANSITION_FIELD));
    int duration = ((Long) action.fields.get(DURATION_FIELD)).intValue();
    boolean await = (boolean) action.fields.get(AWAIT_FIELD);
    return new BackgroundSetData(background, transition, duration, await);
  }

  /**
   * Creates a character enter data object from an action DTO.
   *
   * @param action the action DTO
   * @return the character enter data object
   */
  private CharacterEnterData createCharacterEnterData(ActionDTO action) {
    Character character = getCharacter((String) action.fields.get(CHARACTER_ID_FIELD));
    String pose = (String) action.fields.get("pose");
    Position position = Position.fromString((String) action.fields.get("position"));
    Transition transition = Transition.fromString((String) action.fields.get(TRANSITION_FIELD));
    int duration = ((Long) action.fields.get(DURATION_FIELD)).intValue();
    boolean await = (boolean) action.fields.get(AWAIT_FIELD);
    return new CharacterEnterData(character, pose, position, transition, duration, await);
  }

  /**
   * Creates a character exit data object from an action DTO.
   *
   * @param action the action DTO
   * @return the character exit data object
   */
  private CharacterExitData createCharacterExitData(ActionDTO action) {
    Character character = getCharacter((String) action.fields.get(CHARACTER_ID_FIELD));
    Transition transition = Transition.fromString((String) action.fields.get(TRANSITION_FIELD));
    int duration = ((Long) action.fields.get(DURATION_FIELD)).intValue();
    boolean await = (boolean) action.fields.get(AWAIT_FIELD);
    return new CharacterExitData(character, transition, duration, await);
  }

  /**
   * Creates a choice data object from an action DTO.
   *
   * @param action the action DTO
   * @return the choice data object
   */
  private ChoiceData createChoiceData(ActionDTO action) {
    String prompt = (String) action.fields.get("prompt");
    List<Choice> choices = new ArrayList<>();

    for (Object choiceObject : (List<?>) action.fields.get("choices")) {
      @SuppressWarnings("unchecked")
      Map<String, Object> choiceData = (Map<String, Object>) choiceObject;

      String type = (String) choiceData.get("type");
      String line = (String) choiceData.get("line");
      String cutsceneId = (String) choiceData.get("cutsceneId");
      String entryBetId = (String) choiceData.get("entryBeatId");

      choices.add(new Choice(type, line, cutsceneId, entryBetId));
    }

    return new ChoiceData(prompt, choices);
  }

  /**
   * Creates a dialogue chorus data object from an action DTO.
   *
   * @param action the action DTO
   * @return the dialogue chorus data object
   */
  private DialogueChorusData createDialogueChorusData(ActionDTO action) {
    List<Character> chorusCharacters = new ArrayList<>();
    String text = (String) action.fields.get("text");
    boolean await = (boolean) action.fields.get(AWAIT_FIELD);

    for (Object characterId : (List<?>) action.fields.get("characterIds")) {
      chorusCharacters.add(getCharacter((String) characterId));
    }

    return new DialogueChorusData(chorusCharacters, text, await);
  }

  /**
   * Creates a dialogue show data object from an action DTO.
   *
   * @param action the action DTO
   * @return the dialogue show data object
   */
  private DialogueShowData createDialogueShowData(ActionDTO action) {
    Character character = getCharacter((String) action.fields.get(CHARACTER_ID_FIELD));
    String text = (String) action.fields.get("text");
    boolean await = (boolean) action.fields.get(AWAIT_FIELD);
    return new DialogueShowData(character, text, await);
  }

  /**
   * Creates a dialogue hide data object from an action DTO.
   *
   * @param action the action DTO
   * @return the dialogue hide data object
   */
  private DialogueHideData createDialogueHideData(ActionDTO action) {
    boolean await = (boolean) action.fields.get(AWAIT_FIELD);
    return new DialogueHideData(await);
  }

  /**
   * Creates a goto data object from an action DTO.
   *
   * @param action the action DTO
   * @return the goto data object
   */
  private GotoData createGotoData(ActionDTO action) {
    String cutsceneId = (String) action.fields.get("cutsceneId");
    String beatId = (String) action.fields.get("beatId");
    return new GotoData(cutsceneId, beatId);
  }

  /**
   * Creates a parallel data object from an action DTO.
   *
   * @param action the action DTO
   * @return the parallel data object
   */
  private ParallelData createParallelData(ActionDTO action) {
    List<ActionData> parallelActions = getActions(action.actions);
    boolean await = (boolean) action.fields.get(AWAIT_FIELD);
    return new ParallelData(parallelActions, await);
  }

  /**
   * Gets a background by its id.
   *
   * @param id the id of the background
   * @return the background
   */
  public Background getBackground(String id) {
    for (Background background : backgrounds) {
      if (background.getId().equals(id)) {
        return background;
      }
    }

    return null;
  }

  /**
   * Gets a character by its id.
   *
   * @param id the id of the character
   * @return the character
   */
  public Character getCharacter(String id) {
    for (Character character : characters) {
      if (character.getId().equals(id)) {
        return character;
      }
    }

    return null;
  }

  /**
   * Gets a sound by its id.
   *
   * @param id the id of the sound
   * @return the sound
   */
  public Sound getSound(String id) {
    for (Sound sound : sounds) {
      if (sound.getId().equals(id)) {
        return sound;
      }
    }

    return null;
  }

  /**
   * Gets a beat by its id.
   *
   * @param id the id of the beat
   * @return the beat
   */
  public Beat getBeat(String id) {
    for (Beat beat : beats) {
      if (beat.getId().equals(id)) {
        return beat;
      }
    }

    return null;
  }
}
