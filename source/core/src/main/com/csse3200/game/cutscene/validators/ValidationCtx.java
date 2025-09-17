package com.csse3200.game.cutscene.validators;

import java.util.Map;
import java.util.Set;

/**
 * Stores important cutscene data to be passed into {@link ActionValidator}s
 *
 * @param characterIds A list of {@link String} character IDs
 * @param backgroundIds A list of {@link String} background IDs
 * @param soundIds A list of {@link String} sound IDs
 * @param beatIds A list of {@link String} beat IDs
 * @param characterPoses A map of character id's to a {@link Set<String>} of their pose IDs
 */
public record ValidationCtx(
    Set<String> characterIds,
    Set<String> backgroundIds,
    Set<String> soundIds,
    Set<String> beatIds,
    Map<String, Set<String>> characterPoses) {
  public ValidationCtx {
    characterIds = Set.copyOf(characterIds);
    backgroundIds = Set.copyOf(backgroundIds);
    soundIds = Set.copyOf(soundIds);
    beatIds = Set.copyOf(beatIds);
    characterPoses = Map.copyOf(characterPoses);
  }
}
