package com.csse3200.game.minigame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.services.ServiceLocator;
import java.util.List;
import java.util.Random;

public class ObstacleManager {
  private final List<ObstacleImage> obstacles;
  private final LaneManager laneManager;
  public final Random random;
  private float spawnTimer;
  private int obstaclesDodged = 0;
  private float elapsedTime = 0f;
  private static final float SPEED_GROWTH = 100f;

  public int getObstaclesDodged() {
    return obstaclesDodged;
  }

  private static class ObstacleImage {
    public Image image;
    public float speed;
    public boolean isAlive;

    public ObstacleImage(Image image, float speed) {
      this.image = image;
      this.speed = speed;
      this.isAlive = true;
    }
  }

  public ObstacleManager(LaneManager laneManager) {
    this.laneManager = laneManager;
    this.obstacles = new java.util.ArrayList<>();
    this.random = new Random();
    this.spawnTimer = 0f;
  }

  public void update(float deltaTime) {
    spawnTimer += deltaTime;
    elapsedTime += deltaTime / 1.5f;
    if (spawnTimer >= LaneConfig.OBSTACLE_SPAWN_INTERVAL
        && obstacles.size() < LaneConfig.OBSTACLE_MAX_COUNT) {
      spawnObstacle();
      spawnTimer = 0f;
    }
    List<ObstacleImage> toRemove = new java.util.ArrayList<>();
    for (ObstacleImage obstacle : obstacles) {
      if (!obstacle.isAlive) {
        continue;
      }
      moveObstacleDown(obstacle, deltaTime);

      if (obstacle.image.getY() < -obstacle.image.getHeight()) {
        obstacle.isAlive = false;
        obstaclesDodged++;
        toRemove.add(obstacle);
      }
    }
    for (ObstacleImage obstacle : toRemove) {
      obstacles.remove(obstacle);
      obstacle.image.remove();
    }
  }

  private void moveObstacleDown(ObstacleImage obstacle, float deltaTime) {
    float currentSpeed = obstacle.speed + (elapsedTime * SPEED_GROWTH);
    float newY = obstacle.image.getY() - (currentSpeed * deltaTime);
    obstacle.image.setPosition(obstacle.image.getX(), newY);
  }

  private void spawnObstacle() {
    int LaneIndex = random.nextInt(laneManager.getNumLanes());
    float x = laneManager.getLaneCenter(LaneIndex) - 32f;
    float y = Gdx.graphics.getHeight();
    Texture obstacleTexture =
        ServiceLocator.getResourceService().getAsset("images/Bomb.png", Texture.class);
    Image obstacleImage = new Image(obstacleTexture);
    obstacleImage.setSize(64f, 64f);
    obstacleImage.setPosition(x, y);
    ServiceLocator.getRenderService().getStage().addActor(obstacleImage);
    ObstacleImage obstacle = new ObstacleImage(obstacleImage, LaneConfig.OBSTACLE_BASE_SPEED);
    obstacles.add(obstacle);
  }

  public boolean checkCollision(Image playerImage) {
    for (ObstacleImage obstacle : obstacles) {
      if (!obstacle.isAlive) {
        continue;
      }
      if (isColliding(playerImage, obstacle.image)) {
        return true;
      }
    }
    return false;
  }

  private boolean isColliding(Image player, Image obstacle) {
    float playerX = player.getX();
    float playerY = player.getY();
    float playerWidth = player.getWidth();
    float playerHeight = player.getHeight();

    float obstacleX = obstacle.getX();
    float obstacleY = obstacle.getY();
    float obstacleWidth = obstacle.getWidth();
    float obstacleHeight = obstacle.getHeight();

    return playerX < obstacleX + obstacleWidth
        && playerX + playerWidth > obstacleX
        && playerY < obstacleY + obstacleHeight
        && playerY + playerHeight > obstacleY;
  }

  public void clearObstacles() {
    for (ObstacleImage obstacle : obstacles) {
      obstacle.image.remove();
    }
    obstacles.clear();
    spawnTimer = 0f;
  }

  public int getObstacleCount() {
    return obstacles.size();
  }

  public List<Image> getObstacles() {
    List<Image> obstacleImages = new java.util.ArrayList<>();
    for (ObstacleImage obstacle : obstacles) {
      if (obstacle.isAlive) {
        obstacleImages.add(obstacle.image);
      }
    }
    return obstacleImages;
  }
}
