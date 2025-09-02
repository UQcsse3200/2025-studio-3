package com.csse3200.game.components.items;

import com.csse3200.game.components.Component;
import com.csse3200.game.entities.Entity;

/**
 * Abstract base class for all in-game items.
 * <p>
 * Items are components that can be attached to entities and trigger events
 * when used or placed in the game world. Subclasses should specify the
 * particular details for each item type. Effects of items are handled
 * by the receiving defensive/offensive entities.
 * </p>
 */
public abstract class Item extends Component {

    protected String name; // display name
    protected String eventName; // name of event triggered on entities
    protected float cost; // cost, in coins
    protected String desc; // Shop description


    /**
     * Initializes the item component.
     * <p>
     * This method is called by the game engine when the component is added to an entity.
     * It begins listening for the "dropped" event, triggering the onDropped function.
     * The dropped event should be triggered with an array of all affected entities as a
     * parameter.
     * </p>
     */
    @Override
    public void create() {
        // assuming drag & drop, listen for dropped event (assumed successful placement)
        entity.getEvents().addListener("dropped", this::onDropped);
    }

    /**
     * Function called when this entity receives the event "dropped".
     * Triggers an appropriate event for each target entity.
     *
     * @param entities an array of target entities.
     */
    protected void onDropped(Entity[] entities) {
        String event = getEventName();

        for (Entity e : entities) {
            e.getEvents().trigger(event);
        }
        entity.dispose();
    }

    // getters
    public String getName() {
        return name;
    }

    public String getEventName() {
        return eventName;
    }

    public float getCost() {
        return cost;
    }

    public String getDesc() {
        return desc;
    }
}
