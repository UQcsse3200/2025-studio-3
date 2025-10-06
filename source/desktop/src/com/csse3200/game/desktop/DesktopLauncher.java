package com.csse3200.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.csse3200.game.GdxGame;
import com.csse3200.game.persistence.DeserializedSettings;
import com.csse3200.game.persistence.FileLoader;
import java.io.File;
import net.dermetfan.utils.Pair;

/** This is the launch class for the desktop game. Passes control to libGDX to run GdxGame(). */
public class DesktopLauncher {
  public static void main(String[] arg) {
    Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
    config.setWindowIcon("app.png");
    config.setTitle("The Day We Fought Back");
    FileHandle fileHandle =
        new FileHandle(
            System.getProperty("user.home")
                + File.separator
                + "The Day We Fought Back"
                + File.separator
                + "settings.json");

    try {
      DeserializedSettings deserializedSettings =
          FileLoader.readClass(DeserializedSettings.class, fileHandle);

      switch (deserializedSettings.getCurrentMode()) {
        case WINDOWED:
          Pair<Integer, Integer> windowedRes = deserializedSettings.getWindowedResolution();
          config.setWindowedMode(windowedRes.getKey(), windowedRes.getValue());
          config.setResizable(false);
          break;
        case BORDERLESS:
          config.setResizable(true);
          config.setDecorated(false);
          config.setMaximized(true);
          break;
        default:
          config.setResizable(true);
          config.setMaximized(true);
      }
    } catch (Exception e) {
      config.setResizable(true);
      config.setMaximized(true);
    }

    new Lwjgl3Application(new GdxGame(), config);
  }
}
