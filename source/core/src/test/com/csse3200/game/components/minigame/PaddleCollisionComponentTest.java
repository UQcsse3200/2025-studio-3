package com.csse3200.game.components.minigame;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.services.MinigameService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(GameExtension.class)
@ExtendWith(MockitoExtension.class)
class PaddleCollisionComponentTest {
  @Mock private MinigameService mockMinigameService;

  private PaddleCollisionComponent collisionComponent;

  @BeforeEach
  void setUp() {
    // Setup service mocks
    ServiceLocator.registerMinigameService(mockMinigameService);

    // Create collision component
    collisionComponent = new PaddleCollisionComponent();
  }

  @AfterEach
  void tearDown() {
    ServiceLocator.clear();
  }

  @Test
  void testComponentCreation() {
    // Test basic component creation
    PaddleCollisionComponent component = new PaddleCollisionComponent();
    assertNotNull(component);
  }

  @Test
  void testComponentNotNull() {
    // Test that the component is not null
    assertNotNull(collisionComponent);
  }

  @Test
  void testComponentType() {
    // Test that the component is of the correct type
    assertTrue(collisionComponent instanceof PaddleCollisionComponent);
  }

  @Test
  void testComponentInheritance() {
    // Test that the component extends Component
    assertTrue(collisionComponent instanceof com.csse3200.game.components.Component);
  }
}
