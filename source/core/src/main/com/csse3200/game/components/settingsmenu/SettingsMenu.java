package com.csse3200.game.components.settingsmenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.GdxGame;
import com.csse3200.game.GdxGame.ScreenType;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.ButtonFactory;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Main settings menu component. */
public class SettingsMenu extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(SettingsMenu.class);
  private final GdxGame game;
  private Table rootTable;

  public SettingsMenu(GdxGame game) {
    super();
    this.game = game;
  }

  @Override
  public void create() {
    super.create();
    addActors();
    entity.getEvents().addListener("backtosettingsmenu", this::showMenu);
    entity.getEvents().addListener("gamesettings", this::hideMenu);
    entity.getEvents().addListener("displaysettings", this::hideMenu);
    entity.getEvents().addListener("audiosettings", this::hideMenu);
  }

  private void addActors() {
    rootTable = new Table();
    rootTable.setFillParent(true);

    // Add top menu row (title, exit, apply)
    rootTable.add(makeMenuBtns()).expandX().fillX().top();

    // Next row: settings table
    rootTable.row().padTop(30f);
    showMainMenu();

    stage.addActor(rootTable);
  }

  private void showMainMenu() {
    // Create main menu buttons
    TextButton displayBtn = ButtonFactory.createButton("Display Settings");
    TextButton gameBtn = ButtonFactory.createButton("Game Settings");
    TextButton audioBtn = ButtonFactory.createButton("Audio Settings");

    displayBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            entity.getEvents().trigger("displaysettings");
          }
        });

    gameBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            entity.getEvents().trigger("gamesettings");
          }
        });

    audioBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            entity.getEvents().trigger("audiosettings");
          }
        });

    Table menuTable = new Table();
    menuTable.add(displayBtn).size(200f, 50f).padBottom(20f);
    menuTable.row();
    menuTable.add(gameBtn).size(200f, 50f).padBottom(20f);
    menuTable.row();
    menuTable.add(audioBtn).size(200f, 50f);

    rootTable.add(menuTable).expandX().expandY();
  }

  private Table makeMenuBtns() {
    // Exit button (from main branch)
    ImageButton exitBtn =
        new ImageButton(
            new TextureRegionDrawable(
                ServiceLocator.getGlobalResourceService()
                    .getAsset("images/ui/close-icon.png", Texture.class)));
    exitBtn.setSize(60f, 60f);
    exitBtn.setPosition(
        20f, // padding from left
        stage.getHeight() - 60f - 20f // padding from top
        );
    exitBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Exit button clicked");
            exitMenu();
          }
        });
    stage.addActor(exitBtn);

    // Apply button
    TextButton applyBtn = ButtonFactory.createButton("Apply");
    applyBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Apply button clicked");
            applyChanges();
            if (!ServiceLocator.getProfileService().isActive()) {
              game.setScreen(ScreenType.MAIN_MENU);
            } else {
              game.setScreen(ScreenType.WORLD_MAP);
            }
          }
        });

    // Title
    Label title = new Label("Settings", skin, "title");

    Table table = new Table();
    table.setFillParent(true);
    table.top().padTop(10f).padLeft(10f).padRight(10f);
    table.add(title).expandX().center();

    // Apply button bottom-right
    Table bottomRow = new Table();
    bottomRow.setFillParent(true);
    bottomRow.bottom().right().pad(20f);
    bottomRow.add(applyBtn).size(100f, 50f);
    stage.addActor(bottomRow);

    return table;
  }

  private void applyChanges() {
    // Main menu doesn't have specific settings to apply
    logger.debug("Main settings menu - no changes to apply");
  }

  private void exitMenu() {
    if (!ServiceLocator.getProfileService().isActive()) {
      game.setScreen(ScreenType.MAIN_MENU);
    } else {
      game.setScreen(ScreenType.WORLD_MAP);
    }
  }

  private void showMenu() {
    rootTable.setVisible(true);
  }

  private void hideMenu() {
    rootTable.setVisible(false);
  }

  @Override
  protected void draw(SpriteBatch batch) {
    // draw is handled by the stage
  }

  @Override
  public void update() {
    stage.act(ServiceLocator.getTimeSource().getDeltaTime());
  }

  @Override
  public void dispose() {
    rootTable.clear();
    super.dispose();
  }

}
