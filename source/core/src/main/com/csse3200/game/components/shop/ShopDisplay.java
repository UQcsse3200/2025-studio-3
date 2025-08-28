package com.csse3200.game.components.shop;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.GdxGame;
import com.csse3200.game.GdxGame.ScreenType;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

/**
 * 
 */
public class ShopDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(ShopDisplay.class);
  private static final float Z_INDEX = 2f;
  private static final int SLOT_SIZE = 100;
  private final GdxGame game;
  private ImageButton[] itemSlots = new ImageButton[3];
  private String[] itemNames = new String[3];
  private String[] availableItems = { "heart.png", "box_boy.png", "tree.png" };

  public ShopDisplay(GdxGame game) {
    super();
    this.game = game;
  }

  /**
   * Creates the shop display.
   */
  @Override
  public void create() {
    super.create();
    addActors();
  }

  private void addActors() {
    Image backgroundImage = new Image(
        ServiceLocator.getResourceService()
            .getAsset("images/shopbackground2.png", Texture.class));

    backgroundImage.setFillParent(true);
    stage.addActor(backgroundImage);
    createItemSlots();
    makeBackButton();
  }

  private Table makeBackButton() {
    TextButton backButton = new TextButton("Back", skin);

    backButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Exit button clicked");
            exitMenu();
          }
        });

    Table table = new Table();
    table.add(backButton).expandX().left().pad(0f, 15f, 15f, 0f);
    return table;
  }

  private void createItemSlots() {
    // Position slots in the center area, spaced horizontally
    float centerX = stage.getWidth() / 2f;
    float centerY = stage.getHeight() / 2f;
    float slotSpacing = 150f; // Space between slots

    for (int i = 0; i < 3; i++) {
      // Calculate position for each slot
      float slotX = centerX - slotSpacing + (i * slotSpacing);
      float slotY = centerY - SLOT_SIZE / 2f;

      // Create slot with the corresponding item
      createItemSlot(i, slotX, slotY, availableItems[i]);
    }
  }

  private void createItemSlot(int slotIndex, float x, float y, String itemTexture) {
    // Load the item texture
    Texture itemTex = ServiceLocator.getResourceService()
        .getAsset("images/" + itemTexture, Texture.class);

    // Create ImageButton with the item texture
    ImageButton slot = new ImageButton(new TextureRegionDrawable(itemTex));

    // Position and size the slot
    slot.setPosition(x, y);
    slot.setSize(SLOT_SIZE, SLOT_SIZE);

    // Add click listener
    slot.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent changeEvent, Actor actor) {
        logger.debug("Item slot {} clicked ({})", slotIndex, itemTexture);
        onItemSlotClicked(slotIndex, itemTexture);
      }
    });

    // Store reference and add to stage
    itemSlots[slotIndex] = slot;
    stage.addActor(slot);
  }

  private void onItemSlotClicked(int slotIndex, String itemTexture) {
    logger.info("Selected item: {} from slot {}", itemTexture, slotIndex);
    entity.getEvents().trigger("item_selected", slotIndex, itemTexture);
  }

  private void exitMenu() {
    game.setScreen(ScreenType.MAIN_MENU);
  }

  @Override
  public void draw(SpriteBatch batch) {
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void dispose() {
    super.dispose();
  }

}
