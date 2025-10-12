package com.csse3200.game.cutscene;

public final class CutsceneSchemaKeys {
    private CutsceneSchemaKeys() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static final String AWAIT_FIELD = "await";
    public static final String TRANSITION_FIELD = "transition";
    public static final String DURATION_FIELD = "duration";
    public static final String CHARACTER_ID_FIELD = "characterId";
    public static final String POSITION_FIELD = "position";
    public static final String ACTIONS_KEY = "actions";
    public static final String SOUND_ID_FIELD = "soundId";

    public static final String BEATS_PATH = "doc.cutscene.beats.";
    public static final String CHARACTERS_PATH = "doc.characters.";

    public static final String ACTIONS_SUB = ".action.*";

    public static final String ACTION_ERROR_PREFIX = "ACTION_";
    public static final String NULL_SUFFIX = "_NULL";

    public static final String CANNOT_BE_NULL = " can not be null";
}
