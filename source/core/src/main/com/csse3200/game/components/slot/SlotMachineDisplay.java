package com.csse3200.game.components.slot;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.csse3200.game.areas.SlotMachineArea;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A 3-reel slot machine UI that lives in the top bar.
 *
 * <p>Responsibilities:
 *
 * <ul>
 *   <li>Render a top bar frame with an inner reels area (responsive layout).
 *   <li>Handle user click to trigger a spin.
 *   <li>Animate continuous scrolling and smooth stop on target indices.
 *   <li>Bridge to {@link SlotEngine} to compute outcomes and apply effects.
 *   <li>Display a responsive HUD on the right: remaining spins + pie progress(atlas frames).
 * </ul>
 */
public class SlotMachineDisplay extends UIComponent {
  /** Logic engine used to compute spin outcomes and to apply effects. */
  private final SlotEngine slotEngine;
  
  /** Pause flag for UI animations (reels); currently only used to block new spins during pause. */
  private volatile boolean paused = false;

  /** Cached pending spin result (set on spin, consumed when all reels stop). */
  private SlotEngine.SpinResult pendingResult = null;

  private static final Logger logger = LoggerFactory.getLogger(SlotMachineDisplay.class);

  // ----------- Z-Order ------------

  /** Render order for this UI component. */
  private static final float Z_INDEX = 3f;

  // ------ Layout Ratios (HUD) ------

  /** Top bar height as a ratio of the short screen dimension. */
  private static final float TOPBAR_HEIGHT_RATIO = 0.25f;

  /** Horizontal inner margin of the frame relative to short screen dimension. */
  private static final float TOPBAR_MARGIN_RATIO = 0.015f;

  /** Reels area width relative to the visible frame image. */
  private static final float REELS_AREA_W_RATIO = 0.70f;

  /** Reels area height relative to the visible frame image. */
  private static final float REELS_AREA_H_RATIO = 0.83f;

  /** Vertical offset applied to the reels area relative to the visible frame image. */
  private static final float REELS_AREA_Y_OFFSET = 0.00f;

  /** Number of reels (columns). */
  private static final int REEL_COUNT = 3;

  /** Gap between reel columns as a fraction of the reels area width. */
  private static final float REEL_GAP_RATIO = 0.03f;

  // --------- Reel Runtime ----------

  /** Per-column container groups (vertically scrolled). */
  private final List<Group> reelColumns = new ArrayList<>();

  /** Target symbol indices per column (provided by engine on spin). */
  private int[] targetIndices;

  /** Standard symbol height in pixels (after scaling into one column). */
  private float symbolHeight;

  /** Number of distinct symbols (per cycle). */
  private int symbolCount;

  /** Current vertical speeds per column (used to compute stop curves). */
  private final List<Float> currentScrollSpeeds = new ArrayList<>();

  /** Base reel scroll speed (pixels/second). */
  private float reelScrollSpeedPxPerSec;

  /** Whether any spin sequence is currently active. */
  private boolean isSpinning = false;

  /** Maximum number of cards allowed on the field to permit a spin. */
  private static final int MAX_ACTIVE_CARDS = 5;

  /** Count of columns that have fully stopped in the current sequence. */
  private int stoppedCount = 0;

  // --------- Runtime Sizes ---------

  /** Frame size in pixels (derived from stage short side). */
  private float frameSizePx;

  /** Frame inner margin in pixels. */
  private float marginPx;

  /** Last known stage width (to detect resize). */
  private float lastStageW = -1f;

  /** Last known stage height (to detect resize). */
  private float lastStageH = -1f;

  // ----------- UI Nodes ------------

  /** Root group of the persistent top bar. */
  private Group barGroup;

  /** Full frame image (hit-tested on visible drawn rect). */
  private Image frameImage;

  /** Reels background image (decorative, not interactive). */
  private Image reelsBgImage;

  /** Scroll pane that clips the reels content. */
  private ScrollPane reelsPane;

  /** Reels content holder that scrolls vertically. */
  private Group reelsContent;

  // -------- Loaded Assets ----------

  /** Drawable for unpressed frame state (atlas region). */
  private TextureRegionDrawable frameUpDrawable;

  /** Drawable for pressed frame state (atlas region). */
  private TextureRegionDrawable frameDownDrawable;

  /** Drawable for locked frame state (atlas region). */
  private TextureRegionDrawable frameLockedDrawable;

  /** Cache last available state to avoid redundant swaps. */
  private boolean lastAvailable = true;

  /** Ordered list of symbol regions forming one cycle. */
  private List<TextureAtlas.AtlasRegion> symbolRegions;

