package com.csse3200.game.components.skilltree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.progression.skilltree.Skill;
import com.csse3200.game.progression.skilltree.SkillSet;
import com.csse3200.game.services.DialogService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

/**
 * SkilltreeDisplay is a UI component responsible for rendering the skill tree interface. It
 * provides a background, displays a popup when attempting to unlock a skill, and handles
 * unlock/close actions with appropriate sounds and UI updates.
 */
public class SkilltreeDisplay extends UIComponent {
  /** Style for popup windows. */
  private final Window.WindowStyle windowStyle;

  /** Sound effect for invalid or failed actions. */
  private final Sound errorSound = Gdx.audio.newSound(Gdx.files.internal("sounds/error_click.mp3"));

  /** Sound effect for successful skill unlocks. */
  private final Sound unlockSound =
      Gdx.audio.newSound(Gdx.files.internal("sounds/button_unlock_skill.mp3"));

  /** Creates a new SkilltreeDisplay and applies the default skin's window style. */
  public SkilltreeDisplay() {
    super();
    this.windowStyle = skin.get(Window.WindowStyle.class);
  }

  /** Called when the component is created. Adds initial actors to the stage. */
  @Override
  public void create() {
    super.create();
    addActors();
  }

  /**
   * Draw method for custom rendering. Currently unused since this class relies on Scene2D UI
   * elements.
   *
   * @param batch the SpriteBatch used for rendering
   */
  @Override
  protected void draw(SpriteBatch batch) {
    // Custom drawing can be added here if needed
  }

  /**
   * Displays a popup window for unlocking a skill. The popup shows skill details, and provides
   * options to unlock the skill (if possible) or close the popup.
   *
   * @param skill the skill being displayed
   * @param skillSet the player's current skill set
   * @param unlockedTexture texture to display when a skill is unlocked
   * @param skillButton the button representing the skill in the tree
   * @param stage the stage to which the popup will be added
   */
  public void unlockedPopUp(
      Skill skill, SkillSet skillSet, Texture unlockedTexture, Button skillButton, Stage stage) {
    Window popup = new Window(skill.getStatType().name(), windowStyle);
    popup.setModal(true);
    popup.setMovable(false);

    // Main content table with padding
    Table contentTable = new Table();
    contentTable.pad(20f);

    // Skill name label
    Label skillNameLabel = new Label(skill.getName(), skin, "title");
    skillNameLabel.setColor(Color.WHITE);
    contentTable.add(skillNameLabel).colspan(2).center().padBottom(10f).row();

    // Skill description
    Label skillDescLabel = new Label(skill.getDescription(), skin);
    skillDescLabel.setColor(Color.LIGHT_GRAY);
    skillDescLabel.setWrap(true);
    contentTable.add(skillDescLabel).colspan(2).width(300f).center().padBottom(15f).row();

    // Skill cost label
    Label skillCostLabel = new Label("Cost: " + skill.getCost() + " points", skin);
    skillCostLabel.setColor(Color.GOLD);
    contentTable.add(skillCostLabel).colspan(2).center().padBottom(20f).row();

    // Buttons table
    Table buttonTable = new Table();

    // Unlock button
    TextButton unlockButton = new TextButton("Unlock", skin);
    unlockButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            int cost = skill.getCost();
            int points =
                ServiceLocator.getProfileService().getProfile().getWallet().getSkillsPoints();
            boolean locked = !skillSet.checkIfUnlocked(skill.getName());

            // unlock skill conditions which removes skill points and replaces button if successful
            if (points >= cost && locked && skillSet.isUnlockable(skill.getName())) {
              skillSet.addSkill(skill);
              ServiceLocator.getProfileService().getProfile().getWallet().unlockSkill(cost);

              // Replace button with unlocked image
              Image unlockedImage = new Image(unlockedTexture);
              unlockedImage.setSize(skillButton.getWidth(), skillButton.getHeight());
              unlockedImage.setPosition(skillButton.getX(), skillButton.getY());
              skillButton.remove();
              unlockSound.play();

              stage.addActor(unlockedImage);
              popup.remove();
            } else {
              errorSound.play();
              DialogService dialogService = ServiceLocator.getDialogService();
              // display corresponding error message
              if (cost > points) {
                dialogService.error("Error", "Not enough skill points for this purchase");
              } else if (!skillSet.isUnlockable(skill.getName())) {
                dialogService.error(
                    "Error", "Previous skills for this stat must be unlocked first.");
              }
            }
          }
        });

    // Close button
    TextButton closeButton = new TextButton("Close", skin);
    closeButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            popup.remove();
          }
        });

    buttonTable.add(unlockButton).padRight(10f);
    buttonTable.add(closeButton);
    contentTable.add(buttonTable).colspan(2).center();

    popup.add(contentTable);
    popup.pack();
    popup.setPosition(
        (stage.getWidth() - popup.getWidth()) / 2f, (stage.getHeight() - popup.getHeight()) / 2f);

    stage.addActor(popup);
  }

  /** Adds actors to the stage, including the background image for the skill tree. */
  private void addActors() {
    Image backgroundImage =
        new Image(
            ServiceLocator.getResourceService()
                .getAsset("images/skilltree_background.jpg", Texture.class));

    backgroundImage.setFillParent(true);
    stage.addActor(backgroundImage);
  }
}
