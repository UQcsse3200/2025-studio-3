package com.csse3200.game.components.slot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Array;                    // <-- gdx Array (important)
import com.csse3200.game.areas.SlotMachineArea;
import com.csse3200.game.components.cards.CardActor;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Top-bar 3-reel slot machine UI.
 * Only drops a HERO card when all 3 reels show the hero face (8_SlingShooter).
 */
public class SlotMachineDisplay extends UIComponent {
    // ----- engine & pending result -----
    private final SlotEngine slotEngine;
    private SlotEngine.SpinResult pendingResult = null;

    private static final Logger logger = LoggerFactory.getLogger(SlotMachineDisplay.class);

    // ----- card atlas (UI cards) -----
    private static final String CARD_ATLAS = "images/ui_cards.atlas";
    private static final String CARD_REGION_HERO = "Card_SlingShooter";   // prefer this if present

    private TextureAtlas cardAtlas = null; // prefer ResourceService asset; fallback to local file
    private boolean ownsCardAtlas = false; // dispose only if we created it
    private final List<Texture> ownedRuntimeTextures = new ArrayList<>();

    // ----- reels atlas hero region name -----
    private static final String REEL_REGION_HERO = "8_SlingShooter";

    // ----- z-order -----
    private static final float Z_INDEX = 3f;

    // ----- layout ratios -----
    private static final float TOPBAR_HEIGHT_RATIO = 0.25f;
    private static final float TOPBAR_MARGIN_RATIO = 0.015f;
    private static final float REELS_AREA_W_RATIO = 0.70f;
    private static final float REELS_AREA_H_RATIO = 0.83f;
    private static final float REELS_AREA_Y_OFFSET = 0.00f;
    private static final int REEL_COUNT = 3;
    private static final float REEL_GAP_RATIO = 0.03f;

    // ----- runtime state (reels) -----
    private final List<Group> reelColumns = new ArrayList<>();
    private int[] targetIndices;
    private float symbolHeight;
    private int symbolCount;
    private final List<Float> currentScrollSpeeds = new ArrayList<>();
    private float reelScrollSpeedPxPerSec;
    private boolean isSpinning = false;
    private int stoppedCount = 0;

    // ----- runtime sizes -----
    private float frameSizePx;
    private float marginPx;
    private float lastStageW = -1f, lastStageH = -1f;

    // ----- UI nodes -----
    private Group barGroup;
    private Image frameImage;
    private Image reelsBgImage;
    private ScrollPane reelsPane;
    private Group reelsContent;

    // ----- loaded assets -----
    private TextureRegionDrawable frameUpDrawable;
    private TextureRegionDrawable frameDownDrawable;
    private List<TextureAtlas.AtlasRegion> symbolRegions;

    // ----- ctors -----
    public SlotMachineDisplay(SlotMachineArea area) { this.slotEngine = new SlotEngine(area); }
    public SlotMachineDisplay() { this.slotEngine = new SlotEngine(); }

    // ----- lifecycle -----
    @Override
    public void create() {
        super.create();
        ensureCardAtlasAvailable();
        initTopBar();
        loadSymbols();
        computeSizes();
        applyLayout();
        randomizeReels();
        lastStageW = stage.getWidth();
        lastStageH = stage.getHeight();
    }

    /** Try ResourceService first; otherwise load atlas locally if present. */
    private void ensureCardAtlasAvailable() {
        var rs = ServiceLocator.getResourceService();
        TextureAtlas rsAtlas = null;
        try { rsAtlas = rs.getAsset(CARD_ATLAS, TextureAtlas.class); } catch (Exception ignore) {}
        if (rsAtlas != null) {
            cardAtlas = rsAtlas;
            ownsCardAtlas = false;
            logger.info("Using atlas from ResourceService: {}", CARD_ATLAS);
        } else if (Gdx.files.internal(CARD_ATLAS).exists()) {
            cardAtlas = new TextureAtlas(Gdx.files.internal(CARD_ATLAS));
            ownsCardAtlas = true;
            logger.info("Loaded local atlas: {}", CARD_ATLAS);
        } else {
            logger.warn("Card atlas not found: {}", CARD_ATLAS);
        }
    }

