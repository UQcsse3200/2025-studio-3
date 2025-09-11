package com.csse3200.game.components.items;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.components.Component;
import com.csse3200.game.services.ServiceLocator;

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
  protected Item(String name, String description, String eventName, int cost) {
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

  //  /**
  //   * Function called when the entity receives the event "dropped". Triggers an appropriate event
  // for
  //   * each target entity.
  //   *
  //   * @param entities an array of target entities.
  //   */ //not public?
  public void onPlaced(Vector2 position, int tileSize) {
    // e.getEvents().trigger(eventName);

    // this.getComponent(ItemEffect.class);
    if (this.getName() != null) {
      // entity.getEvents().trigger("itemUsed", this.getName(), position, tileSize);
      // ItemEffect.playEffect(getName(), position, tileSize);
      ServiceLocator.getItemEffectsService().playEffect(getName(), position, tileSize);
    }
  }
}
