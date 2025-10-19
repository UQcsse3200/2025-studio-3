package com.csse3200.game.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.extensions.GameExtension;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class EnvironmentUtilsTest {

  @Test
  void testGetOperatingSystem() {
    String os = EnvironmentUtils.getOperatingSystem();
    assertNotNull(os);
    assertFalse(os.isEmpty());
    assertEquals(System.getProperty("os.name"), os);
  }

  @Test
  void testInstantiationThrows() throws NoSuchMethodException {
    Constructor<EnvironmentUtils> constructor = EnvironmentUtils.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    InvocationTargetException exception =
        assertThrows(InvocationTargetException.class, constructor::newInstance);
    assertTrue(exception.getCause() instanceof IllegalStateException);
  }
}
