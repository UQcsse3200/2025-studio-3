package com.csse3200.game.components.skilltree;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.csse3200.game.progression.skilltree.Skill;
import com.csse3200.game.progression.skilltree.SkillSet;
import com.csse3200.game.services.DialogService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

/**
 * SkilltreeDisplay is a UI component responsible for rendering the skill tree interface. It
 * provides a background, displays a dialog when attempting to unlock a skill, and handles
 * unlock/close actions with appropriate sounds and UI updates.
 */
public class SkilltreeDisplay extends UIComponent {
  /** Creates a new SkilltreeDisplay. */
  public SkilltreeDisplay() {
    super();
  }

  /** Called when the component is created. Adds initial actors to the stage. */
  @Override
  public void create() {
    super.create();
    addActors();
  }

  /**
   * Displays a popup dialog for unlocking a skill. The dialog shows skill details, and provides
   * options to unlock the skill (if possible) or close the dialog.
   *
   * @param skill the skill being displayed
   * @param skillSet the player's current skill set
   * @param unlockedTexture texture to display when a skill is unlocked
   * @param skillButton the button representing the skill in the tree
   * @param stage the stage to which the popup will be added
   */
  public void unlockedPopUp(
      Skill skill, SkillSet skillSet, Texture unlockedTexture, Button skillButton, Stage stage) {
    DialogService dialogService = ServiceLocator.getDialogService();

    // Create the skill description message
    String message = skill.getDescription() + "\n\nCost: " + skill.getCost() + " points";

    // Create skill dialog with callbacks
    dialogService.skill(
        skill.getName(),
        message,
        // onUnlock callback
        dialog -> {
          int cost = skill.getCost();
          int points =
              ServiceLocator.getProfileService().getProfile().getWallet().getSkillsPoints();
          boolean locked = !skillSet.checkIfUnlocked(skill.getName());

          // Get set volume
          float volume = ServiceLocator.getSettingsService().getSoundVolume();

          // unlock skill conditions which removes skill points and replaces button if successful
          if (points >= cost && locked && skillSet.isUnlockable(skill.getName())) {
            skillSet.addSkill(skill);
            ServiceLocator.getProfileService().getProfile().getWallet().unlockSkill(cost);

            // Replace button with unlocked image
            Image unlockedImage = new Image(unlockedTexture);
            unlockedImage.setSize(skillButton.getWidth(), skillButton.getHeight());
            unlockedImage.setPosition(skillButton.getX(), skillButton.getY());
            skillButton.remove();
            Sound unlockSound =
                ServiceLocator.getResourceService()
                    .getAsset("sounds/button_unlock_skill.mp3", Sound.class);
            unlockSound.play(0.5f * volume);
            ServiceLocator.getProfileService()
                .getProfile()
                .getStatistics()
                .incrementStatistic("skillPointsSpent", cost);
            stage.addActor(unlockedImage);
            unlockedImage.setZIndex(1);
            dialog.hide();
          } else {
            // display corresponding error message
            if (cost > points) {
              dialogService.error("Error", "Not enough skill points for this purchase");
            } else if (!skillSet.isUnlockable(skill.getName())) {
              dialogService.error("Error", "Previous skills for this stat must be unlocked first.");
            }
          }
        },
        // onClose callback
        dialog -> {
          // Dialog automatically closes, no additional action needed
        });
  }

  /** Adds actors to the stage. */
  private void addActors() {
    // Title label centered at top
    Label titleLabel = ui.title("Town");
    float pad = ui.getScaledHeight(24f);
    titleLabel.setPosition(
        stage.getViewport().getWorldWidth() / 2f - titleLabel.getWidth() / 2f,
        stage.getViewport().getWorldHeight() - titleLabel.getHeight() - pad);
    titleLabel.setZIndex(3);
    stage.addActor(titleLabel);
    titleLabel.toFront();
  }
}
