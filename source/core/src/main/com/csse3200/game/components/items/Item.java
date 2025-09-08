package com.csse3200.game.components.items;

import com.csse3200.game.components.Component;
import com.csse3200.game.entities.Entity;

public abstract class Item extends Component {
  protected String name;
  protected String description;
  protected String eventName;
  protected int cost;

  /**
   * Constructor for the Item class.
   *
   * @param name The name of the item.
   * @param description The description of the item.
   * @param eventName The event name associated with the item.
   * @param cost The cost of the item.
   */
  public Item(String name, String description, String eventName, int cost) {
    this.name = name;
    this.description = description;
    this.eventName = eventName;
    this.cost = cost;
  }

  /**
   * Gets the name of the item.
   *
   * @return The name of the item.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the description of the item.
   *
   * @return The description of the item.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the event name associated with the item.
   *
   * @return The event name associated with the item.
   */
  public String getEventName() {
    return eventName;
  }

  /**
   * Gets the cost of the item.
   *
   * @return The cost of the item.
   */
  public int getCost() {
    return cost;
  }

  /** Creates the item and sets up event listeners. */
  @Override
  public void create() {
    entity.getEvents().addListener("dropped", this::onPlaced);
  }

  /**
   * Function called when the entity receives the event "dropped". Triggers an appropriate event for
   * each target entity.
   *
   * @param entities an array of target entities.
   */
  protected void onPlaced(Entity[] entities) {
    for (Entity e : entities) {
      e.getEvents().trigger(eventName);
    }
    entity.dispose();
  }
}
