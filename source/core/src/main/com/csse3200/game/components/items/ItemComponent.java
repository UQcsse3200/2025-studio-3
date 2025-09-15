package com.csse3200.game.components.items;

import com.csse3200.game.components.Component;

/**
 * ItemComponent provides the ability to add an Item Type to an Entity for the purpose of
 * identifying the particular Item an animation Entity is representing.
 *
 * <p>This Component tags an Entity as an Item and stores its Type.
 */
public class ItemComponent extends Component {

  /** Enumerators for the supported Item categories */
  public enum Type {
    GRENADE,
    COFFEE,
    BUFF,
    EMP,
    NUKE
  }

  /** Immutable item type assigned upon construction of ItemComponent. */
  private final Type type;

  /**
   * Creates a new ItemComponent with the given Type.
   *
   * @param type the Item type (enumerators)
   */
  public ItemComponent(Type type) {
    this.type = type;
  }

  /**
   * Returns the Item Type associated with the component.
   *
   * @return Type type of Item
   */
  public Type getType() {
    return type;
  }
}
