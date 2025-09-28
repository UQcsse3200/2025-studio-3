package com.csse3200.game.components.slot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.areas.LevelGameGrid;
import com.csse3200.game.components.DeckInputComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.factories.DefenceFactory;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;
import java.util.function.Supplier;

/** Unified card module: resource loading, drop animation, drag placement, count (no UI label). */
public final class SlotCardEntity {
    private static final Logger logger = LoggerFactory.getLogger(SlotCardEntity.class);
    private SlotCardEntity() {}

    // === Resources and parameters (copied from SlotEffect constants) ===
    private static final String CARDS_ATLAS_PATH = "images/ui_cards/ui_cards.atlas";
    private static TextureAtlas cardsAtlas;
    private static boolean ownsCardsAtlas = false;

    private static final float CARD_SCREEN_W_RATIO = 0.06f;
    private static final float CARD_SLOT_W_RATIO  = 0.22f;
    private static final float DROP_FADE_SEC      = 0.15f;
    private static final float DROP_MOVE_SEC      = 0.35f;
    private static final float DROP_PADDING_PCT   = 0.02f;
    private static final float DROP_PADDING_MIN   = 8f;

    // === Count only ===
    private static int activeCount = 0;
    public static int getActiveCardCount() { return Math.max(0, activeCount); }

    // Added: General card specifications
    public static final class CardSpec {
        public final String atlasRegion;           // Atlas Area Name (Map)
        public final Supplier<Entity> factory;     // The factory that produces the unit

        public CardSpec(String atlasRegion, Supplier<Entity> factory) {
            this.atlasRegion = Objects.requireNonNull(atlasRegion, "atlasRegion");
            this.factory = Objects.requireNonNull(factory, "factory");
        }
        public static CardSpec of(String region, Supplier<Entity> factory) {
            return new CardSpec(region, factory);
        }
    }

    /** [Original] Only trebuchet version (retain compatibility); [Modified] Internally call the general entry */
    public static void dropSlingShooterCard(Stage uiStage, ScrollPane uiReelsPane, LevelGameArea area) {

        // Universal Transposition dropCard
        dropCard(
                uiStage,
                uiReelsPane,
                area,
                CardSpec.of("Card_SlingShooter", DefenceFactory::createSlingShooter)
        );
    }

    // New: Universal drop entrance
    public static void dropCard(Stage uiStage, ScrollPane uiReelsPane, LevelGameArea area, CardSpec spec) {
        if (uiStage == null || uiReelsPane == null || area == null || spec == null) {
            logger.error("[SlotCardEntity] drop: missing stage/reels/area/spec");
            return;
        }
        ensureAtlas();

        // The texture area is read from spec
        if (cardsAtlas.findRegion(spec.atlasRegion) == null) {
            logger.error("[SlotCardEntity] region missing: {}", spec.atlasRegion);
            return;
        }

        Vector2 slotTopCenter =
                uiReelsPane.localToStageCoordinates(
                        new Vector2(uiReelsPane.getWidth() * 0.5f, uiReelsPane.getHeight()));
        Vector2 slotBottomCenter =
                uiReelsPane.localToStageCoordinates(new Vector2(uiReelsPane.getWidth() * 0.5f, 0f));

        float stageW = uiStage.getWidth();
        float stageH = uiStage.getHeight();
        float padding = Math.max(DROP_PADDING_MIN, stageH * DROP_PADDING_PCT);

        // 【Past】DraggableCard(uiStage, cardsAtlas, "Card_SlingShooter", area);
        // 【New】Pass in spec, and Image also uses spec to get the texture
        DraggableCard card = new DraggableCard(uiStage, cardsAtlas, spec, area);

        float desiredWpx =
                Math.min(stageW * CARD_SCREEN_W_RATIO, uiReelsPane.getWidth() * CARD_SLOT_W_RATIO);
        card.setPercentWidth(desiredWpx / stageW);
        card.act(0f);

        float startCx = slotTopCenter.x;
        float startCy = slotTopCenter.y + card.getHeight() / 2f + padding;
        float targetCx = slotBottomCenter.x;
        float targetCy =
                Math.max(slotBottomCenter.y - card.getHeight() / 2f - padding, card.getHeight() / 2f + 4f);

        card.setPosition(startCx - card.getWidth() / 2f, startCy - card.getHeight() / 2f);
        card.getColor().a = 0f;
        uiStage.addActor(card);
        card.toFront();

        float targetX = targetCx - card.getWidth() / 2f;
        float targetY = targetCy - card.getHeight() / 2f;

        card.addAction(
                Actions.sequence(
                        Actions.parallel(
                                Actions.fadeIn(DROP_FADE_SEC),
                                Actions.moveTo(targetX, targetY, DROP_MOVE_SEC, Interpolation.sine)),
                        Actions.run(
                                () -> {
                                    float px = targetCx / stageW;
                                    float py = targetCy / stageH;
                                    card.setPercentPosition(px, py);
                                    activeCount++;       // +1 on drop complete
                                    logger.info("[CardCount] after drop = {}", getActiveCardCount());
                                })));
    }

    /** Inner draggable card class. */
    private static final class DraggableCard extends Image {
        private final Stage stage;
        private final LevelGameArea currentArea;

        //New: Save specifications to facilitate selecting the right factory when placing
        private final CardSpec spec;

        private float percentW = 0.12f;
        private float posPx = 0.50f;
        private float posPy = 0.25f;
        private int lastW = -1, lastH = -1;

