package com.csse3200.game.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Provides functionality to draw lines/shapes to the screen for debug purposes. */
public class DebugRenderer {
  private static final Logger logger = LoggerFactory.getLogger(DebugRenderer.class);
  private final Box2DDebugRenderer physicsRenderer;
  private final ShapeRenderer shapeRenderer;

  private World physicsWorld;
  private boolean active = true;
  private DrawRequest[] drawRequests = new DrawRequest[10];
  private int requestCount = 0;

  public DebugRenderer() {
    this(new Box2DDebugRenderer(), new ShapeRenderer());
  }

  public DebugRenderer(Box2DDebugRenderer physicsRenderer, ShapeRenderer shapeRenderer) {
    this.physicsRenderer = physicsRenderer;
    this.shapeRenderer = shapeRenderer;

    for (int i = 0; i < drawRequests.length; i++) {
      drawRequests[i] = new DrawRequest();
    }
  }

  public void renderPhysicsWorld(World physicsWorld) {
    this.physicsWorld = physicsWorld;
  }

  /**
   * Draw a line between two positions
   *
   * @param from start position
   * @param to end position
   */
  public void drawLine(Vector2 from, Vector2 to) {
    drawLine(from, to, Color.WHITE, 1f);
  }

  /**
   * Draw a line between two positions
   *
   * @param from start position
   * @param to end position
   * @param color line color
   * @param lineWidth line width
   */
  public void drawLine(Vector2 from, Vector2 to, Color color, float lineWidth) {
    ensureCapacity();
    DrawRequest request = drawRequests[requestCount];
    request.setDrawRequestType(DrawRequestType.LINE);
    request.setPos(from);
    request.setEnd(to);
    request.setColor(color);
    request.setLineWidth(lineWidth);
    requestCount++;
  }

  /**
   * Draw a rectangle
   *
   * @param pos position of bottom left corner
   * @param size width/height
   */
  public void drawRectangle(Vector2 pos, Vector2 size) {
    drawRectangle(pos, size, Color.WHITE, 1f);
  }

  /**
   * Draw a rectangle
   *
   * @param pos position of bottom left corner
   * @param size width/height
   * @param color line color
   * @param lineWidth line width
   */
  public void drawRectangle(Vector2 pos, Vector2 size, Color color, float lineWidth) {
    ensureCapacity();
    DrawRequest request = drawRequests[requestCount];
    request.setDrawRequestType(DrawRequestType.RECT);
    request.setPos(pos);
    request.setEnd(size);
    request.setColor(color);
    request.setLineWidth(lineWidth);
    requestCount++;
  }

  /**
   * Sets the active state of debug rendering.
   *
   * @param active true to enable debug drawing, false to disable
   */
  public void setActive(boolean active) {
    logger.info("Set debug to: {}", active);
    this.active = active;
  }

  public boolean getActive() {
    return active;
  }

  public void render(Matrix4 projMatrix) {
    if (!active) {
      return;
    }

    if (physicsWorld != null) {
      physicsRenderer.render(physicsWorld, projMatrix);
    }

    shapeRenderer.setProjectionMatrix(projMatrix);
    shapeRenderer.begin(ShapeType.Line);
    for (int i = 0; i < requestCount; i++) {
      switch (drawRequests[i].getDrawRequestType()) {
        case LINE:
          renderLine(drawRequests[i]);
          break;
        case RECT:
          renderRect(drawRequests[i]);
          break;
        default:
          logger.error("Attempting to draw unsupported shape!");
          break;
      }
    }
    shapeRenderer.end();
    requestCount = 0;
  }

  private void renderLine(DrawRequest request) {
    Gdx.gl.glLineWidth(request.getLineWidth());
    shapeRenderer.setColor(request.getColor());
    shapeRenderer.line(request.getPos(), request.getEnd());
  }

  private void renderRect(DrawRequest request) {
    Gdx.gl.glLineWidth(request.getLineWidth());
    shapeRenderer.setColor(request.getColor());
    shapeRenderer.rect(
        request.getPos().x, request.getPos().y, request.getEnd().x, request.getEnd().y);
  }

  /**
   * Use a custom resizing array instead of an array list to avoid allocating/deallocating requests
   * every render. Does not shrink, so a large amount of debug drawing on a single frame can
   * permanently use memory.
   */
  private void ensureCapacity() {
    if (requestCount >= drawRequests.length - 1) {
      DrawRequest[] newArr = new DrawRequest[(int) (requestCount * 1.4f)];
      System.arraycopy(drawRequests, 0, newArr, 0, drawRequests.length);
      for (int i = drawRequests.length; i < newArr.length; i++) {
        newArr[i] = new DrawRequest();
      }
      drawRequests = newArr;
    }
  }

  /**
   * Stores a draw request. One class stores the potential options for all shapes, to prevent
   * allocating/deallocating new instances every render.
   */
  static class DrawRequest {
    private DrawRequestType drawRequestType;
    private Vector2 pos;
    private Color color;
    private float lineWidth;
    private Vector2 end;

    public DrawRequestType getDrawRequestType() {
      return drawRequestType;
    }

    public Vector2 getPos() {
      return pos;
    }

    public Color getColor() {
      return color;
    }

    public float getLineWidth() {
      return lineWidth;
    }

    public Vector2 getEnd() {
      return end;
    }

    public void setDrawRequestType(DrawRequestType drawRequestType) {
      this.drawRequestType = drawRequestType;
    }

    public void setPos(Vector2 pos) {
      this.pos = pos;
    }

    public void setColor(Color color) {
      this.color = color;
    }

    public void setLineWidth(float lineWidth) {
      this.lineWidth = lineWidth;
    }

    public void setEnd(Vector2 end) {
      this.end = end;
    }
  }

  enum DrawRequestType {
    LINE,
    RECT
  }
}
