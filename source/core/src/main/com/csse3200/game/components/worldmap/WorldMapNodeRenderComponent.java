package com.csse3200.game.components.worldmap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.WorldMapService;
import com.csse3200.game.ui.UIComponent;
import com.csse3200.game.ui.WorldMapNode;
import com.csse3200.game.utils.LevelType;

/** Renders a world map node using the engine's rendering system */
@SuppressWarnings("java:S1854") // SonarQube is throwing false positives for useless variables.
public class WorldMapNodeRenderComponent extends UIComponent {
  private final WorldMapNode node;
  private final Vector2 worldSize;
  private final float nodeSize;
  private boolean showPrompt = false;
  private static final int KEY_INSET = 6;
  private static final int LABEL_INSET = 32;
  private TextureRegion keyUpR;
  private TextureRegion keyDownR;
  private TextureRegion keyLeftR;
  private TextureRegion keyRightR;
  private TextureRegion labelBgR;
  private Texture glow;
  private Texture labelBg;
  private Texture keyUp;
  private Texture keyDown;
  private Texture keyLeft;
  private Texture keyRight;
  private BitmapFont font;
  private float pulseT = 0f;
  private static final float ARROW_SIZE = 44f;
  private static final float ARROW_OFFSET = 0.78f;
  private static final float LABEL_GAP = 5f;
  private static final float FONT_SCALE = 1.15f;
  private static final float VISUAL_TRIM_Y = 14f;
  private static final float NAME_GAP = -14f;
  private static final float LABEL_PAD_X = 45f;
  private static final float LABEL_PAD_Y = 36f;
  private static final float MIN_W_FACTOR = 2.2f;
  private static final float MIN_H_FACTOR = 1.35f;


  /**
   * Constructor for the world map node render component.
   *
   * @param node the node to render
   * @param worldSize the size of the world map
   * @param nodeSize the size of the node
   */
  public WorldMapNodeRenderComponent(WorldMapNode node, Vector2 worldSize, float nodeSize) {
    this.node = node;
    this.worldSize = worldSize;
    this.nodeSize = nodeSize;
  }

  @Override
  public void create() {
    super.create();

    // get textures if preloaded
    var rs = ServiceLocator.getResourceService();
    try {
      glow = rs.getAsset("images/ui/glow.png", Texture.class);
      keyUp = rs.getAsset("images/ui/keycap_up.png", Texture.class);
      keyDown = rs.getAsset("images/ui/keycap_down.png", Texture.class);
      keyLeft = rs.getAsset("images/ui/keycap_left.png", Texture.class);
      keyRight = rs.getAsset("images/ui/keycap_right.png", Texture.class);
      labelBg = rs.getAsset("images/ui/label_bg.png", Texture.class);

      if (keyUp != null) {
        keyUpR =
            new TextureRegion(
                keyUp,
                KEY_INSET,
                KEY_INSET,
                keyUp.getWidth() - 2 * KEY_INSET,
                keyUp.getHeight() - 2 * KEY_INSET);
      }
      if (keyDown != null) {
        keyDownR =
            new TextureRegion(
                keyDown,
                KEY_INSET,
                KEY_INSET,
                keyDown.getWidth() - 2 * KEY_INSET,
                keyDown.getHeight() - 2 * KEY_INSET);
      }
      if (keyLeft != null) {
        keyLeftR =
            new TextureRegion(
                keyLeft,
                KEY_INSET,
                KEY_INSET,
                keyLeft.getWidth() - 2 * KEY_INSET,
                keyLeft.getHeight() - 2 * KEY_INSET);
      }
      if (keyRight != null) {
        keyRightR =
            new TextureRegion(
                keyRight,
                KEY_INSET,
                KEY_INSET,
                keyRight.getWidth() - 2 * KEY_INSET,
                keyRight.getHeight() - 2 * KEY_INSET);
      }
      if (labelBg != null) {
        labelBgR =
            new TextureRegion(
                labelBg,
                LABEL_INSET,
                LABEL_INSET,
                labelBg.getWidth() - 2 * LABEL_INSET,
                labelBg.getHeight() - 2 * LABEL_INSET);
      }
    } catch (Exception ignored) {
      // Skip missing assets
    }

    font = ServiceLocator.getGlobalResourceService().generateFreeTypeFont("Default", 20);
  }