  /** Remaining spins text label. */
  private Label spinsLabel;

  /** Pie progress image (frame-swapped from atlas). */
  private Image pieImage;

  /** Pie progress frames sorted by name (pie_filled_000..100). */
  private List<TextureAtlas.AtlasRegion> pieRegions = new ArrayList<>();

  /** Cache of the last selected pie frame index to avoid redundant Drawable swaps. */
  private int currentPieIndex = -1;

  /** Keep in sync with engine default: auto +1 credit every 5s (configurable in engine). */
  private static final int REFILL_PERIOD_SECONDS = 10;

  /** Epoch (ms) when we last observed a +1 refill tick. */
  private long lastRefillEpochMs = System.currentTimeMillis();

  /** Snapshot of last seen remaining spins to detect +1 ticks. */
  private int lastSeenSpins = -1;

  // ---------- Constructors ---------

  /** Creates a display with a bound {@link SlotMachineArea}. */
  public SlotMachineDisplay(SlotMachineArea area) {
    this.slotEngine = new SlotEngine(area);
  }

  /** Creates a display with a default {@link SlotEngine} (no area bound). */
  public SlotMachineDisplay() {
    this.slotEngine = new SlotEngine();
  }

  /** Initializes UI hierarchy, loads resources, computes sizes, and applies layout. */
  @Override
  public void create() {
    super.create();
    initTopBar();
    loadSymbols();
    loadPieRegions();
    initSpinsHud();
    computeSizes();
    applyLayout();
    randomizeReels();
    lastStageW = stage.getWidth();
    lastStageH = stage.getHeight();
    lastSeenSpins = slotEngine.getRemainingSpins();
    lastRefillEpochMs = System.currentTimeMillis();
    updateAvailabilityVisual();
  }

