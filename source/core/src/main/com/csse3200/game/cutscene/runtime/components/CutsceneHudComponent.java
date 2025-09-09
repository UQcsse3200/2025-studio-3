package com.csse3200.game.cutscene.runtime.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.csse3200.game.cutscene.runtime.CutsceneOrchestrator;
import com.csse3200.game.cutscene.runtime.OrchestratorState;
import com.csse3200.game.ui.UIComponent;

public class CutsceneHudComponent extends UIComponent {
    private final CutsceneOrchestrator orchestrator;
    private Table root;
    private Image backgroundImage;
    private Image oldBackgroundImage;

    private Table dialogueBox;
    private Label characterName;
    private Label text;

    /**
     * Initialise with a {@link CutsceneOrchestrator}
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void create() {
        super.create();

        backgroundImage = new Image(colorTexture(Color.LIGHT_GRAY));
        backgroundImage.setScaling(Scaling.fill);
        backgroundImage.setAlign(Align.center);
        backgroundImage.setFillParent(true);

        oldBackgroundImage = new Image(colorTexture(Color.LIGHT_GRAY));
        oldBackgroundImage.setScaling(Scaling.fill);
        oldBackgroundImage.setAlign(Align.center);
        oldBackgroundImage.setFillParent(true);

        stage.addActor(oldBackgroundImage);
        stage.addActor(backgroundImage);

        root = new Table();
        root.setFillParent(true);
        root.setTouchable(Touchable.enabled);
        root.toFront();
        root.setDebug(true); // TODO: Change in PROD

        stage.addActor(root);

        // Make dialogue panel
        dialogueBox = new Table();
        dialogueBox.defaults().pad(20f);

        dialogueBox.setBackground(colorTexture(Color.CORAL));

        characterName = new Label("Placeholder Name", skin);
        characterName.setFontScale(1.5f);
        characterName.setAlignment(Align.topLeft);

        text = new Label("Here is some really super duper long placeholder dialogue text that will need to be rendered, it's so long that it'll need to wrap", skin);
        text.setWrap(true);
        text.setAlignment(Align.topLeft);

        dialogueBox.add(characterName).top().left().padBottom(4f).row();
        dialogueBox.add(text).top().left().expand().fillX().padTop(0f);

        root.bottom().pad(20f);
        root.add(dialogueBox).growX().minHeight(Value.percentHeight(0.3f, root));
    }

    /**
     * Draw the renderable. Should be called only by the renderer, not manually.
     *
     * @param batch Batch to render to.
     */
    @Override
    protected void draw(SpriteBatch batch) {

    }

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
    }
}
