package com.csse3200.game.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.csse3200.game.entities.EntityService;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.input.InputService;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.rendering.RenderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class ServiceLocatorTest {

  private EntityService entityService;
  private RenderService renderService;
  private PhysicsService physicsService;
  private GameTime gameTime;
  private InputService inputService;
  private ResourceService resourceService;
  private CurrencyService currencyService;
  private ProfileService profileService;
  private ItemEffectsService itemEffectsService;
  private CutsceneService cutsceneService;
  private WorldMapService worldMapService;
  private DialogService dialogService;
  private ResourceService globalResourceService;
  private ConfigService configService;
  private SettingsService settingsService;
  private DiscordRichPresenceService discordRichPresenceService;

  @BeforeEach
  void setUp() {
    ServiceLocator.clear();

    entityService = new EntityService();
    renderService = new RenderService();
    physicsService = mock(PhysicsService.class);
    gameTime = new GameTime();
    inputService = new InputService();
    resourceService = new ResourceService();
    currencyService = new CurrencyService(0, 100);
    profileService = new ProfileService();
    itemEffectsService = new ItemEffectsService();
    cutsceneService = new CutsceneService();
    worldMapService = new WorldMapService();
    dialogService = new DialogService();
    globalResourceService = new ResourceService();
    configService = new ConfigService();
    settingsService = mock(SettingsService.class);
    discordRichPresenceService = new DiscordRichPresenceService();
  }

  @AfterEach
  void tearDown() {
    ServiceLocator.clear();

    if (discordRichPresenceService != null) {
      ServiceLocator.deregisterDiscordRichPresenceService();
    }

    if (configService != null) {
      ServiceLocator.deregisterConfigService();
    }

    if (dialogService != null) {
      ServiceLocator.deregisterDialogService();
    }

    if (globalResourceService != null) {
      ServiceLocator.deregisterGlobalResourceService();
    }

    if (profileService != null) {
      ServiceLocator.deregisterProfileService();
    }

    if (worldMapService != null) {
      ServiceLocator.deregisterWorldMapService();
    }

    if (cutsceneService != null) {
      ServiceLocator.deregisterCutsceneService();
    }

    if (settingsService != null) {
      ServiceLocator.deregisterSettingsService();
    }
  }

  @Test
  void shouldRegisterAndRetrieveAllServices() {
    registerAllServices();
    assertAllServicesRegistered();
  }

  @Test
  void shouldClearTransientServices() {
    registerAllServices();

    ServiceLocator.clear();

    // Transient services should be null after clear
    assertNull(ServiceLocator.getEntityService());
    assertNull(ServiceLocator.getRenderService());
    assertNull(ServiceLocator.getPhysicsService());
    assertNull(ServiceLocator.getTimeSource());
    assertNull(ServiceLocator.getInputService());
    assertNull(ServiceLocator.getResourceService());
    assertNull(ServiceLocator.getCurrencyService());
    assertNull(ServiceLocator.getItemEffectsService());
    // Persistent services should remain after clear
    assertNotNull(ServiceLocator.getCutsceneService());
    assertNotNull(ServiceLocator.getWorldMapService());
    assertNotNull(ServiceLocator.getDialogService());
    assertNotNull(ServiceLocator.getGlobalResourceService());
    assertNotNull(ServiceLocator.getConfigService());
    assertNotNull(ServiceLocator.getProfileService());
    assertNotNull(ServiceLocator.getSettingsService());
    assertNotNull(ServiceLocator.getDiscordRichPresenceService());
  }

  @Test
  void shouldDeregisterPersistentServices() {
    registerAllServices();

    ServiceLocator.deregisterDialogService();
    assertNull(ServiceLocator.getDialogService());

    ServiceLocator.deregisterGlobalResourceService();
    assertNull(ServiceLocator.getGlobalResourceService());

    ServiceLocator.deregisterConfigService();
    assertNull(ServiceLocator.getConfigService());

    ServiceLocator.deregisterProfileService();
    assertNull(ServiceLocator.getProfileService());

    ServiceLocator.deregisterWorldMapService();
    assertNull(ServiceLocator.getWorldMapService());

    ServiceLocator.deregisterDiscordRichPresenceService();
    assertNull(ServiceLocator.getDiscordRichPresenceService());

    ServiceLocator.deregisterCutsceneService();
    assertNull(ServiceLocator.getCutsceneService());

    ServiceLocator.deregisterSettingsService();
    assertNull(ServiceLocator.getSettingsService());
  }

  private void registerAllServices() {
    ServiceLocator.registerEntityService(entityService);
    ServiceLocator.registerRenderService(renderService);
    ServiceLocator.registerPhysicsService(physicsService);
    ServiceLocator.registerTimeSource(gameTime);
    ServiceLocator.registerInputService(inputService);
    ServiceLocator.registerResourceService(resourceService);
    ServiceLocator.registerCurrencyService(currencyService);
    ServiceLocator.registerProfileService(profileService);
    ServiceLocator.registerItemEffectsService(itemEffectsService);
    ServiceLocator.registerCutsceneService(cutsceneService);
    ServiceLocator.registerWorldMapService(worldMapService);
    ServiceLocator.registerDialogService(dialogService);
    ServiceLocator.registerConfigService(configService);
    ServiceLocator.registerGlobalResourceService(globalResourceService);
    ServiceLocator.registerSettingsService(settingsService);
    ServiceLocator.registerDiscordRichPresenceService(discordRichPresenceService);
  }

  private void assertAllServicesRegistered() {
    assertEquals(entityService, ServiceLocator.getEntityService());
    assertEquals(renderService, ServiceLocator.getRenderService());
    assertEquals(physicsService, ServiceLocator.getPhysicsService());
    assertEquals(gameTime, ServiceLocator.getTimeSource());
    assertEquals(inputService, ServiceLocator.getInputService());
    assertEquals(resourceService, ServiceLocator.getResourceService());
    assertEquals(currencyService, ServiceLocator.getCurrencyService());
    assertEquals(profileService, ServiceLocator.getProfileService());
    assertEquals(itemEffectsService, ServiceLocator.getItemEffectsService());
    assertEquals(cutsceneService, ServiceLocator.getCutsceneService());
    assertEquals(worldMapService, ServiceLocator.getWorldMapService());
    assertEquals(dialogService, ServiceLocator.getDialogService());
    assertEquals(globalResourceService, ServiceLocator.getGlobalResourceService());
    assertEquals(configService, ServiceLocator.getConfigService());
    assertEquals(discordRichPresenceService, ServiceLocator.getDiscordRichPresenceService());
    assertEquals(settingsService, ServiceLocator.getSettingsService());
  }
}