  /**
   * Updates the proximity state for this node.
   *
   * @param nearbyNode the node the player is currently near, or null if none
   */
  public void updateProximityState(WorldMapNode nearbyNode) {
    showPrompt =
        nearbyNode != null
            && this.node.getRegistrationKey().equals(nearbyNode.getRegistrationKey());
  }

  @Override
  protected void draw(SpriteBatch batch) {
    Texture nodeTexture =
        ServiceLocator.getResourceService().getAsset(node.getNodeTexture(), Texture.class);
    if (node.isCompleted()) {
      nodeTexture =
          ServiceLocator.getResourceService().getAsset("images/nodes/completed.png", Texture.class);
    } else if (!node.isUnlocked()) {
      nodeTexture =
          ServiceLocator.getResourceService().getAsset("images/nodes/locked.png", Texture.class);
    }

    // Calculate position from node's world coordinates
    float x = node.getPositionX() * worldSize.x;
    float y = node.getPositionY() * worldSize.y;

    // Draw with slight enlargement if hovering/nearby
    float drawSize = nodeSize;
    float drawX = x;
    float drawY = y;

    if (showPrompt) {
      drawSize += 8f;
      drawX -= 4f;
      drawY -= 4f;
    }

    // Draw node icon
    batch.draw(nodeTexture, drawX, drawY, drawSize, drawSize);
    final float cx = drawX + drawSize * 0.5f;
    final float cy = drawY + drawSize * 0.5f;

    if (glow != null) {
      drawGlow(batch, cx, cy, drawSize);
    }

    // on-node check
    boolean onNode = false;
    try {
      float px = ServiceLocator.getProfileService().getProfile().getWorldMapX();
      float py = ServiceLocator.getProfileService().getProfile().getWorldMapY();
      float dx = px - x;
      float dy = py - y;
      float r = Math.max(drawSize * 0.45f, 36f);
      onNode = dx * dx + dy * dy <= r * r;
    } catch (Exception ignored) {
      // Skip if profile not ready
    }
    if (!onNode) return;

    // arrows + labels strictly by next
    String key = node.getRegistrationKey();

    // Get key names from settings service
    Settings settings = ServiceLocator.getSettingsService().getSettings();
    String upKeyName = Input.Keys.toString(settings.getUpButton());
    String downKeyName = Input.Keys.toString(settings.getDownButton());
    String leftKeyName = Input.Keys.toString(settings.getLeftButton());
    String rightKeyName = Input.Keys.toString(settings.getRightButton());

    WorldMapService wms = ServiceLocator.getWorldMapService();

    // Up
    drawDirWithLabel(
        batch,
        keyUp,
        new Vector2(cx - ARROW_SIZE * 0.5f, cy + drawSize * ARROW_OFFSET - ARROW_SIZE * 0.5f),
        wms.getPath(key, WorldMapService.Direction.UP),
        upKeyName,
        new Vector2(0f, +1f));
    // Down
    drawDirWithLabel(
        batch,
        keyDown,
        new Vector2(cx - ARROW_SIZE * 0.5f, cy - drawSize * ARROW_OFFSET - ARROW_SIZE * 0.5f),
        wms.getPath(key, WorldMapService.Direction.DOWN),
        downKeyName,
        new Vector2(0f, -1f));
    // Left
    drawDirWithLabel(
        batch,
        keyLeft,
        new Vector2(cx - drawSize * ARROW_OFFSET - ARROW_SIZE * 0.5f, cy - ARROW_SIZE * 0.5f),
        wms.getPath(key, WorldMapService.Direction.LEFT),
        leftKeyName,
        new Vector2(-1f, 0f));
    // Right
    drawDirWithLabel(
        batch,
        keyRight,
        new Vector2(cx + drawSize * ARROW_OFFSET - ARROW_SIZE * 0.5f, cy - ARROW_SIZE * 0.5f),
        wms.getPath(key, WorldMapService.Direction.RIGHT),
        rightKeyName,
        new Vector2(+1f, 0f));

    if (font != null && showPrompt) {
      // Get interaction key name from settings service
      String interactionKeyName =
          Input.Keys.toString(settings.getInteractionButton());
      String hint = "Press " + interactionKeyName + " to Enter";
      float sY = cy - drawSize * ARROW_OFFSET - ARROW_SIZE * 0.5f;

      // Check if there's a down option - if not, move the hint 20px higher
      boolean hasDownOption = wms.getPath(key, WorldMapService.Direction.DOWN) != null;
      float verticalOffset = hasDownOption ? 0f : 20f;

      float oldScale = font.getData().scaleX;
      font.getData().setScale(FONT_SCALE * 0.90f);
      GlyphLayout hl = new GlyphLayout(font, hint);

      float tx = cx - hl.width * 0.5f;
      float ty = sY - LABEL_GAP - hl.height - 14f + verticalOffset;

      font.setColor(1f, 1f, 1f, 1f);
      font.draw(batch, hint, tx, ty);
      font.getData().setScale(oldScale);
    }
  }

