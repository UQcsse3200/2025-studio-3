package com.csse3200.game.components.skilltree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.csse3200.game.progression.skilltree.Skill;
import com.csse3200.game.progression.skilltree.SkillSet;

/**
 * SkilltreeActions is a click listener for skill tree buttons. It handles user interactions with
 * individual skill buttons:
 */
public class SkilltreeActions extends ClickListener {

  /** Name of the skill associated with this button. */
  private final String skillName;

  /** The player's set of skills. */
  private final SkillSet skillSet;

  /** Texture shown when the skill is unlocked. */
  private final Texture unlockedTexture;

  /** The UI button representing this skill. */
  private final Button skillButton;

  /** Stage where UI elements are displayed. */
  private final Stage stage;

  /** The skill tree display used to render the unlock popup. */
  private final SkilltreeDisplay display;

  /** Sound played when clicking a skill button. */
  private final Sound clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/skill_click.mp3"));

  /** Sound played when hovering over a skill button. */
  private final Sound hoverSound = Gdx.audio.newSound(Gdx.files.internal("sounds/skill_hover.mp3"));

  /**
   * Creates a new {@code SkilltreeActions} listener.
   *
   * @param skillName the name of the skill this button represents
   * @param skillSet the player's current skill set
   * @param unlockedTexture texture to display once the skill is unlocked
   * @param skillButton the button representing the skill
   * @param stage the stage to which UI components are added
   * @param display the skill tree display that manages popups
   */
  public SkilltreeActions(
      String skillName,
      SkillSet skillSet,
      Texture unlockedTexture,
      Button skillButton,
      Stage stage,
      SkilltreeDisplay display) {
    this.skillName = skillName;
    this.skillSet = skillSet;
    this.unlockedTexture = unlockedTexture;
    this.skillButton = skillButton;
    this.stage = stage;
    this.display = display;
    skillButton.setTransform(true);
  }

  /**
   * Called when the skill button is clicked. Plays a click sound and displays the unlock popup for
   * the associated skill.
   *
   * @param event the input event
   * @param x the x-coordinate of the click
   * @param y the y-coordinate of the click
   */
  @Override
  public void clicked(InputEvent event, float x, float y) {
    clickSound.play();
    Skill skill = skillSet.getSkill(skillName);
    display.unlockedPopUp(skill, skillSet, unlockedTexture, skillButton, stage);
  }

  /**
   * Called when the cursor enters the skill button area. Plays a hover sound and scales the button
   * up slightly for visual feedback.
   *
   * @param event the input event
   * @param x x-coordinate of the cursor
   * @param y y-coordinate of the cursor
   * @param pointer pointer index
   * @param fromActor the actor the cursor came from
   */
  @Override
  public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
    hoverSound.play();
    skillButton.setOrigin(Align.center);
    skillButton.addAction(Actions.scaleTo(1.1f, 1.1f, 0.1f));
  }

  /**
   * Called when the cursor exits the skill button area. Scales the button back to its original
   * size.
   *
   * @param event the input event
   * @param x x-coordinate of the cursor
   * @param y y-coordinate of the cursor
   * @param pointer pointer index
   * @param toActor the actor the cursor moves to
   */
  @Override
  public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
    skillButton.addAction(Actions.scaleTo(1f, 1f, 0.1f));
  }
}
