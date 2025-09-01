package com.csse3200.game.components.items;

import com.csse3200.game.components.Component;
import com.csse3200.game.entities.Entity;

public class DefaultItemComponent extends Component {

    /**
     * Function used to create item component.
     */
    @Override
    public void create() {
        // assuming drag & drop, listen for dropped event (assumed successful placement)
        entity.getEvents().addListener("dropped", this::onDropped);
    }

    /**
     * Base function to be overridden by subclasses, to retrieve the name of the
     * event to be triggered in OnDropped.
     *
     * @return the name of the event to be triggered. Default NULL
     */
    protected String getEventName() {
        return "NULL";
    }

    /**
     * Function called when the entity receives the event "dropped".
     * Triggers an appropriate event for each target entity.
     *
     * @param entities an array of target entities.
     */
    private void onDropped(Entity[] entities) {
        String event = getEventName();
        for (Entity e : entities) {
            e.getEvents().trigger(event);
        }
        entity.dispose();
    }

}