  private void drawGlow(SpriteBatch batch, float cx, float cy, float drawSize) {
    pulseT += Gdx.graphics.getDeltaTime();
    float s = (MathUtils.sin(2f * MathUtils.PI * 1.2f * pulseT) + 1f) * 0.5f;

    float r = showPrompt ? 0.70f : 0.65f;
    float g = showPrompt ? 1.00f : 0.80f;
    float b = showPrompt ? 0.55f : 1.00f;

    float baseSize = drawSize + 26f;
    float size = baseSize + 3f * s;

    float baseA = showPrompt ? 0.70f : 0.50f;
    float a = baseA + 0.25f * s;

    batch.setColor(r, g, b, a);
    batch.draw(glow, cx - size * 0.5f, cy - size * 0.5f, size, size);
    batch.setColor(r, g, b, a * 0.45f);
    batch.draw(glow, cx - size * 0.5f, cy - size * 0.5f, size, size);
    batch.setColor(1f, 1f, 1f, 1f);
  }

  /**
   * Draws a label.
   * 
   * @param batch the sprite batch
   * @param name the name of the label
   * @param midX the x position of the center
   * @param midY the y position of the center
   * @param bgW the width of the background
   * @param bgH the height of the background
   * @param gl the glyph layout
   */
  private void drawLabel(
      SpriteBatch batch,
      String name,
      float midX,
      float midY,
      float bgW,
      float bgH,
      GlyphLayout gl) {
    float bgX = midX - bgW * 0.5f;
    float bgY = midY - bgH * 0.5f;
    batch.draw(labelBgR != null ? labelBgR : new TextureRegion(labelBg), bgX, bgY, bgW, bgH);
    font.setColor(1f, 1f, 1f, 1f);
    float tx = midX - gl.width * 0.5f;
    float ty = midY + gl.height * 0.35f;
    drawText(batch, name, tx, ty);
  }

  /**
   * Gets the texture region for a given texture.
   * 
   * @param tex the texture
   * @return the texture region
   */
  private TextureRegion regionFor(Texture tex) {
    if (tex == keyUp) return keyUpR;
    if (tex == keyDown) return keyDownR;
    if (tex == keyLeft) return keyLeftR;
    if (tex == keyRight) return keyRightR;
    return null;
  }

  /**
   * Draws a direction with a label.
   * 
   * @param batch the sprite batch
   * @param tex the texture
   * @param pos the position
   * @param def the path definition
   * @param dir the direction
   * @param labelOffset the offset of the label
   */
  private void drawDirWithLabel(
      SpriteBatch batch, Texture tex, Vector2 pos, WorldMapService.Path def, String dir, Vector2 labelOffset) {
    if (tex == null) return;

    // 1) Draw keycap
    batch.setColor(1f, 1f, 1f, def == null ? 0.35f : 1f);
    batch.draw(
      regionFor(tex),
      pos.x,
      pos.y,
      ARROW_SIZE,
      ARROW_SIZE);
    batch.setColor(1f, 1f, 1f, 1f);

    // 2) Center Letter
    if (font != null) {
      drawCenterLetter(batch, pos, dir, def);
    }

    // 3) Draw label if path exists
    if (def != null && font != null && labelBg != null) {
      drawPathLabel(batch, pos, def, labelOffset);
    }
  }

