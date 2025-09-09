package com.csse3200.game.components.dossier;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.GdxGame;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DossierDisplay extends UIComponent {
    private static final Logger logger = LoggerFactory.getLogger(DossierDisplay.class);
    private final GdxGame game;
    private Table rootTable;

    public DossierDisplay(GdxGame game) {
        super();
        this.game = game;
    }

    @Override
    public void create() {
        super.create();
        addActors();
    }

    private void addActors() {
        Label title = new Label("Dossier", skin, "title");
        Table backBtn = makeBackBtn();

        rootTable = new Table();
        rootTable.setFillParent(true);

        rootTable.add(title).expandX().top().padTop(20f);

        stage.addActor(rootTable);
        stage.addActor(backBtn);
    }

    private Table makeBackBtn() {
        TextButton backBtn = new TextButton("Back", skin);

        // Add listener for the back button
        backBtn.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        logger.debug("Back button clicked");
                        backMenu();
                    }
                });

        // Place button in a table
        Table table = new Table();
        table.setFillParent(true);
        table.top().left().pad(15f);
        table.add(backBtn);
        return table;
    }

    /** Handles navigation back to the Profile Screen. */
    private void backMenu() {
        game.setScreen(GdxGame.ScreenType.PROFILE);
    }

    @Override
    protected void draw(SpriteBatch batch) {
        // draw is handled by the stage
    }

    /** Disposes of this UI component. */
    @Override
    public void dispose() {
        rootTable.clear();
        super.dispose();
    }
}
