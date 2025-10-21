package com.csse3200.game.cutscene.runtime.components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.csse3200.game.cutscene.models.object.Position;
import com.csse3200.game.cutscene.runtime.CutsceneOrchestrator;
import com.csse3200.game.cutscene.runtime.OrchestratorState;
import com.csse3200.game.cutscene.runtime.states.CharacterState;
import com.csse3200.game.cutscene.runtime.states.DialogueState;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import java.util.List;

public class CutsceneHudComponent extends UIComponent {
  private final CutsceneOrchestrator orchestrator;

  private Stack layers;

  private Table root;
  private Image backgroundImage;
  private Image oldBackgroundImage;

  private Table characterSprites;
  private PaneGroup leftPane;
  private PaneGroup rightPane;

  private Table choicesGroup;
  private Table choicesLeft;
  private Table choicesCenter;
  private Table choicesRight;
  private boolean choicesBound;

  private Table dialogueBox;
  private Label characterName;
  private Label text;
  private Label continueText;

  /**
   * Initialise with a {@link CutsceneOrchestrator}
   *
   * @param orchestrator The orchestrator used
   */
  public CutsceneHudComponent(CutsceneOrchestrator orchestrator) {
    this.orchestrator = orchestrator;
  }

  private static TextureRegionDrawable colorTexture(Color color) {
    Pixmap bg = new Pixmap(1, 1, Pixmap.Format.RGB565);
    bg.setColor(color);
    bg.fill();
    return new TextureRegionDrawable(new TextureRegion(new Texture(bg)));
  }

  public static TextureRegionDrawable loadImage(String path) {
    Texture texture = new Texture(path);
    return new TextureRegionDrawable(new TextureRegion(texture));
  }

  public static TextureRegionDrawable loadImage(Color color) {
    return colorTexture(color);
  }

  /** {@inheritDoc} */
  @Override
  public void create() {
    super.create();

    layers = new Stack();
    layers.setFillParent(true);
    stage.addActor(layers);

    backgroundImage = new Image(colorTexture(Color.LIGHT_GRAY));
    backgroundImage.setScaling(Scaling.fill);
    backgroundImage.setAlign(Align.center);
    backgroundImage.setFillParent(true);

    oldBackgroundImage = new Image(colorTexture(Color.LIGHT_GRAY));
    oldBackgroundImage.setScaling(Scaling.fill);
    oldBackgroundImage.setAlign(Align.center);
    oldBackgroundImage.setFillParent(true);

    layers.addActor(oldBackgroundImage);
    layers.addActor(backgroundImage);

    // Setup character sprites
    characterSprites = new Table();
    leftPane = new PaneGroup();
    Image leftImage = new Image();
    leftImage.setFillParent(true);
    leftImage.setScaling(Scaling.fit);
    leftImage.setAlign(Align.bottom);
    leftPane.addActor(leftImage);

    rightPane = new PaneGroup();
    Image rightImage = new Image();
    rightImage.setFillParent(true);
    rightImage.setScaling(Scaling.fit);
    rightImage.setAlign(Align.bottom);
    rightPane.addActor(rightImage);

    characterSprites.defaults().expandY().fillY();
    characterSprites
        .add(leftPane)
        .width(Value.percentWidth(0.3f, characterSprites))
        .expandY()
        .fillY();
    characterSprites.add().width(Value.percentWidth(0.4f, characterSprites)).expandY().fillY();
    characterSprites
        .add(rightPane)
        .width(Value.percentWidth(0.3f, characterSprites))
        .expandY()
        .fillY();

    characterSprites.top().padTop(Value.percentHeight(0.1f, characterSprites));

    layers.add(characterSprites);

    // Setup dialogue
    root = new Table();
    root.setFillParent(true);
    root.setTouchable(Touchable.enabled);
    root.toFront();
    root.setDebug(false);

    layers.addActor(root);

    // Setup choices
    choicesGroup = new Table();
    choicesGroup.defaults().pad(8f).space(6f).uniform();

    choicesLeft = new Table();
    choicesLeft.defaults().uniformX().expandX().fillX();
    choicesLeft.setClip(true);
    choicesCenter = new Table();
    choicesCenter.defaults().uniformX().expandX().fillX();
    choicesCenter.setClip(true);
    choicesRight = new Table();
    choicesRight.defaults().uniformX().expandX().fillX();
    choicesRight.setClip(true);

    choicesGroup.add(choicesLeft).expand().fill();
    choicesGroup.add(choicesCenter).expand().fill();
    choicesGroup.add(choicesRight).expand().fill();

    root.add(choicesGroup).growX().bottom().pad(0f, 1f, 12f, 1f).row();

    // Make dialogue panel
    dialogueBox = new Table();
    dialogueBox.defaults().pad(20f);

    dialogueBox.setBackground(skin.getDrawable("o"));

    characterName = ui.createLabel("", (int) (70 * ui.getUIScale()), Color.WHITE);
    characterName.setAlignment(Align.topLeft);

    text = ui.createLabel("", (int) (40 * ui.getUIScale()), Color.WHITE);
    text.setWrap(true);
    text.setAlignment(Align.topLeft);

    continueText = ui.text("");

    dialogueBox.add(characterName).top().left().padBottom(4f).row();
    dialogueBox.add(text).top().left().expand().fillX().padTop(0f).row();
    dialogueBox.add(continueText);

    root.bottom().pad(20f);
    root.add(dialogueBox).growX().fillX().minHeight(Value.percentHeight(0.3f, root));
  }

  /**
   * Draw the renderable. Should be called only by the renderer, not manually.
   *
   * @param batch Batch to render to.
   */
  @Override
  protected void draw(SpriteBatch batch) {
    // No drawing needed
  }

