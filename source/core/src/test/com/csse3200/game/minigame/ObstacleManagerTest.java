package com.csse3200.game.minigame;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ObstacleManagerTest {
  private ObstacleManager obstacleManager;
  private LaneManager laneManager;

  @BeforeAll
  static void initGdx() {
    Gdx.graphics = mock(Graphics.class);
    when(Gdx.graphics.getWidth()).thenReturn(800);
    when(Gdx.graphics.getHeight()).thenReturn(600);
  }

  @BeforeEach
  void setUp() {
    ResourceService resourceService = mock(ResourceService.class);
    RenderService renderService = mock(RenderService.class);
    Stage stage = mock(Stage.class);
    when(resourceService.getAsset(anyString(), eq(Texture.class))).thenReturn(mock(Texture.class));
    when(renderService.getStage()).thenReturn(stage);

    ServiceLocator.registerRenderService(renderService);
    ServiceLocator.registerResourceService(resourceService);

    laneManager = new LaneManager(800);
    obstacleManager = new ObstacleManager(laneManager);
  }

  @AfterEach
  void cleanUp() {
    ServiceLocator.clear();
  }

  @Test
  void testSpawnObstacle() {
    assertEquals(0, obstacleManager.getObstacleCount());
    obstacleManager.update(LaneConfig.OBSTACLE_SPAWN_INTERVAL + 0.1f);
    assertEquals(1, obstacleManager.getObstacleCount());
  }

  @Test
  void testColissionDetection() {
    obstacleManager.update(LaneConfig.OBSTACLE_SPAWN_INTERVAL + 0.1f);

    Image obstacleImage = obstacleManager.getObstacles().get(0);
    Image playerImage = new Image(mock(Texture.class));
    playerImage.setSize(64, 64);
    playerImage.setPosition(obstacleImage.getX(), obstacleImage.getY());
    assertTrue(obstacleManager.checkCollision(playerImage));
  }
}
