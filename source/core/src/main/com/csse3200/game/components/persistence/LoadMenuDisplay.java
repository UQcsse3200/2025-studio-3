package com.csse3200.game.components.persistence;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.persistence.Savefile;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.ButtonFactory;
import com.csse3200.game.ui.UIComponent;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A UI component for displaying the load menu with current saves. */
public class LoadMenuDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(LoadMenuDisplay.class);
  private static final float Z_INDEX = 2f;
  private Table table;
  private List<Savefile> saveFiles;

  @Override
  public void create() {
    super.create();
    saveFiles = Persistence.fetch();
    addActors();
  }

  /** Add the actors to the table. */
  private void addActors() {
    table = new Table();
    table.setFillParent(true);

    // Back button positioned at top-left with close icon
    ImageButton backBtn =
        new ImageButton(
            new TextureRegionDrawable(
                ServiceLocator.getGlobalResourceService()
                    .getAsset("images/ui/close-icon.png", Texture.class)));
    backBtn.setSize(60f, 60f);
    backBtn.setPosition(
        20f, // 20f padding from left
        stage.getHeight() - 60f - 20f // 20f padding from top
        );
    backBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Back button clicked");
            entity.getEvents().trigger("back");
          }
        });

    // Title
    Label titleLabel = new Label("LOAD GAME", skin, "large");

    // Create save slot buttons
    TextButton[] saveSlotButtons = new TextButton[3];
    for (int i = 0; i < 3; i++) {
      if (saveFiles.get(i) != null) {
        // Active save slot
        Savefile save = saveFiles.get(i);
        String buttonText = save.getDisplayName() + "\n" + save.getDisplayDate();
        saveSlotButtons[i] = ButtonFactory.createButton(buttonText);

        final int slotIndex = i;
        saveSlotButtons[i].addListener(
            new ChangeListener() {
              @Override
              public void changed(ChangeEvent changeEvent, Actor actor) {
                logger.debug(
                    "Save slot {} clicked: {}", slotIndex, saveFiles.get(slotIndex).getName());
                entity.getEvents().trigger("loadGame", saveFiles.get(slotIndex));
              }
            });
      } else {
        // Empty save slot
        saveSlotButtons[i] = ButtonFactory.createButton("Empty");
        saveSlotButtons[i].setDisabled(true);
      }
    }

    // Main content table
    Table contentTable = new Table();
    contentTable.setFillParent(true);
    contentTable.center();

    contentTable.add(titleLabel).padBottom(50f);
    contentTable.row();

    // Add save slots with consistent sizing and spacing
    for (int i = 0; i < saveSlotButtons.length; i++) {
      contentTable.add(saveSlotButtons[i]).width(400f).height(80f).padBottom(20f);
      contentTable.row();
    }

    // Add both tables to stage
    stage.addActor(backBtn);
    stage.addActor(contentTable);
  }

  @Override
  public void draw(SpriteBatch batch) {
    // Do nothing, handled by the stage
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void dispose() {
    if (table != null) {
      table.clear();
    }
    super.dispose();
  }
}
