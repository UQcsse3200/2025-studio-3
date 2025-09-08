package com.csse3200.game.components.slot;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Slot machine UI display with responsive sizing and layout.
 * <p>
 * - Entry icon is anchored to the bottom-right corner (with margin).<br>
 * - Popup frame is always centered and scales with the short screen edge.<br>
 * - Layout updates on both create() and resize().
 * </p>
 */
public class SlotMachineDisplay extends UIComponent {
    private static final Logger logger = LoggerFactory.getLogger(SlotMachineDisplay.class);

    /** Render order for this UI. */
    private static final float Z_INDEX = 3f;

    /** Size ratios relative to the stage's short edge. */
    private static final float ICON_SIZE_RATIO  = 0.10f; // 12% of min(width, height)
    private static final float MARGIN_RATIO     = 0.02f; // 2%  of min(width, height)
    private static final float FRAME_SIZE_RATIO = 0.80f; // 70% of min(width, height)

    // Runtime-computed sizes
    private float iconSizePx;
    private float marginPx;
    private float frameSizePx;

    // UI elements
    private ImageButton slotIconBtn;
    private Group frameGroup;
    private Image frameImage;
    private TextureRegionDrawable frameUpDrawable;
    private TextureRegionDrawable frameDownDrawable;

    @Override
    public void create() {
        super.create();
        createPopupFrame();
        createSlotIconButton();
        computeResponsiveSizes();
        applyLayout();
    }

    /**
     * Create the bottom-right entry icon and toggle popup on click.
     * Assumes "images/slot_icon.png" has been loaded by ResourceService.
     */
    private void createSlotIconButton() {
        Texture iconTex = ServiceLocator.getResourceService()
                .getAsset("images/slot_icon.png", Texture.class);

        slotIconBtn = new ImageButton(new TextureRegionDrawable(iconTex));

        slotIconBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean show = !frameGroup.isVisible();
                frameGroup.setVisible(show);
            }
        });

        stage.addActor(slotIconBtn);
    }

    /**
     * Create popup frame group and image with press-down-for-1s animation.
     */
    private void createPopupFrame() {
        frameGroup = new Group();
        frameGroup.setSize(stage.getWidth(), stage.getHeight());
        frameGroup.setPosition(0f, 0f);
        frameGroup.setTransform(false);
        frameGroup.setVisible(false);

        TextureAtlas atlas = ServiceLocator.getResourceService()
                .getAsset("images/slot_frame.atlas", TextureAtlas.class);
        TextureRegion upRegion   = atlas.findRegion("slot_frame_up");
        TextureRegion downRegion = atlas.findRegion("slot_frame_down");
        frameUpDrawable   = new TextureRegionDrawable(upRegion);
        frameDownDrawable = new TextureRegionDrawable(downRegion);

        frameImage = new Image(frameUpDrawable);
        frameImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                logger.info("Slot frame clicked");
                frameImage.clearActions();
                frameImage.setDrawable(frameDownDrawable);
                frameImage.addAction(Actions.sequence(
                        Actions.delay(1f),
                        Actions.run(() -> frameImage.setDrawable(frameUpDrawable))
                ));
            }
        });

        frameGroup.addActor(frameImage);
        stage.addActor(frameGroup);
    }

    /** Compute pixel sizes based on the current stage dimensions. */
    private void computeResponsiveSizes() {
        float w = stage.getWidth();
        float h = stage.getHeight();
        float base = Math.min(w, h);

        iconSizePx  = base * ICON_SIZE_RATIO;
        marginPx    = base * MARGIN_RATIO;
        frameSizePx = base * FRAME_SIZE_RATIO;
    }

    /**
     * Apply current sizes/positions:
     * - frameGroup covers the whole stage
     * - frameImage is square, centered
     * - slotIconBtn anchored bottom-right with margin
     */
    private void applyLayout() {
        // Ensure the frame container matches stage size
        if (frameGroup != null) {
            frameGroup.setSize(stage.getWidth(), stage.getHeight());
            frameGroup.setPosition(0f, 0f);
        }

        // Center the popup frame
        if (frameImage != null) {
            frameImage.setSize(frameSizePx, frameSizePx);
            float gx = (frameGroup.getWidth()  - frameSizePx) / 2f;
            float gy = (frameGroup.getHeight() - frameSizePx) / 2f;
            frameImage.setPosition(gx, gy);
        }

        // Bottom-right corner
        if (slotIconBtn != null) {
            slotIconBtn.setSize(iconSizePx, iconSizePx);
            float x = stage.getWidth()  - iconSizePx - marginPx;
            float y = marginPx;
            slotIconBtn.setPosition(x, y);
        }
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
        if (slotIconBtn != null) slotIconBtn.remove();
        if (frameGroup != null) frameGroup.remove();
    }
}
