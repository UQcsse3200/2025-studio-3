package com.csse3200.game.utils;

/**
 * Enum for the names of all the levels and nodes on the world map.
 */
public enum LevelType {
    TOWN,
    MINIGAMES,
    SHOP,
    LEVEL_ONE,
    LEVEL_TWO,
    LEVEL_THREE,
    LEVEL_FOUR,
    LEVEL_FIVE;

    /**
     * Converts the level to the display name.
     * 
     * @return The display name of the level.
     */
    @Override
    public String toString() {
      return switch (this) {
        case TOWN -> "Town";
        case MINIGAMES -> "Arcade";
        case SHOP -> "Shop";
        case LEVEL_ONE -> "Level 1";
        case LEVEL_TWO -> "Level 2";
        case LEVEL_THREE -> "Level 3";
        case LEVEL_FOUR -> "Level 4";
        case LEVEL_FIVE -> "Level 5";
      };
    }

    /**
     * Converts the level to the key used in the config
     */
    public String toKey() {
      return switch (this) {
        case TOWN -> "town";
        case MINIGAMES -> "minigames";
        case SHOP -> "shop";
        case LEVEL_ONE -> "levelOne";
        case LEVEL_TWO -> "levelTwo";
        case LEVEL_THREE -> "levelThree";
        case LEVEL_FOUR -> "levelFour";
        case LEVEL_FIVE -> "levelFive";
      };
    }
  }