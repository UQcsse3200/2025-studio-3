package com.csse3200.game.cutscene.runtime.components;

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
import com.csse3200.game.ui.UIComponent;

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
  private VerticalGroup choicesLeft;
  private VerticalGroup choicesCenter;
  private VerticalGroup choicesRight;

  private Table dialogueBox;
  private Label characterName;
  private Label text;

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
    choicesGroup.setDebug(true);
    choicesGroup.defaults().uniformX().expandX().fillY().pad(8f);
    choicesGroup.setDebug(true);

    choicesLeft = new VerticalGroup();
    choicesLeft.fill();
    choicesCenter = new VerticalGroup();
    choicesCenter.fill();
    choicesRight = new VerticalGroup();
    choicesRight.fill();

    choicesGroup.add(choicesLeft).fill().expand();
    choicesGroup.add(choicesCenter).fill().expand();
    choicesGroup.add(choicesRight).fill().expand();

    TextButton testButton1 = new TextButton("Test", skin);
    TextButton testButton2 = new TextButton("Test2", skin);
    TextButton testButton3 = new TextButton("Test3", skin);

    choicesLeft.addActor(testButton1);
    choicesCenter.addActor(testButton2);
    choicesRight.addActor(testButton3);

    //        root.add(choicesGroup).growX().fillX().row();

    // Make dialogue panel
    dialogueBox = new Table();
    dialogueBox.defaults().pad(20f);

    dialogueBox.setBackground(colorTexture(Color.CORAL));

    characterName = new Label("", skin);
    characterName.setFontScale(1.5f);
    characterName.setAlignment(Align.topLeft);

    text = new Label("", skin);
    text.setWrap(true);
    text.setAlignment(Align.topLeft);

    dialogueBox.add(characterName).top().left().padBottom(4f).row();
    dialogueBox.add(text).top().left().expand().fillX().padTop(0f);

    root.bottom().pad(20f);
    root.add(dialogueBox).growX().fillX().minHeight(Value.percentHeight(0.3f, root));
  }

  /**
   * Draw the renderable. Should be called only by the renderer, not manually.
   *
   * @param batch Batch to render to.
   */
  @Override
  protected void draw(SpriteBatch batch) {}

  @Override
  public void update() {
    OrchestratorState orchestratorState = orchestrator.state();

    if (backgroundImage.getDrawable() != orchestratorState.getBackgroundState().getImage()) {
      backgroundImage.setDrawable(orchestratorState.getBackgroundState().getImage());
    }
    backgroundImage.setColor(1, 1, 1, orchestratorState.getBackgroundState().getImageOpacity());
    if (oldBackgroundImage.getDrawable() != orchestratorState.getBackgroundState().getOldImage()) {
      oldBackgroundImage.setDrawable(orchestratorState.getBackgroundState().getOldImage());
    }

    dialogueBox.setVisible(orchestratorState.getDialogueState().isVisible());
    characterName.setText(orchestratorState.getDialogueState().getSpeaker());
    text.setText(orchestratorState.getDialogueState().getText());

    orchestratorState
        .getCharacterStatesList()
        .forEach(
            characterState -> {
              if (characterState.isOnScreen()) {
                Image spriteImage = null;
                if (characterState.getPosition() == Position.LEFT) {
                  spriteImage = (Image) leftPane.getChild(0);
                  characterState.getTexture().getSprite().setFlip(false, false);
                  leftPane.setOffsetX(spriteImage, characterState.getxOffset());
                  leftPane.relayout();
                } else if (characterState.getPosition() == Position.RIGHT) {
                  spriteImage = (Image) rightPane.getChild(0);
                  characterState.getTexture().getSprite().setFlip(true, false);
                  rightPane.setOffsetX(spriteImage, -characterState.getxOffset());
                  rightPane.relayout();
                }

                if (spriteImage != null) {
                  spriteImage.setDrawable(characterState.getTexture());
                }
              }
            });
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
