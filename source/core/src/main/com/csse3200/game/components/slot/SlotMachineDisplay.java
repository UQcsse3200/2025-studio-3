package com.csse3200.game.components.slot;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * SlotMachineDisplay renders and controls a 3-reel slot machine UI with responsive layout.
 * It provides: a bottom-right launcher icon, a centered popup frame, and reel spin/stop logic
 * that can land on predefined target indices.
 */
public class SlotMachineDisplay extends UIComponent {
    private static final Logger logger = LoggerFactory.getLogger(SlotMachineDisplay.class);

    /**
     * Render order for this UI.
     */
    private static final float Z_INDEX = 3f;

    /**
     * Size ratios relative to the stage's short edge.
     */
    private static final float ICON_SIZE_RATIO = 0.10f;
    private static final float MARGIN_RATIO = 0.02f;
    private static final float FRAME_SIZE_RATIO = 0.80f;
    /**
     * Normalized layout ratios inside the frame for the reels area.
     */
    private static final float REELS_AREA_W_RATIO = 0.68f;
    private static final float REELS_AREA_H_RATIO = 0.40f;
    private static final float REELS_AREA_Y_OFFSET = 0.00f;
    private static final int REEL_COUNT = 3;
    private static final float REEL_GAP_RATIO = 0.03f;
    private final List<Group> reelColumns = new ArrayList<>();
    // Target symbol indices (per column) where reels should stop. (now for test)
    private int[] targetIndices;
    /**
     * Per-column symbol metrics and runtime state used for smooth stopping.
     */
    private float symbolHeights;
    private int symbolCounts;
    private final List<Float> currentScrollSpeeds = new ArrayList<>();
    // Runtime-computed sizes
    private float iconSizePx;
    private float marginPx;
    private float frameSizePx;
    // UI elements
    private ImageButton slotIconBtn;
    private Group frameGroup;
    private Image frameImage;
    private Image reelsBgImage;
    private Image dimmer;
    private TextureRegionDrawable frameUpDrawable;
    private TextureRegionDrawable frameDownDrawable;
    // reels
    private ScrollPane reelsPane;
    private Group reelsContent;
    private List<TextureAtlas.AtlasRegion> symbolRegions;
    private int stoppedCount = 0;
    /**
     * Base reel scroll speed in pixels per second.
     */
    private float reelScrollSpeedPxPerSec;
    /**
     * Whether a spin sequence is currently active.
     */
    private boolean spinning = false;

    /**
     * Initializes UI hierarchy, loads resources, computes sizes, and applies layout.
     */
    @Override
    public void create() {
        super.create();
        createPopupFrame();
        createSlotIconButton();
        loadReelsAtlasAndRegions();
        computeResponsiveSizes();
        applyLayout();
    }