        // 【Past】DraggableCard(Stage stage, TextureAtlas atlas, String regionName, LevelGameArea area)
        // 【New】Pass in CardSpec instead
        DraggableCard(Stage stage, TextureAtlas atlas, CardSpec spec, LevelGameArea area) {
            // 【Past】super(new TextureRegionDrawable(atlas.findRegion(regionName)));
            // 【New】Get the texture from spec.atlasRegion
            super(new TextureRegionDrawable(atlas.findRegion(spec.atlasRegion)));
            if (getDrawable() == null)
                // 【Past】throw new IllegalArgumentException("Region not found: " + regionName);
                // 【New】Error message synchronization spec
                throw new IllegalArgumentException("Region not found: " + spec.atlasRegion);

            this.stage = stage;
            this.currentArea = area;
            this.spec = spec;

            setOrigin(Align.center);
            setScaling(Scaling.stretch);
            setAlign(Align.center);
            setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
            addDrag();
            applyLayout();
        }

        void setPercentWidth(float p) {
            if (p < 0.05f) p = 0.05f;
            if (p > 0.25f) p = 0.25f;
            this.percentW = p;
            applyLayout();
        }

        void setPercentPosition(float px, float py) {
            this.posPx = px; this.posPy = py;
            applyLayout();
        }

        @Override public void act(float delta) {
            super.act(delta);
            int sw = (int) stage.getWidth(), sh = (int) stage.getHeight();
            if (sw != lastW || sh != lastH) applyLayout();
        }

        private void applyLayout() {
            int sw = (int) stage.getWidth(), sh = (int) stage.getHeight();
            if (sw <= 0 || sh <= 0 || getDrawable() == null) return;
            float targetW = sw * percentW;

            float aspect = getDrawable().getMinHeight() / getDrawable().getMinWidth();
            float targetH = targetW * aspect;
            setSize(targetW, targetH);
            float cx = sw * posPx, cy = sh * posPy;
            setPosition(cx - targetW / 2f, cy - targetH / 2f);
            lastW = sw; lastH = sh;
        }

        private void addDrag() {
            addListener(new DragListener() {
                float grabX, grabY;

                @Override public void dragStart(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int p) {
                    toFront(); grabX = x; grabY = y;
                }

                @Override public void drag(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int p) {
                    float nx = getX() + (x - grabX), ny = getY() + (y - grabY);
                    setPosition(nx, ny);
                    posPx = (nx + getWidth()/2f) / stage.getWidth();
                    posPy = (ny + getHeight()/2f) / stage.getHeight();
                }

                @Override public void dragStop(com.badlogic.gdx.scenes.scene2d.InputEvent e, float x, float y, int p) {
                    tryPlaceViaOfficialPipeline();
                }
            });
        }

        private void tryPlaceViaOfficialPipeline() {
            if (currentArea == null) return;

            Vector2 stageCenter = new Vector2(getX() + getWidth()/2f, getY() + getHeight()/2f);
            Vector2 screen = stage.stageToScreenCoordinates(new Vector2(stageCenter));

            com.badlogic.gdx.math.GridPoint2 worldPx =
                    currentArea.stageToWorld(new com.badlogic.gdx.math.GridPoint2((int) screen.x, (int) screen.y));
            float wx = worldPx.x, wy = worldPx.y;

            LevelGameGrid grid = currentArea.getGrid();
            if (grid == null || grid.getRows() <= 0 || grid.getCols() <= 0) return;
            Entity tile0 = grid.getTile(0);
            if (tile0 == null) return;
            Vector2 off = tile0.getPosition();
            float tileSize = currentArea.getTileSize();

            int col = (int) Math.floor((wx - off.x) / tileSize);
            int row = (int) Math.floor((wy - off.y) / tileSize);
            if (col < 0 || col >= grid.getCols() || row < 0 || row >= grid.getRows()) {
                logger.info("[SlotCardEntity] Placement failed: outside grid");
                return;
            }
            int pos = row * grid.getCols() + col;

            boolean occupiedBefore = grid.isOccupiedIndex(pos);

            Entity selected =
                    new Entity().addComponent(new DeckInputComponent(currentArea, spec.factory));
            selected.create();

            currentArea.setSelectedUnit(selected);
            currentArea.setIsCharacterSelected(true);
            currentArea.spawnUnit(pos);

            boolean occupiedAfter = grid.isOccupiedIndex(pos);
            if (!occupiedBefore && occupiedAfter) {
                remove();
                activeCount = Math.max(0, activeCount - 1); // -1 on successful placement
                logger.info("[CardCount] after placement = {}", getActiveCardCount());
                // 【原】logger.info("[SlotCardEntity] Placed slingshooter at row={}, col={}, pos={}", row, col, pos);
                logger.info("[SlotCardEntity] Placed '{}' at row={}, col={}, pos={}",
                        spec.atlasRegion, row, col, pos);
            } else {
                logger.info("[SlotCardEntity] Placement did not take effect");
            }
        }
    }

    // === Resource loader ===
    private static void ensureAtlas() {
        if (cardsAtlas != null) return;
        try {
            cardsAtlas = ServiceLocator.getResourceService().getAsset(CARDS_ATLAS_PATH, TextureAtlas.class);
        } catch (Exception ignore) { cardsAtlas = null; }
        if (cardsAtlas == null) {
            if (!Gdx.files.internal(CARDS_ATLAS_PATH).exists())
                throw new IllegalStateException("Card atlas not found: " + CARDS_ATLAS_PATH);
            cardsAtlas = new TextureAtlas(Gdx.files.internal(CARDS_ATLAS_PATH));
            ownsCardsAtlas = true;
        }
    }

    /** Optional: dispose when unloading scene */
    public static void disposeUi() {
        if (ownsCardsAtlas && cardsAtlas != null) {
            cardsAtlas.dispose(); cardsAtlas = null; ownsCardsAtlas = false;
        }
    }
}
