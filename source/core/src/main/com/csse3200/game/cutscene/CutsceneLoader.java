package com.csse3200.game.cutscene;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.csse3200.game.cutscene.models.dto.*;

import java.util.*;

/**
 * Loads the JSON from a {@link FileHandle} into the relevant DTO classes.
 */
public class CutsceneLoader {
    private final JsonReader jsonReader = new JsonReader();

    public CutsceneDocDTO load(FileHandle fileHandle) {
        JsonValue root = jsonReader.parse(fileHandle);

        CutsceneDocDTO docDTO = new CutsceneDocDTO();
        docDTO.schemaVersion = root.getInt("schemaVersion");

        // characters
        docDTO.characters = new ArrayList<>();
        JsonValue charactersJson = root.get("characters");
        if (charactersJson != null) {
            for (JsonValue character : charactersJson) {
                CharacterDTO characterDTO = new CharacterDTO();
                characterDTO.id = character.getString("id");
                characterDTO.name = character.getString("name");
                characterDTO.poses = mapStringString(character.get("poses"));

                docDTO.characters.add(characterDTO);
            }
        }


        // Backgrounds
        docDTO.backgrounds = new ArrayList<>();
        JsonValue backgroundsJson = root.get("backgrounds");
        if (backgroundsJson != null) {
            for (JsonValue background : backgroundsJson) {
                BackgroundDTO backgroundDTO = new BackgroundDTO();
                backgroundDTO.id = background.getString("id");
                backgroundDTO.image = background.getString("image");

                docDTO.backgrounds.add(backgroundDTO);
            }
        }


        // Sounds
        docDTO.sounds = new ArrayList<>();
        JsonValue soundsJson = root.get("sounds");
        if (soundsJson != null) {
            for (JsonValue sound : soundsJson) {
                SoundDTO soundDTO = new SoundDTO();
                soundDTO.id = sound.getString("id");
                soundDTO.file = sound.getString("file");

                docDTO.sounds.add(soundDTO);
            }
        }


        // Cutscene
        docDTO.cutscene = parseCutscene(root.get("cutscene"));

        return docDTO;
    }

    private CutsceneDTO parseCutscene(JsonValue jsonValue) {
        CutsceneDTO cutsceneDTO = new CutsceneDTO();

        cutsceneDTO.id = jsonValue.getString("id");

        JsonValue beats = jsonValue.get("beats");
        if (beats == null) return cutsceneDTO;

        cutsceneDTO.beats = new ArrayList<>();

        for (JsonValue beat : beats) {
            cutsceneDTO.beats.add(parseBeat(beat));
        }

        return cutsceneDTO;
    }

    private BeatDTO parseBeat(JsonValue jsonValue) {
        BeatDTO beatDTO = new BeatDTO();

        beatDTO.id = jsonValue.getString("id");

        beatDTO.advance = parseAdvance(jsonValue.get("advance"));

        JsonValue actions = jsonValue.get("actions");
        if (actions == null) return beatDTO;

        beatDTO.actions = new ArrayList<>();

        for (JsonValue action : actions) {
            beatDTO.actions.add(parseAction(action));
        }

        return beatDTO;
    }

    private AdvanceDTO parseAdvance(JsonValue jsonValue) {
        AdvanceDTO advanceDTO = new AdvanceDTO();

        advanceDTO.mode = jsonValue.getString("mode");
        try{
            advanceDTO.delay = jsonValue.getInt("delay");
        } catch (IllegalArgumentException e) {
            advanceDTO.delay = null;
        }

        try {
            advanceDTO.signalKey = jsonValue.getString("signalKey");
        } catch (IllegalArgumentException e) {
            advanceDTO.signalKey = null;
        }

        return advanceDTO;
    }

    private ActionDTO parseAction(JsonValue jsonValue) {
        ActionDTO actionDTO = new ActionDTO();

        actionDTO.type = jsonValue.getString("type");

        JsonValue actions = jsonValue.get("actions");
        if (actions != null) {
            actionDTO.actions = new ArrayList<>();
            for (JsonValue action : actions) {
                actionDTO.actions.add(parseAction(action));
            }
        }

        Map<String, Object> kvPairs = mapAny(jsonValue);
        kvPairs.remove("type");
        kvPairs.remove("actions");

        actionDTO.fields.putAll(kvPairs);

        return actionDTO;
    }

    private Map<String, String> mapStringString (JsonValue jsonValue) {
        Map<String, String> stringStringMap = new HashMap<>();

        if (jsonValue == null) return stringStringMap;

        for (JsonValue child : jsonValue) {
            stringStringMap.put(child.name, child.asString());
        }

        return stringStringMap;
    }

    private Map<String, Object> mapAny (JsonValue jsonValue) {
        Map<String, Object> stringAnyMap = new HashMap<>();

        if (jsonValue == null) return stringAnyMap;

        for (JsonValue child : jsonValue) {
            stringAnyMap.put(child.name, getObject(child));
        }

        return stringAnyMap;
    }

    private Object getObject(JsonValue jsonValue) {
        switch (jsonValue.type()) {
            case stringValue:
                return jsonValue.asString();
            case booleanValue:
                return jsonValue.asBoolean();
            case doubleValue:
                return jsonValue.asDouble();
            case longValue:
                return jsonValue.asLong();
            case array:
                List<Object> list = new ArrayList<>();
                for (JsonValue child : jsonValue) {
                    list.add(getObject(child));
                }
                return list;
            case object:
                Map<String, Object> map = new HashMap<>();
                for (JsonValue child : jsonValue) {
                    map.put(child.name, getObject(child));
                }
                return map;
            default:
                return null;
        }
    }
}