  /**
   * Creates the persistent top bar, loads the frame atlas (up/down), builds the reels pane, and
   * attaches a click listener that triggers a spin.
   *
   * <p>Hit-testing is restricted to the visible drawn portion of the frame image to avoid clicking
   * in transparent/letterboxed areas.
   */
  private void initTopBar() {
    barGroup = new Group();
    barGroup.setTransform(false);
    barGroup.setVisible(true);
    barGroup.setTouchable(Touchable.childrenOnly);
    stage.addActor(barGroup);
    TextureAtlas atlas =
        ServiceLocator.getResourceService()
            .getAsset("images/entities/slotmachine/slot_frame.atlas", TextureAtlas.class);
    TextureRegion upRegion = atlas.findRegion("slot_frame_up");
    TextureRegion downRegion = atlas.findRegion("slot_frame_down");
    TextureRegion lockedRegion = atlas.findRegion("slot_frame_locked");
    if (lockedRegion == null) {
      logger.warn("slot_frame_locked not found in atlas; falling back to up state.");
      lockedRegion = upRegion;
    }
    for (Texture tex : atlas.getTextures()) {
      tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }
    frameUpDrawable = new TextureRegionDrawable(upRegion);
    frameDownDrawable = new TextureRegionDrawable(downRegion);
    frameLockedDrawable = new TextureRegionDrawable(lockedRegion);

    Texture reelsBgTex =
        ServiceLocator.getResourceService()
            .getAsset("images/entities/slotmachine/slot_reels_background.png", Texture.class);
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
    SlotEffect.bindUiContext(stage, reelsPane);

    frameImage = new HitTestImage(frameUpDrawable, Scaling.fit, Align.center);
    frameImage.setScaling(Scaling.fit);
    frameImage.setAlign(Align.center);
    frameImage.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            if (paused) return; // block spins while paused
            if (isSpinning) return;
            int remaining = slotEngine.getRemainingSpins();
            int activeCards = SlotEffect.getActiveCardCount();
            if (remaining <= 0 || activeCards >= MAX_ACTIVE_CARDS) {
              logger.info(
                  "Spin blocked: credits={}, fieldCards={} (limit={})",
                  remaining,
                  activeCards,
                  MAX_ACTIVE_CARDS);
              frameImage.clearActions();
              frameImage.setDrawable(frameDownDrawable);
              frameImage.addAction(
                  Actions.sequence(
                      Actions.delay(0.08f),
                      Actions.run(() -> frameImage.setDrawable(frameUpDrawable))));
              return;
            }

            logger.info("Topbar slot clicked");
            pendingResult = slotEngine.spin();
            targetIndices = pendingResult.getReels();
            frameImage.clearActions();
            frameImage.setDrawable(frameDownDrawable);
            frameImage.addAction(
                Actions.sequence(
                    Actions.delay(0.12f),
                    Actions.run(() -> frameImage.setDrawable(frameUpDrawable))));
            spinToTargets();
          }
        });
    barGroup.addActor(frameImage);
  }

  /** Loads the reels atlas and collects symbol regions in name order. */
  private void loadSymbols() {
    TextureAtlas reelsAtlas =
        ServiceLocator.getResourceService()
            .getAsset("images/entities/slotmachine/slot_reels.atlas", TextureAtlas.class);
    symbolRegions = new ArrayList<>();
    for (TextureAtlas.AtlasRegion r : reelsAtlas.getRegions()) {
      symbolRegions.add(r);
    }
    symbolRegions.sort(Comparator.comparing(a -> a.name));
    if (symbolRegions.isEmpty()) {
      logger.warn("No symbol regions found.");
    }
  }

  /** Loads the pie progress atlas and sorts regions by name (pie_filled_000..100). */
  private void loadPieRegions() {
    // Ensure this asset is registered during the game's asset-loading phase:
    TextureAtlas pieAtlas =
        ServiceLocator.getResourceService()
            .getAsset("images/entities/slotmachine/pie_filled.atlas", TextureAtlas.class);
    pieRegions.clear();
    for (TextureAtlas.AtlasRegion r : pieAtlas.getRegions()) {
      pieRegions.add(r);
    }
    pieRegions.sort(Comparator.comparing(a -> a.name));
    for (Texture t : pieAtlas.getTextures()) {
      t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }
    if (pieRegions.isEmpty()) {
      logger.warn("No pie progress regions found.");
    }
  }

  /** Creates the right-side HUD showing remaining spins and pie progress. */
  private void initSpinsHud() {
    if (barGroup == null) return;

    // Pie image (start from the first region if available)
    TextureRegion first = (pieRegions.isEmpty() ? null : pieRegions.get(0));
    pieImage = new Image(first);
    pieImage.setTouchable(Touchable.disabled);
    pieImage.setScaling(Scaling.fit);
    pieImage.setAlign(Align.center);
    barGroup.addActor(pieImage);

    // Remaining spins label
    spinsLabel = ui.text("0");
    spinsLabel.setColor(skin.getColor("gold"));
    spinsLabel.setAlignment(Align.center);
    spinsLabel.setTouchable(Touchable.disabled);
    barGroup.addActor(spinsLabel);
  }

  /**
   * Builds a single reel column by duplicating the symbol sequence twice to enable seamless
   * vertical looping.
   *
   * @param colWidth column width in pixels
   * @param colHeight column height in pixels
   * @return reel column group with images stacked vertically
   */
  private Group buildOneReel(float colWidth, float colHeight) {
    Group reel = new Group();
    float y = 0f;

    if (symbolRegions == null || symbolRegions.isEmpty()) {
      reel.setSize(colWidth, colHeight);
      return reel;
    }

    symbolCount = symbolRegions.size();

    TextureRegion first = symbolRegions.getFirst();
    float scale = Math.min(colWidth / first.getRegionWidth(), colHeight / first.getRegionHeight());
    float wStd = first.getRegionWidth() * scale;
    float hStd = first.getRegionHeight() * scale;
    symbolHeight = hStd;

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
   * Rebuilds all reels and resets per-column speeds.
   *
   * @param areaX reels area x
   * @param areaY reels area y
   * @param areaW reels area width
   * @param areaH reels area height
   */
  private void buildReels(float areaX, float areaY, float areaW, float areaH) {
    reelsContent.clearChildren();
    reelColumns.clear();

    if (symbolRegions == null || symbolRegions.isEmpty()) {
      return;
    }

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
    for (int i = 0; i < REEL_COUNT; i++) {
      currentScrollSpeeds.add(0f);
    }
  }

  /** Compute pixel sizes based on the current stage dimensions. */
  private void computeSizes() {
    float w = stage.getWidth();
    float h = stage.getHeight();
    float base = Math.min(w, h);

    frameSizePx = base * TOPBAR_HEIGHT_RATIO;
    marginPx = base * TOPBAR_MARGIN_RATIO;

    reelScrollSpeedPxPerSec = frameSizePx * 4.0f;
  }

  /** Randomizes reel positions when the slot machine is first shown or rebuilt. */
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

  private void layoutPie(float visX, float visW, float barX, float barY, float barH) {
    float gap = marginPx * 0.6f;
    float pieSize = barH * 0.35f;
    float preferredPieX = visX + visW + gap;
    float pieY = barY + (barH - pieSize) * 0.5f;

    if (pieImage != null) {
      pieImage.setSize(pieSize, pieSize);
      pieImage.setPosition(preferredPieX - barX, pieY - barY);
    }
    centerSpinsLabelOverPie();
  }

  /**
   * Applies positions/sizes for the top bar frame, the reels area, and then rebuilds reels. This is
   * responsive and should be called on create and whenever the stage size changes.
   */
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
      float drawX;
      float drawY;
      if ((align & Align.left) != 0) drawX = 0f;
      else if ((align & Align.right) != 0) drawX = actorW - drawW;
      else drawX = (actorW - drawW) * 0.5f;

      if ((align & Align.bottom) != 0) drawY = 0f;
      else if ((align & Align.top) != 0) drawY = actorH - drawH;
      else drawY = (actorH - drawH) * 0.5f;

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
      layoutPie(visX, visW, barX, barY, barH);
    }
  }

  /**
   * Starts looped scrolling for all reels and schedules staggered smooth stop landing on target.
   */
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
          new ScrollAction(
              8f, speed, oneCycle, col.getY(), v -> currentScrollSpeeds.set(colIndex, v));
      col.addAction(loop);
    }

    for (int i = 0; i < reelColumns.size(); i++) {
      final int colIndex = i;
      float delay = 1.2f + 0.6f * i;

      Group col = reelColumns.get(i);
      col.addAction(
          Actions.sequence(Actions.delay(delay), Actions.run(() -> stopColumnAt(colIndex))));
    }
  }

  /**
   * Smoothly stops a given column at its target index, avoiding large bounce-back by optionally
   * advancing to the next equivalent lap when over half a symbol height.
   *
   * @param colIdx reel column index
   */
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

    float v0 =
        (colIdx < currentScrollSpeeds.size())
            ? currentScrollSpeeds.get(colIdx)
            : -reelScrollSpeedPxPerSec;

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
            Actions.run(
                () -> {
                  currentScrollSpeeds.set(colIdx, 0f);
                  notifyReelStopped();
                })));
  }

  /** Count the reels has stopped. While all reels has stopped, resolve outcome. */
  private void notifyReelStopped() {
    stoppedCount++;
    if (stoppedCount >= REEL_COUNT) {
      isSpinning = false;
      logger.info("All reels stopped.");
      if (pendingResult != null) {
        if (pendingResult.isEffectTriggered()) {
          slotEngine.applyEffect(pendingResult);
        } else {
          logger.info("No effect triggered.");
        }
        pendingResult = null;
      }
    }
  }

  /** Draw pass also detects stage resize and reapplies layout if needed. */
  @Override
  public void draw(SpriteBatch batch) {
    float w = stage.getWidth();
    float h = stage.getHeight();
    if (w != lastStageW || h != lastStageH) {
      lastStageW = w;
      lastStageH = h;
      computeSizes();
      applyLayout();
      centerSpinsLabelOverPie();
    }
    updateSpinsHud();
    updateAvailabilityVisual();
  }

  /** Updates remaining spins text and swaps pie frame based on refill progress. */
  private void updateSpinsHud() {
    // 1) Remaining spins
    int cur = slotEngine.getRemainingSpins();
    if (spinsLabel != null) {
      spinsLabel.setText(String.valueOf(cur));
      centerSpinsLabelOverPie();
    }

    // Detect auto-refill tick (remaining spins increased)
    if (lastSeenSpins >= 0 && cur > lastSeenSpins) {
      lastRefillEpochMs = System.currentTimeMillis();
    }
    lastSeenSpins = cur;

    // 2) Pie frame by time since last tick
    if (pieImage != null && !pieRegions.isEmpty()) {
      long now = System.currentTimeMillis();
      double periodMs = Math.max(1.0, REFILL_PERIOD_SECONDS * 1000.0);
      double f = (now - lastRefillEpochMs) / periodMs; // 0..1
      if (f < 0) f = 0;
      if (f > 1) f = 1;

      int n = pieRegions.size();
      int idx = (int) Math.floor(f * (n - 1) + 1e-6);
      if (idx < 0) idx = 0;
      if (idx >= n) idx = n - 1;

      if (idx != currentPieIndex) {
        currentPieIndex = idx;
        pieImage.setDrawable(new TextureRegionDrawable(pieRegions.get(idx)));
      }
    }
  }

  /** Swap the frame image to locked/unlocked depending on availability. */
  private void updateAvailabilityVisual() {
    if (frameImage == null) return;
    if (isSpinning || frameImage.hasActions()) return;

    int remaining = slotEngine.getRemainingSpins();
    int activeCards = SlotEffect.getActiveCardCount();
    boolean available = (remaining > 0) && (activeCards < MAX_ACTIVE_CARDS);

    if (available != lastAvailable) {
      if (available) {
        frameImage.setDrawable(frameUpDrawable);
        frameImage.setTouchable(Touchable.enabled);
      } else {
        frameImage.setDrawable(frameLockedDrawable);
        frameImage.setTouchable(Touchable.disabled);
      }
      lastAvailable = available;
    }
  }

  /** Keep the spinsLabel centered over the pieImage regardless of scaling/resizing. */
  private void centerSpinsLabelOverPie() {
    if (spinsLabel == null || pieImage == null) return;

    final float pieW = pieImage.getWidth();
    final float pieH = pieImage.getHeight();
    final float targetPx = pieW * 0.65f;
    final float base = 32f;
    final float scale = Math.clamp(targetPx / base, 0.8f, 3.5f);
    spinsLabel.setFontScale(scale);
    spinsLabel.setAlignment(Align.center);
    spinsLabel.invalidateHierarchy();
    spinsLabel.pack();

    final float w = spinsLabel.getPrefWidth();
    final float h = spinsLabel.getPrefHeight();

    final float cx = pieImage.getX() + pieW * 0.5f;
    final float cy = pieImage.getY() + pieH * 0.5f;

    final float offsetX = 0.00f * pieW;
    final float offsetY = 0.10f * pieH;

    spinsLabel.setSize(w, h);
    spinsLabel.setPosition(Math.round(cx - w / 2f + offsetX), Math.round(cy - h / 2f + offsetY));
    spinsLabel.toFront();
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void dispose() {
    super.dispose();
    if (barGroup != null) barGroup.remove();
    SlotEffect.unbindUiContext();
  }
  
  /** Pause slot auto-refill and disable spin input. */
  public void pauseSpin() {
    try {
      paused = true;
      slotEngine.stopAutoRefill();
      if (frameImage != null) {
        frameImage.setTouchable(Touchable.disabled);
      }
    } catch (Exception e) {
      logger.warn("pauseSpin encountered an issue: {}", e.getMessage());
    }
  }
  
  /** Resume slot auto-refill and re-enable spin input. */
  public void resumeSpin() {
    try {
      paused = false;
      slotEngine.startAutoRefill();
      if (frameImage != null) {
        frameImage.setTouchable(Touchable.enabled);
      }
    } catch (Exception e) {
      logger.warn("resumeSpin encountered an issue: {}", e.getMessage());
    }
  }

  /** Image with precise hit test limited to actually drawn (scaled & aligned) rectangle. */
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

      float actorW = getWidth();
      float actorH = getHeight();
      float srcW = getDrawable().getMinWidth();
      float srcH = getDrawable().getMinHeight();

      Vector2 size = scaling.apply(srcW, srcH, actorW, actorH);
      float drawW = size.x;
      float drawH = size.y;
      float drawX;
      float drawY;

      if ((align & Align.left) != 0) {
        drawX = 0f;
      } else if ((align & Align.right) != 0) {
        drawX = actorW - drawW;
      } else {
        drawX = (actorW - drawW) * 0.5f;
      }

      if ((align & Align.bottom) != 0) {
        drawY = 0f;
      } else if ((align & Align.top) != 0) {
        drawY = actorH - drawH;
      } else {
        drawY = (actorH - drawH) * 0.5f;
      }

      if (x >= drawX && x <= drawX + drawW && y >= drawY && y <= drawY + drawH) {
        return this;
      }
      return null;
    }
  }

  /** Continuous scrolling action with constant speed and modular wrap. */
  private static class ScrollAction extends TemporalAction {
    private final float speed;
    private final float cycle;
    private final float startY;
    private final java.util.function.Consumer<Float> speedTap;

    ScrollAction(
        float duration,
        float speed,
        float cycle,
        float startY,
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

  /** Cubic Hermite deceleration from current Y/velocity to a target Y with zero end velocity. */
  private static class HermiteStopYAction extends TemporalAction {
    private final float y0;
    private final float y1;
    private final float v0;
    private final float v1;

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

      float t = Math.clamp(percent, 0.0f, 1.0f);
      float t2 = t * t;
      float t3 = t2 * t;
      float h00 = 2 * t3 - 3 * t2 + 1;
      float h10 = t3 - 2 * t2 + t;
      float h01 = -2 * t3 + 3 * t2;
      float h11 = t3 - t2;

      float d = getDuration();
      float y = h00 * y0 + h10 * (v0 * d) + h01 * y1 + h11 * (v1 * d);
      a.setY(y);
    }
  }
}
