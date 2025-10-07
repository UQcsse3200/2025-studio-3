package com.csse3200.game.services;

import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.areas.SlotMachineArea;
import com.csse3200.game.entities.configs.BaseLevelConfig;

public class GameAreaService {
    private LevelGameArea gameArea;
    private String level;

    public GameAreaService(String level) {
        this.level = level;
        this.gameArea = createGameArea();
    }

    protected LevelGameArea createGameArea() {
        BaseLevelConfig cfg = ServiceLocator.getConfigService().getLevelConfig(level);
        if (cfg != null && cfg.isSlotMachine()) {
          return new SlotMachineArea(level);
        } else {
          return new LevelGameArea(level);
        }
    }

    public LevelGameArea getGameArea() {
        return gameArea;
    }

    public void create() {
        gameArea.create();
    }

    public void nextLevel() {
        this.gameArea.dispose();
        this.level = ServiceLocator.getConfigService().getLevelConfig(level).getNextLevel();
        this.gameArea = createGameArea();
        gameArea.create();
    }
}
