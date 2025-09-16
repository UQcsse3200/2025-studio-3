package com.csse3200.game.components.gameover;

import com.csse3200.game.entities.Entity;

public class GameOverEntityFactory {
    private GameOverEntityFactory() {}
    public static Entity createGameOver() {
        Entity gameOver = new Entity()
                .addComponent(new GameOverWindow());
        return gameOver;
    }

}