  void updateBackground() {
    OrchestratorState orchestratorState = orchestrator.state();

    if (backgroundImage.getDrawable() != orchestratorState.getBackgroundState().getImage()) {
      backgroundImage.setDrawable(orchestratorState.getBackgroundState().getImage());
    }
    backgroundImage.setColor(1, 1, 1, orchestratorState.getBackgroundState().getImageOpacity());
    if (oldBackgroundImage.getDrawable() != orchestratorState.getBackgroundState().getOldImage()) {
      oldBackgroundImage.setDrawable(orchestratorState.getBackgroundState().getOldImage());
    }
  }

  void updateCharacters() {
    OrchestratorState orchestratorState = orchestrator.state();

    orchestratorState
        .getCharacterStatesList()
        .forEach(
            characterState -> {
              if (characterState.isOnScreen()) {
                Image spriteImage = characterState.getImage();

                if (characterState.getPosition() == Position.LEFT) {
                  characterState.getTexture().getSprite().setFlip(false, false);
                  characterState.updateImage();
                  leftPane.getImage(spriteImage).setxOffset(characterState.getxOffset());
                  leftPane.getImage(spriteImage).setyOffset(characterState.getyOffset());
                  leftPane.relayout();
                } else if (characterState.getPosition() == Position.RIGHT) {
                  characterState.getTexture().getSprite().setFlip(true, false);
                  characterState.updateImage();
                  rightPane.getImage(spriteImage).setxOffset(-characterState.getxOffset());
                  rightPane.getImage(spriteImage).setyOffset(-characterState.getyOffset());
                  rightPane.relayout();
                }

                if (!characterState.isOnScreen()) {
                  if (characterState.getPosition() == Position.LEFT) {
                    leftPane.removeImage(spriteImage);
                  } else if (characterState.getPosition() == Position.RIGHT) {
                    rightPane.removeImage(spriteImage);
                  }
                }
              }
            });
  }

  void cleanupCharacters() {
    OrchestratorState orchestratorState = orchestrator.state();

    // Clean up any dead ones
    List<Image> leftStates =
        orchestratorState.getCharacterStatesList().stream()
            .filter(characterState -> characterState.getPosition() == Position.LEFT)
            .map(CharacterState::getImage)
            .toList();
    List<Image> rightStates =
        orchestratorState.getCharacterStatesList().stream()
            .filter(characterState -> characterState.getPosition() == Position.RIGHT)
            .map(CharacterState::getImage)
            .toList();

    for (Image image : leftPane.getImagesKeys()) {
      if (!leftStates.contains(image)) {
        leftPane.removeImage(image);
      }
    }

    for (Image image : rightPane.getImagesKeys()) {
      if (!rightStates.contains(image)) {
        rightPane.removeImage(image);
      }
    }
  }

  Table getChoicesGroup() {
    if (!leftPane.getImagesKeys().isEmpty() && !rightPane.getImagesKeys().isEmpty()) {
      return choicesCenter;
    } else if (leftPane.getImagesKeys().isEmpty() && !rightPane.getImagesKeys().isEmpty()) {
      return choicesRight;
    } else if (rightPane.getImagesKeys().isEmpty() && !leftPane.getImagesKeys().isEmpty()) {
      return choicesLeft;
    }
    return choicesLeft;
  }

  void updateChoices() {
    OrchestratorState orchestratorState = orchestrator.state();

    if (orchestratorState.getChoiceState().isActive() && !choicesBound) {
      choicesGroup.setVisible(true);
      choicesLeft.clearChildren();
      choicesCenter.clearChildren();
      choicesRight.clearChildren();
      Table choiceGroup = getChoicesGroup();

      for (Button button : orchestratorState.getChoiceState().getChoices()) {
        if (button instanceof TextButton tb) tb.setFillParent(false);
        if (button.getParent() == null) {
          choiceGroup
              .add(button)
              .minWidth(0)
              .prefWidth(Value.percentWidth(1f, choiceGroup))
              .maxWidth(Value.percentWidth(1f, choiceGroup))
              .fillX()
              .height(48f)
              .padTop(10f)
              .row();
        }
      }
      choicesBound = true;
    } else if (!orchestratorState.getChoiceState().isActive() && choicesBound) {
      choicesLeft.clearChildren();
      choicesCenter.clearChildren();
      choicesRight.clearChildren();
      choicesBound = false;
    }

    if (!orchestratorState.getChoiceState().isActive()) {
      choicesGroup.setVisible(false);
    }
  }

  @Override
  public void update() {
    OrchestratorState orchestratorState = orchestrator.state();

    updateBackground();

    DialogueState dialogueState = orchestratorState.getDialogueState();

    dialogueBox.setVisible(dialogueState.isVisible());
    characterName.setText(dialogueState.getSpeaker());
    text.setText(dialogueState.getText());

    String skipKey =
        Input.Keys.toString(ServiceLocator.getSettingsService().getSettings().getSkipButton());

    if (!orchestratorState.getChoiceState().isActive()) {
      if (dialogueState.isDone()) {
        continueText.setText("Press \"" + skipKey + "\" to continue");
      } else {
        continueText.setText("Press \"" + skipKey + "\" to skip");
      }
    } else {
      continueText.setText("");
    }

    updateCharacters();
    cleanupCharacters();

    updateChoices();
  }

  @Override
  public void dispose() {
    super.dispose();
    layers.remove();
    root.remove();
    backgroundImage.remove();
    oldBackgroundImage.remove();
    characterSprites.remove();
    leftPane.remove();
    rightPane.remove();
    choicesGroup.remove();
    choicesLeft.remove();
    choicesCenter.remove();
    choicesRight.remove();
    dialogueBox.remove();
    characterName.remove();
    text.remove();
  }
}
