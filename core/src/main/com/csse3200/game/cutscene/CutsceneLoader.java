package com.csse3200.game.cutscene;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.csse3200.game.cutscene.models.dto.*;
import java.util.*;

/** Loads the JSON from a {@link FileHandle} into the relevant DTO classes. */
public class CutsceneLoader {
  private final JsonReader jsonReader = new JsonReader();

  public CutsceneDocDTO load(FileHandle fileHandle) {
    JsonValue root = jsonReader.parse(fileHandle);

    CutsceneDocDTO docDTO = new CutsceneDocDTO();
    docDTO.setSchemaVersion(root.getInt("schemaVersion"));

    // characters
    docDTO.setCharacters(new ArrayList<>());
    JsonValue charactersJson = root.get("characters");
    if (charactersJson != null) {
      for (JsonValue character : charactersJson) {
        CharacterDTO characterDTO = new CharacterDTO();
        characterDTO.setId(character.getString("id"));
        characterDTO.setName(character.getString("name"));
        characterDTO.setPoses(mapStringString(character.get("poses")));

        docDTO.getCharacters().add(characterDTO);
      }
    }

    // Backgrounds
    docDTO.setBackgrounds(new ArrayList<>());
    JsonValue backgroundsJson = root.get("backgrounds");
    if (backgroundsJson != null) {
      for (JsonValue background : backgroundsJson) {
        BackgroundDTO backgroundDTO = new BackgroundDTO();
        backgroundDTO.setId(background.getString("id"));
        backgroundDTO.setImage(background.getString("image"));

        docDTO.getBackgrounds().add(backgroundDTO);
      }
    }

    // Sounds
    docDTO.setSounds(new ArrayList<>());
    JsonValue soundsJson = root.get("sounds");
    if (soundsJson != null) {
      for (JsonValue sound : soundsJson) {
        SoundDTO soundDTO = new SoundDTO();
        soundDTO.setId(sound.getString("id"));
        soundDTO.setFile(sound.getString("file"));

        docDTO.getSounds().add(soundDTO);
      }
    }

    // Cutscene
    docDTO.setCutscene(parseCutscene(root.get("cutscene")));

    return docDTO;
  }

  private CutsceneDTO parseCutscene(JsonValue jsonValue) {
    CutsceneDTO cutsceneDTO = new CutsceneDTO();

    cutsceneDTO.setId(jsonValue.getString("id"));

    JsonValue beats = jsonValue.get("beats");
    if (beats == null) return cutsceneDTO;

    cutsceneDTO.setBeats(new ArrayList<>());

    for (JsonValue beat : beats) {
      cutsceneDTO.getBeats().add(parseBeat(beat));
    }

    return cutsceneDTO;
  }

  private BeatDTO parseBeat(JsonValue jsonValue) {
    BeatDTO beatDTO = new BeatDTO();

    beatDTO.setId(jsonValue.getString("id"));

    beatDTO.setAdvance(parseAdvance(jsonValue.get("advance")));

    JsonValue actions = jsonValue.get(CutsceneSchemaKeys.ACTIONS_KEY);
    if (actions == null) return beatDTO;

    beatDTO.setActions(new ArrayList<>());

    for (JsonValue action : actions) {
      beatDTO.getActions().add(parseAction(action));
    }

    return beatDTO;
  }

  private AdvanceDTO parseAdvance(JsonValue jsonValue) {
    AdvanceDTO advanceDTO = new AdvanceDTO();

    advanceDTO.setMode(jsonValue.getString("mode"));
    try {
      advanceDTO.setDelay(jsonValue.getInt("delay"));
    } catch (IllegalArgumentException e) {
      advanceDTO.setDelay(null);
    }

    try {
      advanceDTO.setSignalKey(jsonValue.getString("signalKey"));
    } catch (IllegalArgumentException e) {
      advanceDTO.setSignalKey(null);
    }

    return advanceDTO;
  }

  private ActionDTO parseAction(JsonValue jsonValue) {
    ActionDTO actionDTO = new ActionDTO();

    actionDTO.setType(jsonValue.getString("type"));

    JsonValue actions = jsonValue.get(CutsceneSchemaKeys.ACTIONS_KEY);
    if (actions != null) {
      actionDTO.setActions(new ArrayList<>());
      for (JsonValue action : actions) {
        actionDTO.getActions().add(parseAction(action));
      }
    }

    Map<String, Object> kvPairs = mapAny(jsonValue);
    kvPairs.remove("type");
    kvPairs.remove(CutsceneSchemaKeys.ACTIONS_KEY);

    actionDTO.getFields().putAll(kvPairs);

    return actionDTO;
  }

  private Map<String, String> mapStringString(JsonValue jsonValue) {
    Map<String, String> stringStringMap = new HashMap<>();

    if (jsonValue == null) return stringStringMap;

    for (JsonValue child : jsonValue) {
      stringStringMap.put(child.name, child.asString());
    }

    return stringStringMap;
  }

  private Map<String, Object> mapAny(JsonValue jsonValue) {
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
