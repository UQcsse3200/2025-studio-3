package com.csse3200.game.data;

public class MenuSpriteDataEditor {
  private MenuSpriteData menuSpriteData;

  // Optional
  private Integer x;
  private Integer y;
  private String name;
  private String description;
  private String sprite_resource_path;
  private Boolean locked;

  public MenuSpriteDataEditor(MenuSpriteData menuSpriteData) {
    this.menuSpriteData = menuSpriteData;
  }

  public MenuSpriteDataEditor position(int x, int y) {
    this.x = x;
    this.y = y;
    return this;
  }

  public MenuSpriteDataEditor name(String name) {
    this.name = name;
    return this;
  }

  public MenuSpriteDataEditor description(String description) {
    this.description = description;
    return this;
  }

  public MenuSpriteDataEditor sprite(String sprite_resource_path) {
    this.sprite_resource_path = sprite_resource_path;
    return this;
  }

  public MenuSpriteDataEditor locked(boolean locked) {
    this.locked = locked;
    return this;
  }

  public void apply() {
    if (x != null) {
      menuSpriteData.setX(x);
    }
    if (x != null) {
      menuSpriteData.setY(y);
    }

    if (name != null) {
      menuSpriteData.setName(name);
    }
    if (description != null) {
      menuSpriteData.setDescription(description);
    }

    if (sprite_resource_path != null) {
      menuSpriteData.setSpriteResourcePath(sprite_resource_path);
    }

    if (locked != null) {
      menuSpriteData.setLocked(locked);
    }
  }
}
