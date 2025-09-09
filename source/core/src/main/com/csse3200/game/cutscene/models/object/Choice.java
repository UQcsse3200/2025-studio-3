package com.csse3200.game.cutscene.models.object;

/**
 * Stores Choice data.
 */
public class Choice {
    private String type;
    private String line;
    private String cutsceneId;
    private String entryBeatId;

    /**
     * Creates a {@code Choice} object with type, line, cutsceneId, and entryBeatId
     * @param type         Type of choice
     * @param line         The text of the choice
     * @param cutsceneId   The ID of the cutscene to go to
     * @param entryBeatId  The beat of the cutscene to go to
     */
    public Choice(String type, String line, String cutsceneId, String entryBeatId) {
        this.type = type;
        this.line = line;
        this.cutsceneId = cutsceneId;
        this.entryBeatId = entryBeatId;
    }

    public String getType() {
        return type;
    }

    public String getLine() {
        return line;
    }

    public String getCutsceneId() {
        return cutsceneId;
    }

    public String getEntryBeatId() {
        return entryBeatId;
    }
}
