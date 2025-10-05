package com.csse3200.game.components.worldmap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import com.csse3200.game.ui.WorldMapNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** Renders a world map node using the engine's rendering system */
public class WorldMapNodeRenderComponent extends UIComponent {
  private final WorldMapNode node;
  private final Vector2 worldSize;
  private final float nodeSize;
  private boolean showPrompt = false;
  private boolean anyPrompt = false;

  // assets
  private Texture glow, texW, texA, texS, texD, texE;
  private BitmapFont font;
  private float pulseT = 0f;

  // visuals
  private static final float ARROW_SIZE = 56f;
  private static final float ARROW_OFFSET = 0.95f;
  private static final float LABEL_GAP = 10f;
  private static final float FONT_SCALE = 1.15f;

  // local JSON cache: nodeKey -> (dir -> PathDef)
  private final Map<String, Map<String, com.csse3200.game.services.WorldMapService.PathDef>>
      localPaths = new HashMap<>();

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
    ServiceLocator.getWorldMapService().registerNodeRenderComponent(this);
    // read JSON locally
    loadLocalPathConfig("configs/worldmap_paths.json");

    // get textures if preloaded
    var rs = ServiceLocator.getResourceService();
    try {
      glow = rs.getAsset("images/ui/glow.png", Texture.class);
    } catch (Exception ignored) {
    }
    try {
      texW = rs.getAsset("images/ui/dir_w.png", Texture.class);
    } catch (Exception ignored) {
    }
    try {
      texA = rs.getAsset("images/ui/dir_a.png", Texture.class);
    } catch (Exception ignored) {
    }
    try {
      texS = rs.getAsset("images/ui/dir_s.png", Texture.class);
    } catch (Exception ignored) {
    }
    try {
      texD = rs.getAsset("images/ui/dir_d.png", Texture.class);
    } catch (Exception ignored) {
    }
    try {
      texE = rs.getAsset("images/ui/key_e.png", Texture.class);
    } catch (Exception ignored) {
    }