    /**
     * The bottom-right launcher icon and toggling behavior.
     */
    private void createSlotIconButton() {
        Texture iconTex = ServiceLocator.getResourceService()
                .getAsset("images/slot_icon.png", Texture.class);

        slotIconBtn = new ImageButton(new TextureRegionDrawable(iconTex));
        slotIconBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean willShow = !frameGroup.isVisible();
                if (willShow) {
                    showSlotPopup();
                } else {
                    if (spinning) {
                        return;
                    }
                    hideSlotPopup();
                }
            }
        });
        stage.addActor(slotIconBtn);
    }

    /**
     * Popup frame + frame press animation + START on click
     *
     */
    private void createPopupFrame() {
        frameGroup = new Group();
        frameGroup.setSize(stage.getWidth(), stage.getHeight());
        frameGroup.setPosition(0f, 0f);
        frameGroup.setTransform(false);
        frameGroup.setVisible(false);

        dimmer = new Image(skin.getDrawable("black"));
        dimmer.setSize(stage.getWidth(), stage.getHeight());
        dimmer.setPosition(0f, 0f);
        dimmer.getColor().a = 0.6f;
        dimmer.setTouchable(Touchable.enabled);
        dimmer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.stop();
                if (spinning) {
                    return;
                }
                hideSlotPopup();
            }
        });
        frameGroup.addActor(dimmer);

        TextureAtlas atlas = ServiceLocator.getResourceService()
                .getAsset("images/slot_frame.atlas", TextureAtlas.class);
        TextureRegion upRegion = atlas.findRegion("slot_frame_up");
        TextureRegion downRegion = atlas.findRegion("slot_frame_down");
        frameUpDrawable = new TextureRegionDrawable(upRegion);
        frameDownDrawable = new TextureRegionDrawable(downRegion);

        Texture reelsBgTex = ServiceLocator.getResourceService()
                .getAsset("images/slot_reels_background.png", Texture.class);
        reelsBgImage = new Image(reelsBgTex);
        reelsBgImage.setTouchable(Touchable.disabled);
        frameGroup.addActor(reelsBgImage);

        reelsContent = new Group();
        reelsPane = new ScrollPane(reelsContent);
        reelsPane.setScrollingDisabled(true, true);
        reelsPane.setFadeScrollBars(false);
        reelsPane.setOverscroll(false, false);
        frameGroup.addActor(reelsPane);

        frameImage = new Image(frameUpDrawable);
        frameImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                logger.info("Slot frame clicked");
                targetIndices = new int[]{3, 3, 3};     // Logic
                frameImage.clearActions();
                frameImage.setDrawable(frameDownDrawable);
                frameImage.addAction(Actions.sequence(
                        Actions.delay(0.15f),
                        Actions.run(() -> frameImage.setDrawable(frameUpDrawable))
                ));
                startSpinThenStopAtTargets();
            }
        });

        frameGroup.addActor(frameImage);
        stage.addActor(frameGroup);
    }

    /**
     * Loads the reels atlas and collects symbol regions.
     */
    private void loadReelsAtlasAndRegions() {
        TextureAtlas reelsAtlas = ServiceLocator.getResourceService()
                .getAsset("images/slot_reels.atlas", TextureAtlas.class);
        symbolRegions = new ArrayList<>();
        for (TextureAtlas.AtlasRegion r : reelsAtlas.getRegions()) {
            symbolRegions.add(r);
        }
        symbolRegions.sort(Comparator.comparing(a -> a.name));
        if (symbolRegions.isEmpty()) {
            logger.warn("No symbol regions found.");
        }
    }

    /**
     * Builds a single reel column by duplicating the symbol sequence twice
     * to enable seamless vertical looping.
     *
     * @param colWidth  column width
     * @param colHeight column height
     * @return the reel column group
     */
    private Group buildOneReel(float colWidth, float colHeight) {
        Group reel = new Group();
        float y = 0f;

        if (symbolRegions == null || symbolRegions.isEmpty()) {
            reel.setSize(colWidth, colHeight);
            return reel;
        }

        symbolCounts = symbolRegions.size();

        TextureRegion first = symbolRegions.getFirst();
        float scale = Math.min(colWidth / first.getRegionWidth(), colHeight / first.getRegionHeight());
        float wStd = first.getRegionWidth() * scale;
        float hStd = first.getRegionHeight() * scale;
        symbolHeights = hStd;

        for (int k = 0; k < 2; k++) {
            for (TextureRegion r : symbolRegions) {
                Image img = new Image(r);
                img.setSize(wStd, hStd);
                img.setPosition((colWidth - wStd) / 2f, y);
                y += hStd;
                reel.addActor(img);
            }
        }

        reel.setSize(colWidth, y);
        reel.setPosition(0f, 0f);
        return reel;
    }

    /**
     * Rebuilds all reels and resets per-column states.
     */
    private void constructReels(float areaX, float areaY, float areaW, float areaH) {
        reelsContent.clearChildren();
        reelColumns.clear();

        if (symbolRegions == null || symbolRegions.isEmpty()) {
            return;
        }

        float gap = frameSizePx * REEL_GAP_RATIO;
        float colW = (areaW - gap * (REEL_COUNT - 1)) / REEL_COUNT;
        float colH = areaH;

        reelsPane.setSize(areaW, areaH);
        reelsPane.setPosition(areaX, areaY);

        for (int i = 0; i < REEL_COUNT; i++) {
            Group col = buildOneReel(colW, colH);
            col.setPosition(i * (colW + gap), 0f);
            reelColumns.add(col);
            reelsContent.addActor(col);
        }

        currentScrollSpeeds.clear();
        for (int i = 0; i < REEL_COUNT; i++) {
            currentScrollSpeeds.add(0f);
        }

    }

    /**
     * Compute pixel sizes based on the current stage dimensions.
     *
     */
    private void computeResponsiveSizes() {
        float w = stage.getWidth();
        float h = stage.getHeight();
        float base = Math.min(w, h);

        iconSizePx = base * ICON_SIZE_RATIO;
        marginPx = base * MARGIN_RATIO;
        frameSizePx = base * FRAME_SIZE_RATIO;

        reelScrollSpeedPxPerSec = frameSizePx * 1.2f;
    }

    /**
     * Randomize reel positions when opening slot machine
     */
    private void randomizeReelPositions() {
        if (reelColumns.isEmpty() || symbolCounts <= 0) return;

        float h = symbolHeights;
        float centerOffset = (reelsPane.getHeight() - h) / 2f;

        for (Group col : reelColumns) {
            int randIndex = (int)(Math.random() * symbolCounts);
            float targetY = -(randIndex * h) + centerOffset;
            col.setY(targetY);
        }
    }

    /**
     * Applies positions/sizes for frame, reels area and launcher icon.
     */
    private void applyLayout() {
        if (frameGroup != null) {
            frameGroup.setSize(stage.getWidth(), stage.getHeight());
            frameGroup.setPosition(0f, 0f);
        }

        if (dimmer != null) {
            dimmer.setSize(stage.getWidth(), stage.getHeight());
            dimmer.setPosition(0f, 0f);
        }

        if (frameImage != null) {
            frameImage.setSize(frameSizePx, frameSizePx);
            float gx = (frameGroup.getWidth() - frameSizePx) / 2f;
            float gy = (frameGroup.getHeight() - frameSizePx) / 2f;
            frameImage.setPosition(gx, gy);

            if (reelsBgImage != null) {
                reelsBgImage.setSize(frameSizePx, frameSizePx);
                reelsBgImage.setPosition(gx, gy);
            }

            float areaW = frameSizePx * REELS_AREA_W_RATIO;
            float areaH = frameSizePx * REELS_AREA_H_RATIO;
            float areaX = gx + (frameSizePx - areaW) / 2f;
            float areaY = gy + (frameSizePx - areaH) / 2f + frameSizePx * REELS_AREA_Y_OFFSET;

            constructReels(areaX, areaY, areaW, areaH);
        }


        if (slotIconBtn != null) {
            slotIconBtn.setSize(iconSizePx, iconSizePx);
            float x = stage.getWidth() - iconSizePx - marginPx;
            float y = marginPx;
            slotIconBtn.setPosition(x, y);
        }
    }

    /**
     * Starts looped scrolling for all reels and schedules staggered smooth stop landing on target.
     */
    private void startSpinThenStopAtTargets() {
        if (spinning) {
            logger.info("Already spinning.");
            return;
        }

        stoppedCount = 0;
        spinning = true;

        for (int i = 0; i < reelColumns.size(); i++) {
            final int colIndex = i;
            Group col = reelColumns.get(i);
            col.clearActions();

            float oneCycle = symbolHeights * symbolCounts;
            float speed = reelScrollSpeedPxPerSec * (0.9f + 0.1f * i);

            ScrollAction loop = new ScrollAction(
                    8f,
                    speed,
                    oneCycle,
                    col.getY(),
                    v -> currentScrollSpeeds.set(colIndex, v)
            );
            col.addAction(loop);
        }

        for (int i = 0; i < reelColumns.size(); i++) {
            final int colIndex = i;
            float delay = 1.2f + 0.6f * i;

            Group col = reelColumns.get(i);
            col.addAction(Actions.sequence(
                    Actions.delay(delay),
                    Actions.run(() -> smoothStopColumnAtTarget(colIndex))
            ));
        }
    }

    /**
     * Smoothly stops a given column at its target index, avoiding large bounce-back
     * by optionally advancing to the next equivalent lap when over half a symbol height.
     *
     * @param colIdx reel column index
     */
    private void smoothStopColumnAtTarget(int colIdx) {
        if (colIdx < 0 || colIdx >= reelColumns.size()) return;

        Group col = reelColumns.get(colIdx);
        col.clearActions();

        float h = symbolHeights;
        int baseCount = symbolCounts;
        float oneCycle = h * baseCount;

        int rawIndex = targetIndices[colIdx];
        int target = ((rawIndex % baseCount) + baseCount) % baseCount;

        float centerOffset = (reelsPane != null ? (reelsPane.getHeight() - h) / 2f : 0f);
        float idealY = -(target * h) + centerOffset;

        float currentY = col.getY();

        float kNearest = Math.round((currentY - idealY) / (-oneCycle));
        float mappedY = idealY - kNearest * oneCycle;

        float v0 = (colIdx < currentScrollSpeeds.size()) ? currentScrollSpeeds.get(colIdx) : -reelScrollSpeedPxPerSec;

        float delta = mappedY - currentY;
        float halfSymbol = 0.5f * h;

        if (v0 < -1e-3f) {
            if (delta > 0f && Math.abs(delta) > halfSymbol) {
                mappedY -= oneCycle;
                delta = mappedY - currentY;
            }
        } else if (v0 > 1e-3f) {
            if (delta < 0f && Math.abs(delta) > halfSymbol) {
                mappedY += oneCycle;
                delta = mappedY - currentY;
            }
        }

        float distance = Math.abs(delta);
        float base = Math.max(20f, Math.abs(v0));
        float tSuggested = distance / (base * 0.9f);
        float t = Math.max(0.40f, Math.min(1.10f, tSuggested));

        col.addAction(Actions.sequence(
                new HermiteStopYAction(t, currentY, mappedY, v0),
                Actions.run(() -> {
                    currentScrollSpeeds.set(colIdx, 0f);
                    notifyReelStopped();
                })
        ));
    }

    /**
     * Show the slot with a mask.
     */
    private void showSlotPopup() {
        frameGroup.setVisible(true);
        frameGroup.setTouchable(Touchable.enabled);
        if (dimmer != null) {
            dimmer.setVisible(true);
            dimmer.getColor().a = 0.6f;
        }
        randomizeReelPositions();
    }

    /**
     * Close the slot.
     */
    private void hideSlotPopup() {
        if (spinning) {
            return;
        }
        frameGroup.setVisible(false);
        if (dimmer != null) {
            dimmer.setVisible(false);
        }
        for (Group col : reelColumns) {
            col.clearActions();
            col.setY(0f);
        }
    }

    /**
     * Count the reels has stopped.
     * While all reels has stopped, resolve outcome.
     */
    private void notifyReelStopped() {
        stoppedCount++;
        if (stoppedCount >= REEL_COUNT) {
            spinning = false;
            logger.info("Reel stopped. Resolve outcome.");
            // Logic
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

    /**
     * Continuous scrolling action with constant speed and modular wrap.
     */
    private static class ScrollAction extends TemporalAction {
        private final float speed;
        private final float cycle;
        private final float startY;
        private final java.util.function.Consumer<Float> speedTap;

        ScrollAction(float duration, float speed, float cycle, float startY,
                     java.util.function.Consumer<Float> speedTap) {
            super(duration);
            this.speed = speed;
            this.cycle = cycle;
            this.startY = startY;
            this.speedTap = speedTap;
            setInterpolation(null);
        }

        @Override
        protected void update(float percent) {
            Actor a = getActor();
            if (a == null) return;
            float t = getTime();
            float rawY = startY - speed * t;
            float y = rawY % (-cycle);
            if (y > 0) y -= cycle;
            a.setY(y);
            if (speedTap != null) speedTap.accept(-speed);
        }
    }

    /**
     * Cubic Hermite deceleration from current Y/velocity to a target Y with zero end velocity.
     */
    private static class HermiteStopYAction extends TemporalAction {
        private final float y0, y1;
        private final float v0, v1;

        HermiteStopYAction(float duration, float y0, float y1, float v0) {
            super(duration);
            this.y0 = y0;
            this.y1 = y1;
            this.v0 = v0;
            this.v1 = 0f;
            setInterpolation(null);
        }

        @Override
        protected void update(float percent) {
            Actor a = getActor();
            if (a == null) return;

            float t = Math.max(0f, Math.min(1f, percent));
            float t2 = t * t;
            float t3 = t2 * t;
            float h00 = 2 * t3 - 3 * t2 + 1;
            float h10 = t3 - 2 * t2 + t;
            float h01 = -2 * t3 + 3 * t2;
            float h11 = t3 - t2;

            float T = getDuration();
            float y = h00 * y0 + h10 * (v0 * T) + h01 * y1 + h11 * (v1 * T);
            a.setY(y);
        }
    }
}