  /**
   * Draws the center letter.
   * 
   * @param batch the sprite batch
   * @param pos the position
   * @param dir the direction
   * @param def the path definition
   */
  private void drawCenterLetter(SpriteBatch batch, Vector2 pos, String dir, WorldMapService.Path def) {
    font.getData().setScale(FONT_SCALE * 1.10f);

    GlyphLayout glyphLayout = new GlyphLayout(font, dir);
    float centerX = pos.x + ARROW_SIZE * 0.5f - glyphLayout.width * 0.5f;
    float centerY = pos.y + ARROW_SIZE * 0.56f + glyphLayout.height * 0.45f;

    font.setColor(1f, 1f, 1f, def == null ? 0.55f : 1f);
    drawText(batch, dir, centerX, centerY);
  }

  private void drawPathLabel(SpriteBatch batch, Vector2 pos, WorldMapService.Path def, Vector2 labelOffset) {
    String nodeKey = def.destination();
    LevelType nodeType = LevelType.fromKey(nodeKey);
    String nodeName = nodeType.toString();

    font.getData().setScale(FONT_SCALE * 0.90f);

    GlyphLayout nameLayout = new GlyphLayout(font, nodeName);
    float backgroundWidth =
        Math.max(nameLayout.width + LABEL_PAD_X * 2f, ARROW_SIZE * MIN_W_FACTOR);
    float backgroundHeight =
        Math.max(nameLayout.height + LABEL_PAD_Y * 2f, ARROW_SIZE * MIN_H_FACTOR);

    // Calculate label position
    float labelX = pos.x + ARROW_SIZE * 0.5f;
    float labelY = pos.y + ARROW_SIZE * 0.5f;

    float sideX = Math.signum(labelOffset.x);
    float sideY = Math.signum(labelOffset.y);

    if (sideX > 0f) labelX = pos.x + ARROW_SIZE + NAME_GAP + backgroundWidth * 0.5f;
    else if (sideX < 0f) labelX = pos.x - NAME_GAP - backgroundWidth * 0.5f;
    if (sideY > 0f)
      labelY = pos.y + ARROW_SIZE + (NAME_GAP - VISUAL_TRIM_Y) + backgroundHeight * 0.5f;
    else if (sideY < 0f) labelY = pos.y - (NAME_GAP - VISUAL_TRIM_Y) - backgroundHeight * 0.5f;

    drawLabel(batch, nodeName, labelX, labelY, backgroundWidth, backgroundHeight, nameLayout);
  }

  /**
   * Draws text.
   * 
   * @param batch the sprite batch
   * @param text the text to draw
   * @param x the x position
   * @param y the y position
   */
  private void drawText(SpriteBatch batch, String text, float x, float y) {
    font.setColor(1f, 1f, 1f, 1f);
    font.draw(batch, text, x, y);
  }

  /**
   * Gets the key of the node.
   * 
   * @return the key of the node
   */
  public String getKey() {
    return node.getRegistrationKey();
  }

  /** 
   * Access the underlying node data object
   * 
   * @return the underlying node data object
   */
  public WorldMapNode getNode() {
    return node;
  }

  /** 
   * Center position in world coordinates (used by render and hit test)
   * 
   * @return the center position in world coordinates
   */
  public Vector2 getCenterWorld() {
    float x = node.getPositionX() * worldSize.x;
    float y = node.getPositionY() * worldSize.y;
    return new Vector2(x + nodeSize * 0.5f, y + nodeSize * 0.5f);
  }

  /** 
   * Hit radius consistent with on-node logic
   * 
   * @return the hit radius
   */
  public float getHitRadius() {
    return Math.max(nodeSize * 0.45f, 36f);
  }

  /** 
   * Point-in-node test in world coords
   * 
   * @param worldX the x position in world coordinates
   * @param worldY the y position in world coordinates
   * @return true if the point is in the node, false otherwise
   */
  public boolean hit(float worldX, float worldY) {
    Vector2 c = getCenterWorld();
    float r = getHitRadius();
    float dx = worldX - c.x;
    float dy = worldY - c.y;
    return (dx * dx + dy * dy) <= (r * r);
  }
}
