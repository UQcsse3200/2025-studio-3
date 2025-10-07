package com.csse3200.game.components.dossier;

import com.csse3200.game.GdxGame;

public class DossierBackAction {

    private final GdxGame game;

    public DossierBackAction(GdxGame game) {
        this.game = game;
    }

    /** Handles logic for returning to the world map. */
    /** Handles navigation back to the World Map Screen. */
    public void backMenu() {
        game.setScreen(GdxGame.ScreenType.WORLD_MAP);
    }
}
