package com.csse3200.game.services;

import static org.junit.jupiter.api.Assertions.*;
import com.csse3200.game.entities.configs.BaseItemConfig;
import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(GameExtension.class)
class ConfigServiceTest {
  private static final Logger logger = LoggerFactory.getLogger(ConfigServiceTest.class);
  private ConfigService configService;

  @Test
  void testLoadConfigsValidConfigFile() {
    configService = new ConfigService();
    Map<String, BaseItemConfig> itemConfigs = configService.loadConfigs("test/files/ExampleItems.json");
    logger.info("Item configs: {}", itemConfigs);
    assertNotNull(itemConfigs);
    assertEquals(2, itemConfigs.size());
    BaseItemConfig itemConfig = configService.getItemConfig("item1");
    assertEquals("Item 1", itemConfig.getName());
    assertEquals("Item 1 description", itemConfig.getDescription());
    assertEquals("item1", itemConfig.getEventName());
    assertEquals(30, itemConfig.getCost());
    itemConfig = configService.getItemConfig("item2");
    assertEquals("Item 2", itemConfig.getName());
    assertEquals("Item 2 description", itemConfig.getDescription());
    assertEquals("item2", itemConfig.getEventName());
    assertEquals(30, itemConfig.getCost());
  }

  @Test
  void testLoadConfigsMissingConfigFile() {
    configService = new ConfigService();
    Map<String, BaseItemConfig> itemConfigs = configService.loadConfigs("test/files/missing.json");
    assertNotNull(itemConfigs);
    assertEquals(0, itemConfigs.size());
  }

  @Test
  void testLoadConfigsInvalidConfigFile() {
    configService = new ConfigService();
    Map<String, BaseItemConfig> itemConfigs = configService.loadConfigs("test/files/invalid.json");
    assertNotNull(itemConfigs);
    assertEquals(0, itemConfigs.size());
  }
}
