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
  private static MenuSpriteService menuSpriteService;
  private static CutsceneService cutsceneService;

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
   * Gets the menu sprite service.
   *
   * @return the menu sprite service
   */
  public static MenuSpriteService getMenuSpriteService() {
    return menuSpriteService;
  }

  public static CutsceneService getCutsceneService() {
    return cutsceneService;
  }

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
   * Registers the menu sprite service.
   *
   * @param source the menu sprite service
   */
  public static void registerMenuSpriteService(MenuSpriteService source) {
    logger.debug("Registering menu sprite service {}", source);
    menuSpriteService = source;
  }

  public static void registerCutsceneService(CutsceneService source) {
    logger.debug("Registering cutscene service {}", source);
    cutsceneService = source;
  }

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
