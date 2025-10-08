package com.csse3200.game.components;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

/** Component that manages camera positioning and rendering for an entity. */
public class CameraComponent extends Component {
  private final Camera camera;
  private Vector2 lastPosition;

  /** Creates a camera component with a default orthographic camera. */
  public CameraComponent() {
    this(new OrthographicCamera());
  }

  /**
   * Creates a camera component with the specified camera.
   *
   * @param camera the camera to use
   */
  public CameraComponent(Camera camera) {
    this.camera = camera;
    lastPosition = Vector2.Zero.cpy();
  }

  @Override
  public void update() {
    Vector2 position = entity.getPosition();
    if (!lastPosition.epsilonEquals(entity.getPosition())) {
      camera.position.set(position.x, position.y, 0f);
      lastPosition = position;
      camera.update();
    }
  }

  /**
   * Gets the projection matrix of the camera.
   *
   * @return the combined projection matrix
   */
  public Matrix4 getProjectionMatrix() {
    return camera.combined;
  }

  /**
   * Gets the camera instance.
   *
   * @return the camera
   */
  public Camera getCamera() {
    return camera;
  }

  /**
   * Resizes the camera viewport based on screen dimensions.
   *
   * @param screenWidth the screen width
   * @param screenHeight the screen height
   * @param gameWidth the desired game width
   */
  public void resize(int screenWidth, int screenHeight, float gameWidth) {
    float ratio = (float) screenHeight / screenWidth;
    camera.viewportWidth = gameWidth;
    camera.viewportHeight = gameWidth * ratio;
    camera.update();
  }
}
