package com.csse3200.game.cutscene.models.object;

import com.csse3200.game.cutscene.models.object.actiondata.ActionData;

import java.util.List;

/**
 * Stores beat data.
 */
public class Beat {
    private String id;
    private Advance advance;
    private List<ActionData> actions;

    /**
     * Creates a {@code Beat} object with specified id, advance, and actions
     * @param id       ID of the beat
     * @param advance  The {@link Advance} object for the beat
     * @param actions  A List of {@link ActionData} (actions) that the beat executes
     */
    public Beat(String id, Advance advance, List<ActionData> actions) {
        this.id = id;
        this.advance = advance;
        this.actions = actions;
    }

    public String getId() {
        return id;
    }

    public Advance getAdvance() {
        return advance;
    }

    public List<ActionData> getActions() {
        return List.copyOf(actions);
    }
}
