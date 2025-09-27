package com.csse3200.game.services;

import com.csse3200.game.entities.EntityService;
import com.csse3200.game.input.InputService;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.rendering.RenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simplified implementation of the Service Locator pattern:
 * https://martinfowler.com/articles/injection.html#UsingAServiceLocator
 *
 * <p>Allows global access to a few core game services. Warning: global access is a trap and should
 * be used <i>extremely</i> sparingly. Read the wiki for details
 * (https://github.com/UQcsse3200/game-engine/wiki/Service-Locator).
 */
public class ServiceLocator {
  private static final Logger logger = LoggerFactory.getLogger(ServiceLocator.class);
  private static EntityService entityService;
  private static RenderService renderService;
  private static PhysicsService physicsService;
  private static GameTime timeSource;
  private static InputService inputService;
  private static ResourceService resourceService;
  private static CurrencyService currencyService;
  private static ConfigService configService;
  private static DialogService dialogService;
  private static ResourceService globalResourceService;
  private static ProfileService profileService;
  private static ItemEffectsService itemEffectsService;
  private static CutsceneService cutsceneService;
  private static WorldMapService worldMapService;
  private static SettingsService settingsService;
  private static DiscordRichPresenceService discordRichPresenceService;

  /**
   * Gets the entity service.
   *
   * @return the entity service
   */
  public static EntityService getEntityService() {
    return entityService;
  }

  /**
   * Gets the render service.
   *
   * @return the render service
   */
  public static RenderService getRenderService() {
    return renderService;
  }

  /**
   * Gets the physics service.
   *
   * @return the physics service
   */
  public static PhysicsService getPhysicsService() {
    return physicsService;
  }

  /**
   * Gets the time source.
   *
   * @return the time source
   */
  public static GameTime getTimeSource() {
    return timeSource;
  }

  /**
   * Gets the input service.
   *
   * @return the input service
   */
  public static InputService getInputService() {
    return inputService;
  }

  /**
   * Gets the resource service.
   *
   * @return the resource service
   */
  public static ResourceService getResourceService() {
    return resourceService;
  }

  /**
   * Gets the currency service.
   *
   * @return the currency service
   */
  public static CurrencyService getCurrencyService() {
    return currencyService;
  }

  /**
   * Gets the config service.
   *
   * @return the config service
   */
  public static ConfigService getConfigService() {
    return configService;
  }

  /**
   * Gets the dialog service.
   *
   * @return the dialog service
   */
  public static DialogService getDialogService() {
    return dialogService;
  }

  /**
   * Gets the global resource service.
   *
   * @return the global resource service
   */
  public static ResourceService getGlobalResourceService() {
    return globalResourceService;
  }

  /**
   * Gets the profile service.
   *
   * @return the profile service
   */
  public static ProfileService getProfileService() {
    return profileService;
  }

  /**
   * Gets the item effects service.
   *
   * @return the item effects service
   */
  public static ItemEffectsService getItemEffectsService() {
    return itemEffectsService;
  }

  /**
   * Gets the cutscene service.
   *
   * @return the cutscene service
   */
  public static CutsceneService getCutsceneService() {
    return cutsceneService;
  }

  /**
   * Gets the world map service.
   *
   * @return the world map service
   */
  public static WorldMapService getWorldMapService() {
    return worldMapService;
  }

  /**
   * Gets the settings service.
   *
   * @return the settings service
   */
  public static SettingsService getSettingsService() {
    return settingsService;
  }

  /**
   * Gets the Discord Rich Presence service.
   *
   * @return the Discord Rich Presence service
   */
  public static DiscordRichPresenceService getDiscordRichPresenceService() {
    return discordRichPresenceService;
  }

  /**
   * Registers the entity service.
   *
   * @param service the entity service
   */
  public static void registerEntityService(EntityService service) {
    logger.debug("Registering entity service {}", service);
    entityService = service;
  }

  /**
   * Registers the render service.
   *
   * @param service the render service
   */
  public static void registerRenderService(RenderService service) {
    logger.debug("Registering render service {}", service);
    renderService = service;
  }

  /**
   * Registers the physics service.
   *
   * @param service the physics service
   */
  public static void registerPhysicsService(PhysicsService service) {
    logger.debug("Registering physics service {}", service);
    physicsService = service;
  }

  /**
   * Registers the time source.
   *
   * @param source the time source
   */
  public static void registerTimeSource(GameTime source) {
    logger.debug("Registering time source {}", source);
    timeSource = source;
  }

  /**
   * Registers the input service.
   *
   * @param source the input service
   */
  public static void registerInputService(InputService source) {
    logger.debug("Registering input service {}", source);
    inputService = source;
  }

  /**
   * Registers the resource service.
   *
   * @param source the resource service
   */
  public static void registerResourceService(ResourceService source) {
    logger.debug("Registering resource service {}", source);
    resourceService = source;
  }

  /**
   * Registers the global resource service.
   *
   * @param source the global resource service
   */
  public static void registerGlobalResourceService(ResourceService source) {
    logger.debug("Registering global resource service {}", source);
    globalResourceService = source;
  }

  /** Deregisters the global resource service. */
  public static void deregisterGlobalResourceService() {
    logger.debug("Removing global resource service");
    globalResourceService = null;
  }

  /**
   * Registers the currency service.
   *
   * @param source the currency service
   */
  public static void registerCurrencyService(CurrencyService source) {
    logger.debug("Registering currency service {}", source);
    currencyService = source;
  }

  /**
   * Registers the config service.
   *
   * @param source the config service
   */
  public static void registerConfigService(ConfigService source) {
    logger.debug("Registering config service {}", source);
    configService = source;
  }

  /** Deregisters the config service. */
  public static void deregisterConfigService() {
    logger.debug("Removing config service");
    configService = null;
  }

  /**
   * Registers the dialog service.
   *
   * @param source the dialog service
   */
  public static void registerDialogService(DialogService source) {
    logger.debug("Registering dialog service {}", source);
    dialogService = source;
  }

  /**
   * Registers the item effects service.
   *
   * @param source the item effects service
   */
  public static void registerItemEffectsService(ItemEffectsService source) {
    logger.debug("Registering item effects service {}", source);
    itemEffectsService = source;
  }

  /** Deregisters the dialog service. */
  public static void deregisterDialogService() {
    logger.debug("Removing dialog service");
    dialogService = null;
  }

  /**
   * Registers the profile service.
   *
   * @param source the profile service
   */
  public static void registerProfileService(ProfileService source) {
    logger.debug("Registering profile service {}", source);
    profileService = source;
  }

  /** Deregisters the profile service. */
  public static void deregisterProfileService() {
    logger.debug("Removing profile service");
    profileService = null;
  }

  /** Registers cutscene service */
  public static void registerCutsceneService(CutsceneService source) {
    logger.debug("Registering cutscene service {}", source);
    cutsceneService = source;
  }

  /**
   * Registers the world map service.
   *
   * @param source the world map service
   */
  public static void registerWorldMapService(WorldMapService source) {
    logger.debug("Registering world map service {}", source);
    worldMapService = source;
  }

  /** Deregisters the world map service. */
  public static void deregisterWorldMapService() {
    logger.debug("Removing world map service");
    worldMapService = null;
  }

  /**
   * Registers the settings service.
   *
   * @param source the settings service
   */
  public static void registerSettingsService(SettingsService source) {
    logger.debug("Registering settings service {}", source);
    settingsService = source;
  }

  /** Deregisters the settings service. */
  public static void deregisterSettingsService() {
    logger.debug("Removing settings service");
    settingsService = null;
  }

  /**
   * Registers the Discord Rich Presence service.
   *
   * @param source the Discord Rich Presence service
   */
  public static void registerDiscordRichPresenceService(DiscordRichPresenceService source) {
    logger.debug("Registering Discord Rich Presence service {}", source);
    discordRichPresenceService = source;
  }

  /** Deregisters the Discord Rich Presence service. */
  public static void deregisterDiscordRichPresenceService() {
    logger.debug("Removing Discord Rich Presence service");
    if (discordRichPresenceService != null) {
      discordRichPresenceService.shutdown();
    }
    discordRichPresenceService = null;
  }

  /** Clears all transient services. */
  public static void clear() {
    entityService = null;
    renderService = null;
    physicsService = null;
    timeSource = null;
    inputService = null;
    resourceService = null;
    currencyService = null;
    itemEffectsService = null;
  }

  /** Private constructor to prevent instantiation. */
  private ServiceLocator() {
    throw new IllegalStateException("Instantiating static util class");
  }
}
