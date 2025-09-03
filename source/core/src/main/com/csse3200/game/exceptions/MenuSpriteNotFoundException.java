package com.csse3200.game.exceptions;

public class MenuSpriteNotFoundException extends Exception {

    public MenuSpriteNotFoundException(String message) {
        super(message);
    }

    public MenuSpriteNotFoundException() {
        super("Could not find the menu sprite specified");
    }
}
