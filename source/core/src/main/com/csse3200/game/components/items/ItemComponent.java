package com.csse3200.game.components.items;

import com.csse3200.game.components.Component;

public class ItemComponent extends Component {
  public enum Type {
    GRENADE,
    COFFEE,
    BUFF,
    EMP,
    NUKE
  }

  private final Type type;

  public ItemComponent(Type type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }
}
