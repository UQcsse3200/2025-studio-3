package com.csse3200.game.minigame;

import com.badlogic.gdx.Input;
import com.csse3200.game.input.InputComponent;

public class MiniGameInputComponent extends InputComponent {
public MiniGameInputComponent() {
    super(5);
}
@Override
public boolean keyDown(int key) {
    switch (key) {
        case Input.Keys.LEFT:
            case Input.Keys.A:
           entity.getEvents().trigger("moveLeft");
            return true;
        case Input.Keys.RIGHT:
        case Input.Keys.D:
            entity.getEvents().trigger("moveRight");
            return true;
            default:
                return false;
    }
}

}
