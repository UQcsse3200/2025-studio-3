package com.csse3200.game.cutscene;

import com.badlogic.gdx.utils.JsonValue;
import com.csse3200.game.cutscene.models.dto.*;
import com.csse3200.game.cutscene.models.object.*;
import com.csse3200.game.cutscene.models.object.Character;
import com.csse3200.game.cutscene.models.object.actiondata.*;

import java.awt.desktop.SystemSleepEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CutsceneCompiler {
    private String id;
    private List<Character> characters;
    private List<Background> backgrounds;
    private List<Sound> sounds;
    private List<Beat> beats;

    /**
     * Compiles a {@link CutsceneDocDTO} into a {@link Cutscene}
     * No error checking is handled in this method as it assumed that the {@link CutsceneDocDTO} has already been validated.
     *
     * @param cutsceneDocDTO The {@link CutsceneDocDTO} to be compiled
     * @return A {@link Cutscene} with all the information filled out.
     */
    public Cutscene compile(CutsceneDocDTO cutsceneDocDTO) {
        id = cutsceneDocDTO.cutscene.id;
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
            String id = beatDTO.id;
            Advance advance = switch (beatDTO.advance.mode) {
                case "auto" -> Advance.auto();
                case "input" -> Advance.input();
                case "auto_delay" -> Advance.autoDelay(beatDTO.advance.delay);
                case "signal" -> Advance.signal(beatDTO.advance.signalKey);
                default -> Advance.auto();
            };
            List<ActionData> actions = getActions(beatDTO.actions);

            beats.add(new Beat(id, advance, actions));
        }

        return new Cutscene(id, characters, backgrounds, sounds, beats);
    }

    private List<ActionData> getActions(List<ActionDTO> actionsData) {
        List<ActionData> actions = new ArrayList<>();

        for (ActionDTO action : actionsData) {
            switch (action.type) {
                case "audio.play": {
                    AudioBus bus = AudioBus.fromString((String) action.fields.get("bus"));
                    Sound sound = getSound((String) action.fields.get("soundId"));
                    Float volume = ((Double) action.fields.get("volume")).floatValue();
                    Float pitch = null;
                    if (action.fields.get("pitch") != null) {
                        pitch = ((Double) action.fields.get("pitch")).floatValue();
                    }
                    Float pan = null;
                    if (action.fields.get("pan") != null) {
                        pan = ((Double) action.fields.get("pan")).floatValue();
                    }
                    boolean loop = false;
                    if (action.fields.get("loop") != null) {
                        loop = (boolean) action.fields.get("loop");
                    }

                    boolean await = (boolean) action.fields.get("await");

                    actions.add(new AudioPlayData(bus, sound, volume, pitch, pan, loop, await));
                    break;
                }
                case "audio.set": {
                    AudioBus bus = AudioBus.fromString((String) action.fields.get("bus"));
                    Float volume = ((Double) action.fields.get("volume")).floatValue();

                    actions.add(new AudioSetData(bus, volume));
                    break;
                }
                case "audio.stop": {
                    AudioBus bus = AudioBus.fromString((String) action.fields.get("bus"));
                    int fadeMs = ((Long)action.fields.get("fadeMs")).intValue();
                    boolean await = (boolean) action.fields.get("await");

                    actions.add(new AudioStopData(bus, fadeMs, await));
                    break;
                }
                case "background.set": {
                    Background background = getBackground((String) action.fields.get("backgroundId"));
                    Transition transition = Transition.fromString((String) action.fields.get("transition"));
                    int duration = ((Long)action.fields.get("duration")).intValue();
                    boolean await = (boolean) action.fields.get("await");

                    actions.add(new BackgroundSetData(background, transition, duration, await));
                    break;
                }
                case "character.enter": {
                    Character character = getCharacter((String) action.fields.get("characterId"));
                    String pose = (String) action.fields.get("pose");
                    Position position = Position.fromString((String) action.fields.get("position"));
                    Transition transition = Transition.fromString((String) action.fields.get("transition"));
                    int duration = ((Long)action.fields.get("duration")).intValue();
                    boolean await = (boolean) action.fields.get("await");

                    actions.add(new CharacterEnterData(character, pose, position, transition, duration, await));
                    break;
                }
                case "character.exit": {
                    Character character = getCharacter((String) action.fields.get("characterId"));
                    Transition transition = Transition.fromString((String) action.fields.get("transition"));
                    int duration = ((Long)action.fields.get("duration")).intValue();
                    boolean await = (boolean) action.fields.get("await");

                    actions.add(new CharacterExitData(character, transition, duration, await));
                    break;
                }
                case "choice": {
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

                    actions.add(new ChoiceData(prompt, choices));
                    break;
                }
                case "dialogue.chorus": {
                    List<Character> chorusCharacters = new ArrayList<>();
                    String text = (String) action.fields.get("text");
                    boolean await = (boolean) action.fields.get("await");

                    for (Object characterId : (List<?>) action.fields.get("characterIds")) {
                        chorusCharacters.add(getCharacter((String) characterId));
                    }

                    actions.add(new DialogueChorusData(chorusCharacters, text, await));
                    break;
                }
                case "dialogue.show": {
                    Character character = getCharacter((String) action.fields.get("characterId"));
                    String text = (String) action.fields.get("text");
                    boolean await = (boolean) action.fields.get("await");

                    actions.add(new DialogueShowData(character, text, await));
                    break;
                }
                case "goto": {
                    String cutsceneId = (String) action.fields.get("cutsceneId");

                    actions.add(new GotoData(cutsceneId, (String) action.fields.get("beatId")));
                    break;
                }
                case "parallel": {
                    List<ActionData> parallelActions = getActions(action.actions);
                    boolean await = (boolean) action.fields.get("await");

                    actions.add(new ParallelData(parallelActions, await));
                    break;
                }
            }
        }

        return actions;
    }

    private Background getBackground(String id) {
        for (Background background : backgrounds) {
            if (background.getId().equals(id)) {
                return background;
            }
        }

        return null;
    }

    private Character getCharacter(String id) {
        for (Character character : characters) {
            if (character.getId().equals(id)) {
                return character;
            }
        }

        return null;
    }

    private Sound getSound(String id) {
        for (Sound sound : sounds) {
            if (sound.getId().equals(id)) {
                return sound;
            }
        }

        return null;
    }

    private Beat getBeat(String id) {
        for (Beat beat : beats) {
            if (beat.getId().equals(id)) {
                return beat;
            }
        }

        return null;
    }
}
