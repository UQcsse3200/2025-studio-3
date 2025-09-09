package com.csse3200.game.minigame;

import com.badlogic.gdx.Input;
import com.csse3200.game.input.InputComponent;

public class MinigameInputComponent extends InputComponent {
private final PlayerLaneManager playerLaneManager;
public MinigameInputComponent(PlayerLaneManager playerLaneManager) {

    this.playerLaneManager = playerLaneManager;
}
@Override
public boolean keyDown(int keycode) {
    switch (keycode) {
        case Input.Keys.LEFT:
            playerLaneManager.movePlayertoLane(0);
            return true;
        case Input.Keys.DOWN:
            playerLaneManager.movePlayertoLane(1);
            return true;
            case Input.Keys.RIGHT:
            playerLaneManager.movePlayertoLane(2);
            return true;
            default:
                return false;
    }
}

}
