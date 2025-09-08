package com.csse3200.game.data;

import com.badlogic.gdx.Screen;

public final class MenuSpriteDataBuilder {
  // Required
  private final Class<? extends Screen> screenClass;
  private final Enum<?> id;

  // Optional
  private Integer x;
  private Integer y;
  private String name;
  private String description;
  private String sprite_resource_path;
  private Boolean locked;

  public MenuSpriteDataBuilder(Screen screen, Enum<?> id) {
    this.screenClass = screen.getClass();
    this.id = id;
  }

  public MenuSpriteDataBuilder position(int x, int y) {
    this.x = x;
    this.y = y;
    return this;
  }

  public MenuSpriteDataBuilder name(String name) {
    this.name = name;
    return this;
  }

  public MenuSpriteDataBuilder description(String description) {
    this.description = description;
    return this;
  }

  public MenuSpriteDataBuilder sprite(String sprite_resource_path) {
    this.sprite_resource_path = sprite_resource_path;
    return this;
  }

  public MenuSpriteDataBuilder locked(boolean locked) {
    this.locked = locked;
    return this;
  }

  public MenuSpriteData build() {
    // apply defaults if they aren't entered
    if (x == null) {
      x = 0;
    }
    if (y == null) {
      y = 0;
    }
    if (sprite_resource_path == null) {
      // get default path
    }
    if (locked == null) {
      locked = false;
    }

    return new MenuSpriteData(
        screenClass, id, x, y, name, description, sprite_resource_path, locked);
  }
}