    font = new BitmapFont();
    font.getData().setScale(FONT_SCALE);
  }

  /**
   * Updates the proximity state for this node.
   *
   * @param nearbyNode the node the player is currently near, or null if none
   */
  public void updateProximityState(WorldMapNode nearbyNode) {
    anyPrompt = (nearbyNode != null); // light up all when someone is near
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

    // Glow
    if (glow != null && anyPrompt) {
      pulseT += Gdx.graphics.getDeltaTime();
      final float pulse = 0.5f + 0.5f * MathUtils.sin(2f * MathUtils.PI * 0.8f * pulseT);

      float size = drawSize + 28f; // ring size
      float r = 0.65f, g = 0.80f, b = 1.00f;

      float a = 0.78f;

      if (showPrompt) {
        r = 0.70f;
        g = 1.00f;
        b = 0.55f;
        a = 0.78f + 0.22f * pulse;
        size += 6f;
      }

      batch.setColor(r, g, b, a);
      batch.draw(glow, cx - size * 0.5f, cy - size * 0.5f, size, size);
      batch.setColor(r, g, b, a * 0.55f);
      batch.draw(glow, cx - size * 0.5f, cy - size * 0.5f, size, size);
      batch.setColor(1f, 1f, 1f, 1f); // reset
    }

    // on-node check
    boolean onNode = false;
    try {
      float px = ServiceLocator.getProfileService().getProfile().getWorldMapX();
      float py = ServiceLocator.getProfileService().getProfile().getWorldMapY();
      float dx = px - x, dy = py - y;
      float r = Math.max(drawSize * 0.45f, 36f);
      onNode = dx * dx + dy * dy <= r * r;
    } catch (Exception ignored) {
    }
    if (!onNode) return;

    // arrows + labels strictly by next
    String key = node.getRegistrationKey();

    drawDirWithLabel(
        batch,
        texW,
        cx - ARROW_SIZE * 0.5f,
        cy + drawSize * ARROW_OFFSET - ARROW_SIZE * 0.5f,
        getLocalPath(key, "W"),
        "W",
        0f,
        (ARROW_SIZE + LABEL_GAP));

    drawDirWithLabel(
        batch,
        texS,
        cx - ARROW_SIZE * 0.5f,
        cy - drawSize * ARROW_OFFSET - ARROW_SIZE * 0.5f,
        getLocalPath(key, "S"),
        "S",
        0f,
        -(ARROW_SIZE + LABEL_GAP));

    drawDirWithLabel(
        batch,
        texA,
        cx - drawSize * ARROW_OFFSET - ARROW_SIZE * 0.5f,
        cy - ARROW_SIZE * 0.5f,
        getLocalPath(key, "A"),
        "A",
        -(ARROW_SIZE + LABEL_GAP),
        0f);

    drawDirWithLabel(
        batch,
        texD,
        cx + drawSize * ARROW_OFFSET - ARROW_SIZE * 0.5f,
        cy - ARROW_SIZE * 0.5f,
        getLocalPath(key, "D"),
        "D",
        (ARROW_SIZE + LABEL_GAP),
        0f);

    if (node.isUnlocked() && !node.isCompleted() && texE != null) {
      float es = 28f;
      batch.draw(texE, cx - es * 0.5f, drawY + drawSize + 10f, es, es);
    }
  }

  public void setShowPrompt(boolean showPrompt) {
    this.showPrompt = showPrompt;
  }

  // Draw arrow + label
  private void drawDirWithLabel(
      SpriteBatch batch,
      Texture tex,
      float dx,
      float dy,
      com.csse3200.game.services.WorldMapService.PathDef def,
      String dir,
      float labelOffsetX,
      float labelOffsetY) {

    if (tex == null) return;

    if (def == null) {
      batch.setColor(1f, 1f, 1f, 0.25f);
      batch.draw(tex, dx, dy, ARROW_SIZE, ARROW_SIZE);
      batch.setColor(1f, 1f, 1f, 1f);
      return;
    }

    batch.draw(tex, dx, dy, ARROW_SIZE, ARROW_SIZE);
    if (font == null) return;

    if (font == null) return;
    String name = def.next;

    String inside = dir + " " + name;
    String below = name;

    float midX = dx + ARROW_SIZE * 0.5f;
    float midY = dy + ARROW_SIZE * 0.56f;

    GlyphLayout gl = new GlyphLayout(font, inside);
    boolean fitsInside = gl.width <= (ARROW_SIZE - 8f);

    if (fitsInside) {
      float tx = midX - gl.width * 0.5f;
      float ty = midY + gl.height * 0.45f;
      drawOutlinedText(batch, inside, tx, ty);
    } else {
      float old = font.getData().scaleX;
      font.getData().setScale(FONT_SCALE * 1.3f);
      GlyphLayout g2 = new GlyphLayout(font, dir);
      float tx2 = midX - g2.width * 0.5f;
      float ty2 = midY + g2.height * 0.45f;
      drawOutlinedText(batch, dir, tx2, ty2);
      font.getData().setScale(old);

      float tx3 = dx + labelOffsetX;
      float ty3 = dy + labelOffsetY + ARROW_SIZE * 0.5f + font.getCapHeight() * 0.5f;
      drawOutlinedText(batch, below, tx3, ty3);
    }
  }

  // Outlined white text
  private void drawOutlinedText(SpriteBatch b, String t, float x, float y) {
    font.setColor(0f, 0f, 0f, 1f);
    font.draw(b, t, x + 2f, y - 2f);
    font.draw(b, t, x - 2f, y - 2f);
    font.draw(b, t, x + 2f, y + 2f);
    font.draw(b, t, x - 2f, y + 2f);
    font.setColor(1f, 1f, 1f, 1f);
    font.draw(b, t, x, y);
  }

  // Load JSON to cache
  private void loadLocalPathConfig(String internalPath) {
    var file = Gdx.files.internal(internalPath);
    if (!file.exists()) return;

    localPaths.clear();
    JsonReader reader = new JsonReader();
    JsonValue root = reader.parse(file);

    for (JsonValue nodeEntry = root.child(); nodeEntry != null; nodeEntry = nodeEntry.next()) {
      String nodeKey = nodeEntry.name();
      Map<String, com.csse3200.game.services.WorldMapService.PathDef> dirMap = new HashMap<>();

      for (JsonValue keyEntry = nodeEntry.child(); keyEntry != null; keyEntry = keyEntry.next()) {
        String dir = keyEntry.name();
        var def = new com.csse3200.game.services.WorldMapService.PathDef();
        def.next = keyEntry.getString("next");
        def.waypoints = new ArrayList<>();
        JsonValue pathArr = keyEntry.get("path");
        if (pathArr != null) {
          for (JsonValue p = pathArr.child(); p != null; p = p.next()) {
            float wx = p.get(0).asFloat();
            float wy = p.get(1).asFloat();
            def.waypoints.add(new Vector2(wx, wy));
          }
        }
        dirMap.put(dir, def);
      }
      localPaths.put(nodeKey, dirMap);
    }
  }

  // Get path def
  private com.csse3200.game.services.WorldMapService.PathDef getLocalPath(
      String nodeKey, String dir) {
    Map<String, com.csse3200.game.services.WorldMapService.PathDef> m = localPaths.get(nodeKey);
    return m == null ? null : m.get(dir);
  }

  public String getKey() {
    return node.getRegistrationKey();
  }

  /** Access the underlying node data object */
  public WorldMapNode getNode() {
    return node;
  }

  /** Center position in world coordinates (used by render & hit test) */
  public Vector2 getCenterWorld() {
    float x = node.getPositionX() * worldSize.x;
    float y = node.getPositionY() * worldSize.y;
    return new Vector2(x + nodeSize * 0.5f, y + nodeSize * 0.5f);
  }

  /** Hit radius consistent with on-node logic */
  public float getHitRadius() {
    return Math.max(nodeSize * 0.45f, 36f);
  }

  /** Point-in-node test in world coords */
  public boolean hit(float worldX, float worldY) {
    Vector2 c = getCenterWorld();
    float r = getHitRadius();
    float dx = worldX - c.x;
    float dy = worldY - c.y;
    return (dx * dx + dy * dy) <= (r * r);
  }
}
