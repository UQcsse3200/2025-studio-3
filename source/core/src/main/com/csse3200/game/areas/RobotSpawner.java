package com.csse3200.game.areas;

import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseEntityConfig;
import com.csse3200.game.entities.factories.RobotFactory;

public class RobotSpawner {
    private final GameArea gameArea;

    public RobotSpawner(GameArea gameArea) {
        this.gameArea = gameArea;
    }

    /** Spawn a single robot with default config at position (x,y). */
    public Entity spawnRobot(float x, float y) {
        BaseEntityConfig cfg = new BaseEntityConfig();
        cfg.health = 10;
        cfg.baseAttack = 2;

        Entity robot = RobotFactory.createRobot(cfg);
        gameArea.spawnEntity(robot);
        robot.setPosition(x, y);
        return robot;
    }
}