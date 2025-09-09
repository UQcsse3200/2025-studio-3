package com.csse3200.game.cutscene.models.object;

/**
 * The different audio busses that sound can be played on.
 */
public enum AudioBus {
    /**
     * For (usually) single, quick audio effects.
     * E.g. explosion, door opening, crash.
     */
    SFX,

    /**
     * For long-lasting and (usually) looping soundtracks.
     * E.g. ambiance, or background music.
     */
    MUSIC;

    /**
     * Maps JSON values {@code "sfx"|"music"} to their {@code AudioBus} counterparts.
     *
     * @param value - String value to be converted
     * @return {@code AudioBus} value corresponding to the input string (sfx by default).
     */
    public static AudioBus fromString(String value) {
        return switch(value) {
            case "sfx" -> SFX;
            case "music" -> MUSIC;
            default -> SFX;
        };
    }
}