    // ----- UI build -----
    private void initTopBar() {
        barGroup = new Group();
        barGroup.setTransform(false);
        barGroup.setVisible(true);
        barGroup.setTouchable(Touchable.childrenOnly);
        stage.addActor(barGroup);

        TextureAtlas frameAtlas =
                ServiceLocator.getResourceService().getAsset("images/slot_frame.atlas", TextureAtlas.class);
        TextureRegion upRegion = frameAtlas.findRegion("slot_frame_up");
        TextureRegion downRegion = frameAtlas.findRegion("slot_frame_down");
        for (Texture tex : frameAtlas.getTextures()) {
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        frameUpDrawable = new TextureRegionDrawable(upRegion);
        frameDownDrawable = new TextureRegionDrawable(downRegion);

        Texture reelsBgTex =
                ServiceLocator.getResourceService().getAsset("images/slot_reels_background.png", Texture.class);
        reelsBgTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        reelsBgImage = new Image(reelsBgTex);
        reelsBgImage.setTouchable(Touchable.disabled);
        reelsBgImage.setScaling(Scaling.fit);
        reelsBgImage.setAlign(Align.center);
        barGroup.addActor(reelsBgImage);

        reelsContent = new Group();
        reelsPane = new ScrollPane(reelsContent);
        reelsPane.setScrollingDisabled(true, true);
        reelsPane.setFadeScrollBars(false);
        reelsPane.setOverscroll(false, false);
        barGroup.addActor(reelsPane);

        frameImage = new HitTestImage(frameUpDrawable, Scaling.fit, Align.center);
        frameImage.setScaling(Scaling.fit);
        frameImage.setAlign(Align.center);
        frameImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isSpinning) return;
                pendingResult = slotEngine.spin();
                targetIndices = pendingResult.getReels();
                frameImage.clearActions();
                frameImage.setDrawable(frameDownDrawable);
                frameImage.addAction(Actions.sequence(
                        Actions.delay(0.12f),
                        Actions.run(() -> frameImage.setDrawable(frameUpDrawable))
                ));
                spinToTargets();
            }
        });
        barGroup.addActor(frameImage);
    }

    /** Load reels atlas and collect regions sorted by name (handle gdx Array). */
    private void loadSymbols() {
        TextureAtlas reelsAtlas =
                ServiceLocator.getResourceService().getAsset("images/slot_reels.atlas", TextureAtlas.class);

        Array<TextureAtlas.AtlasRegion> gdxRegions = reelsAtlas.getRegions(); // gdx Array
        symbolRegions = new ArrayList<>(gdxRegions.size);
        for (TextureAtlas.AtlasRegion r : gdxRegions) {
            symbolRegions.add(r);
        }

        symbolRegions.sort(Comparator.comparing(a -> a.name));
        if (symbolRegions.isEmpty()) logger.warn("No symbol regions found.");
    }

    private Group buildOneReel(float colWidth, float colHeight) {
        Group reel = new Group();
        float y = 0f;

        if (symbolRegions == null || symbolRegions.isEmpty()) {
            reel.setSize(colWidth, colHeight);
            return reel;
        }

        symbolCount = symbolRegions.size();

        TextureRegion first = symbolRegions.get(0); // fixed: get(0) instead of getFirst()
        float scale = Math.min(colWidth / first.getRegionWidth(), colHeight / first.getRegionHeight());
        float wStd = first.getRegionWidth() * scale;
        float hStd = first.getRegionHeight() * scale;
        symbolHeight = hStd;

        // Build 2 copies to allow continuous scroll.
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

    private void buildReels(float areaX, float areaY, float areaW, float areaH) {
        reelsContent.clearChildren();
        reelColumns.clear();

        if (symbolRegions == null || symbolRegions.isEmpty()) return;

        float gap = areaW * REEL_GAP_RATIO;
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
        for (int i = 0; i < REEL_COUNT; i++) currentScrollSpeeds.add(0f);
    }

    private void computeSizes() {
        float w = stage.getWidth();
        float h = stage.getHeight();
        float base = Math.min(w, h);

        frameSizePx = base * TOPBAR_HEIGHT_RATIO;
        marginPx = base * TOPBAR_MARGIN_RATIO;

        reelScrollSpeedPxPerSec = frameSizePx * 4.0f;
    }

    private void randomizeReels() {
        if (reelColumns.isEmpty() || symbolCount <= 0) return;
        float h = symbolHeight;
        float centerOffset = (reelsPane.getHeight() - h) / 2f;
        for (Group col : reelColumns) {
            int randIndex = ThreadLocalRandom.current().nextInt(symbolCount);
            float targetY = -(randIndex * h) + centerOffset;
            col.setY(targetY);
        }
    }

    private void applyLayout() {
        float stageW = stage.getWidth();
        float stageH = stage.getHeight();

        float barH = frameSizePx;
        float barW = stageW;
        float barX = 0f;
        float barY = stageH - barH;

        if (barGroup != null) {
            barGroup.setSize(barW, barH);
            barGroup.setPosition(barX, barY);
        }

        float frameW = barW - 2f * marginPx;
        float frameH = barH - 2f * marginPx;

        if (frameImage != null) {
            frameImage.setSize(frameW, frameH);
            frameImage.setPosition(marginPx, marginPx);
        }
        if (reelsBgImage != null) {
            reelsBgImage.setSize(frameW, frameH);
            reelsBgImage.setPosition(marginPx, marginPx);
        }

        float actorW = frameW;
        float actorH = frameH;
        float srcW = frameUpDrawable.getMinWidth();
        float srcH = frameUpDrawable.getMinHeight();

        if (frameImage != null) {
            Vector2 size = ((HitTestImage) frameImage).scaling.apply(srcW, srcH, actorW, actorH);

            float drawW = size.x;
            float drawH = size.y;

            int align = frameImage.getAlign();
            float drawX = (align & Align.left) != 0 ? 0f :
                    (align & Align.right) != 0 ? actorW - drawW : (actorW - drawW) * 0.5f;
            float drawY = (align & Align.bottom) != 0 ? 0f :
                    (align & Align.top) != 0 ? actorH - drawH : (actorH - drawH) * 0.5f;

            float visX = marginPx + drawX;
            float visY = marginPx + drawY;
            float visW = drawW;
            float visH = drawH;

            float areaW = visW * REELS_AREA_W_RATIO;
            float areaH = visH * REELS_AREA_H_RATIO;
            float areaX = visX + (visW - areaW) * 0.5f;
            float areaY = visY + (visH - areaH) * 0.5f + visH * REELS_AREA_Y_OFFSET;

            if (reelsPane != null) {
                reelsPane.setSize(areaW, areaH);
                reelsPane.setPosition(areaX, areaY);
            }

            buildReels(areaX, areaY, areaW, areaH);
            randomizeReels();
            isSpinning = false;
        }
    }

    private void spinToTargets() {
        if (isSpinning) {
            logger.info("Already spinning.");
            return;
        }

        stoppedCount = 0;
        isSpinning = true;

        for (int i = 0; i < reelColumns.size(); i++) {
            final int colIndex = i;
            Group col = reelColumns.get(i);
            col.clearActions();

            float oneCycle = symbolHeight * symbolCount;
            float speed = reelScrollSpeedPxPerSec * (0.9f + 0.1f * i);

            ScrollAction loop =
                    new ScrollAction(8f, speed, oneCycle, col.getY(), v -> currentScrollSpeeds.set(colIndex, v));
            col.addAction(loop);
        }

        for (int i = 0; i < reelColumns.size(); i++) {
            final int colIndex = i;
            float delay = 1.2f + 0.6f * i;

            Group col = reelColumns.get(i);
            col.addAction(Actions.sequence(Actions.delay(delay), Actions.run(() -> stopColumnAt(colIndex))));
        }
    }

    private void stopColumnAt(int colIdx) {
        if (colIdx < 0 || colIdx >= reelColumns.size()) return;

        Group col = reelColumns.get(colIdx);
        col.clearActions();

        float h = symbolHeight;
        int baseCount = symbolCount;
        float oneCycle = h * baseCount;

        int rawIndex = targetIndices[colIdx];
        int target = ((rawIndex % baseCount) + baseCount) % baseCount;

        float centerOffset = (reelsPane != null ? (reelsPane.getHeight() - h) / 2f : 0f);
        float idealY = -(target * h) + centerOffset;

        float currentY = col.getY();

        float kNearest = Math.round((currentY - idealY) / (-oneCycle));
        float mappedY = idealY - kNearest * oneCycle;

        float v0 = (colIdx < currentScrollSpeeds.size())
                ? currentScrollSpeeds.get(colIdx) : -reelScrollSpeedPxPerSec;

        float delta = mappedY - currentY;
        float halfSymbol = 0.5f * h;

        if (v0 < -1e-3f) {
            if (delta > 0f && Math.abs(delta) > halfSymbol) {
                mappedY -= oneCycle;
                delta = mappedY - currentY;
            }
        } else if (v0 > 1e-3f && delta < 0f && Math.abs(delta) > halfSymbol) {
            mappedY += oneCycle;
            delta = mappedY - currentY;
        }

        float distance = Math.abs(delta);
        float base = Math.max(20f, Math.abs(v0));
        float tSuggested = distance / (base * 0.9f);
        float t = Math.clamp(tSuggested, 0.40f, 1.10f);

        col.addAction(
                Actions.sequence(
                        new HermiteStopYAction(t, currentY, mappedY, v0),
                        Actions.run(() -> {
                            currentScrollSpeeds.set(colIdx, 0f);
                            notifyReelStopped();
                        })
                )
        );
    }

    // ----- hero triple detection -----

    private String symbolNameAt(int safeIndex) {
        if (symbolRegions == null || symbolRegions.isEmpty()) return null;
        int n = symbolRegions.size();
        int idx = ((safeIndex % n) + n) % n;
        return symbolRegions.get(idx).name;
    }

    private boolean isTripleOf(String regionName) {
        if (targetIndices == null || targetIndices.length < 3 || symbolCount <= 0) return false;
        String a = symbolNameAt(targetIndices[0]);
        String b = symbolNameAt(targetIndices[1]);
        String c = symbolNameAt(targetIndices[2]);
        return regionName.equals(a) && regionName.equals(b) && regionName.equals(c);
    }

    private boolean isHeroTriple() {
        return isTripleOf(REEL_REGION_HERO);
    }

    /** Called when each reel fully stops; when all stop we resolve the outcome. */
    private void notifyReelStopped() {
        stoppedCount++;
        if (stoppedCount >= REEL_COUNT) {
            isSpinning = false;
            if (pendingResult != null) {
                boolean engineTriggered = pendingResult.isEffectTriggered();

                // Let the engine apply its non-hero effects (if any).
                if (engineTriggered) {
                    slotEngine.applyEffect(pendingResult);
                }

                // Only drop a card when the triple is the hero face.
                if (isHeroTriple()) {
                    logger.info("Hero triple -> drop hero card.");
                    playHeroCardSpawnAnimation();
                }
                pendingResult = null;
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        float w = stage.getWidth();
        float h = stage.getHeight();
        if (w != lastStageW || h != lastStageH) {
            lastStageW = w; lastStageH = h;
            computeSizes();
            applyLayout();
        }
    }

    @Override
    public float getZIndex() { return Z_INDEX; }

    @Override
    public void dispose() {
        super.dispose();
        if (barGroup != null) barGroup.remove();
        if (ownsCardAtlas && cardAtlas != null) {
            cardAtlas.dispose();
            cardAtlas = null;
        }
        for (Texture t : ownedRuntimeTextures) if (t != null) t.dispose();
        ownedRuntimeTextures.clear();
    }

    // ----- helpers & actions -----

    private static class HitTestImage extends Image {
        private final Scaling scaling;
        private final int align;
        HitTestImage(TextureRegionDrawable drawable, Scaling scaling, int align) {
            super(drawable);
            this.scaling = scaling != null ? scaling : Scaling.stretch;
            this.align = align;
            setScaling(this.scaling);
            setAlign(this.align);
            setTouchable(Touchable.enabled);
        }
        @Override
        public Actor hit(float x, float y, boolean touchable) {
            if (touchable && getTouchable() != Touchable.enabled) return null;
            if (getDrawable() == null) return null;
            float actorW = getWidth(), actorH = getHeight();
            float srcW = getDrawable().getMinWidth(), srcH = getDrawable().getMinHeight();
            Vector2 size = scaling.apply(srcW, srcH, actorW, actorH);
            float drawW = size.x, drawH = size.y;
            float drawX = (align & Align.left) != 0 ? 0f :
                    (align & Align.right) != 0 ? actorW - drawW : (actorW - drawW) * 0.5f;
            float drawY = (align & Align.bottom) != 0 ? 0f :
                    (align & Align.top) != 0 ? actorH - drawH : (actorH - drawH) * 0.5f;
            return (x >= drawX && x <= drawX + drawW && y >= drawY && y <= drawY + drawH) ? this : null;
        }
    }

    private static class ScrollAction extends TemporalAction {
        private final float speed, cycle, startY;
        private final java.util.function.Consumer<Float> speedTap;
        ScrollAction(float duration, float speed, float cycle, float startY,
                     java.util.function.Consumer<Float> speedTap) {
            super(duration);
            this.speed = speed; this.cycle = cycle; this.startY = startY; this.speedTap = speedTap;
            setInterpolation(null);
        }
        @Override
        protected void update(float percent) {
            Actor a = getActor(); if (a == null) return;
            float t = getTime();
            float rawY = startY - speed * t;
            float y = rawY % (-cycle);
            if (y > 0) y -= cycle;
            a.setY(y);
            if (speedTap != null) speedTap.accept(-speed);
        }
    }

    private static class HermiteStopYAction extends TemporalAction {
        private final float y0, y1, v0, v1;
        HermiteStopYAction(float duration, float y0, float y1, float v0) {
            super(duration);
            this.y0 = y0; this.y1 = y1; this.v0 = v0; this.v1 = 0f;
            setInterpolation(null);
        }
        @Override
        protected void update(float percent) {
            Actor a = getActor(); if (a == null) return;
            float t = Math.clamp(percent, 0.0f, 1.0f);
            float t2 = t * t, t3 = t2 * t;
            float h00 = 2 * t3 - 3 * t2 + 1;
            float h10 = t3 - 2 * t2 + t;
            float h01 = -2 * t3 + 3 * t2;
            float h11 = t3 - t2;
            float d = getDuration();
            float y = h00 * y0 + h10 * (v0 * d) + h01 * y1 + h11 * (v1 * d);
            a.setY(y);
        }
    }

    // ----- card spawning -----

    private Vector2 reelMouthStageCenter() {
        if (reelsPane == null) return new Vector2(stage.getWidth() * 0.5f, stage.getHeight() * 0.8f);
        return reelsPane.localToStageCoordinates(new Vector2(reelsPane.getWidth() * 0.5f, reelsPane.getHeight()));
    }

    /** Spawn a hero card (prefer the card atlas region; fallback to the hero face or a runtime blank). */
    private void playHeroCardSpawnAnimation() {
        try {
            CardActor card = newHeroCard();
            if (card == null) return;

            Vector2 mouth = reelMouthStageCenter();
            float startX = mouth.x - card.getWidth() * 0.5f;
            float startY = mouth.y + card.getHeight() * 0.20f;
            card.setPosition(startX, startY);
            card.getColor().a = 0f;
            card.setOrigin(Align.center);

            float endX = startX;
            float endY = Math.max(stage.getHeight() * 0.12f - card.getHeight() * 0.5f, 16f);

            stage.addActor(card);
            float rot = (ThreadLocalRandom.current().nextFloat() * 12f) - 6f;
            card.addAction(Actions.sequence(
                    Actions.parallel(
                            Actions.fadeIn(0.12f),
                            Actions.rotateBy(rot, 0.55f),
                            Actions.moveTo(endX, endY, 0.55f, Interpolation.pow2In)
                    ),
                    Actions.scaleTo(1.06f, 0.94f, 0.07f),
                    Actions.scaleTo(1f, 1f, 0.08f)
            ));

            if (entity != null && entity.getEvents() != null) {
                entity.getEvents().trigger("slot-card-spawned", "hero:SlingShooter");
            }
        } catch (Exception e) {
            logger.warn("Failed to play hero card spawn animation: {}", e.getMessage());
        }
    }

    private CardActor newHeroCard() {
        TextureRegion r = null;
        if (cardAtlas != null) r = cardAtlas.findRegion(CARD_REGION_HERO);
        if (r == null) {
            try {
                TextureAtlas reelsAtlas =
                        ServiceLocator.getResourceService().getAsset("images/slot_reels.atlas", TextureAtlas.class);
                r = reelsAtlas.findRegion(REEL_REGION_HERO);
            } catch (Exception ignore) {}
        }
        if (r == null) r = createRuntimeBlankCardRegion(512, 768);
        return new CardActor(r);
    }

    private TextureRegion createRuntimeBlankCardRegion(int w, int h) {
        Pixmap pm = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pm.setColor(0, 0, 0, 0); pm.fill();

        int radius = Math.max(12, Math.min(w, h) / 16);
        int border = Math.max(8, Math.min(w, h) / 32);

        // white rounded rect
        pm.setColor(1, 1, 1, 1);
        pm.fillCircle(radius, radius, radius);
        pm.fillCircle(w - 1 - radius, radius, radius);
        pm.fillCircle(radius, h - 1 - radius, radius);
        pm.fillCircle(w - 1 - radius, h - 1 - radius, radius);
        pm.fillRectangle(radius, 0, w - 2 * radius, h);
        pm.fillRectangle(0, radius, w, h - 2 * radius);

        // black border
        pm.setColor(0, 0, 0, 1);
        for (int i = 0; i < border; i++) {
            int r = radius - i; if (r < 1) break;
            pm.drawCircle(radius, radius, r);
            pm.drawCircle(w - 1 - radius, radius, r);
            pm.drawCircle(radius, h - 1 - radius, r);
            pm.drawCircle(w - 1 - radius, h - 1 - radius, r);
            pm.drawLine(radius, i, w - 1 - radius, i);
            pm.drawLine(radius, h - 1 - i, w - 1 - radius, h - 1 - i);
            pm.drawLine(i, radius, i, h - 1 - radius);
            pm.drawLine(w - 1 - i, radius, w - 1 - i, h - 1 - radius);
        }

        Texture tex = new Texture(pm);
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pm.dispose();
        ownedRuntimeTextures.add(tex);
        return new TextureRegion(tex);
    }
}
