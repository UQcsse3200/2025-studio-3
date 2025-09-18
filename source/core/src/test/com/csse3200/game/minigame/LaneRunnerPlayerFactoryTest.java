package com.csse3200.game.minigame;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.graphics.Texture;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class LaneRunnerPlayerFactoryTest {

  private MockedStatic<ServiceLocator> mockedServiceLocator;
  private ResourceService mockedResourceService;

  @BeforeEach
  void setUp() {
    mockedServiceLocator = mockStatic(ServiceLocator.class);
    mockedResourceService = mock(ResourceService.class);
    mockedServiceLocator.when(ServiceLocator::getResourceService).thenReturn(mockedResourceService);

    Texture mockedTexture = mock(Texture.class);
    when(mockedResourceService.getAsset(anyString(), eq(Texture.class))).thenReturn(mockedTexture);
  }

  @AfterEach
  void tearDown() {
    if (mockedServiceLocator != null) mockedServiceLocator.close();
  }

  @Test
  void testCreatePlayer() {
    LaneManager mockLaneManager = mock(LaneManager.class);
    when(mockLaneManager.getLaneCenter(1)).thenReturn(5.0f);

    Entity player = LaneRunnerPlayerFactory.createPlayer(mockLaneManager);

    assertNotNull(player, "Player entity should not be null");
    assertNotNull(
        player.getComponent(TextureRenderComponent.class),
        "Player should have TextureRenderComponent");
    assertNotNull(
        player.getComponent(MiniGameInputComponent.class),
        "Player should have MiniGameInputComponent");

    TextureRenderComponent textureRenderComponent =
        player.getComponent(TextureRenderComponent.class);
    assertNotNull(textureRenderComponent, "TextureRenderComponent should exist");

    assertEquals(
        LaneConfig.OBSTACLE_WIDTH,
        player.getScale().x,
        0.001f,
        "Player scale X should match OBSTACLE_WIDTH");
    assertEquals(
        LaneConfig.OBSTACLE_HEIGHT,
        player.getScale().y,
        0.001f,
        "Player scale Y should match OBSTACLE_HEIGHT");

    assertEquals(
        5.0f,
        player.getPosition().x,
        0.001f,
        "Player X position should be the centre of the starting lane");
    assertEquals(
        LaneConfig.PLAYER_Y,
        player.getPosition().y,
        0.001f,
        "Player Y position should match PLAYER_Y");

    verify(mockLaneManager).getLaneCenter(1);

    verify(mockedResourceService).getAsset("images/box_boy.png", Texture.class);
  }

  @Test
  void testMiniGameInputComponentInitialisation() {
    LaneManager mockLaneManager = mock(LaneManager.class);
    when(mockLaneManager.getLaneCenter(1)).thenReturn(5.0f);

    Entity player = LaneRunnerPlayerFactory.createPlayer(mockLaneManager);
    MiniGameInputComponent inputComponent = player.getComponent(MiniGameInputComponent.class);

    assertNotNull(inputComponent, "MiniGameInputComponent should not be present");
  }
}
