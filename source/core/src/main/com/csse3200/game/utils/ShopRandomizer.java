package com.csse3200.game.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

/**
 * A utility class for randomizing the shop items. Takes the profile name as a seed, and based on
 * the current time, it will return 3 random indexes for the shop items. This means the items will
 * be different for each profile, but the same for each hour.
 */
public class ShopRandomizer {
  /** Creates a new ShopRandomizer. */
  private ShopRandomizer() {
    throw new IllegalStateException("Instantiating static util class");
  }

  /**
   * Gets the indexes of the shop items.
   *
   * @param seed the seed for the random number generator
   * @param min the minimum value for the random number generator
   * @param max the maximum value for the random number generator
   * @param dateTime the date and time to use for the random number generator
   * @return the indexes of the shop items
   */
  public static int[] getShopItemIndexes(String seed, int min, int max, LocalDateTime dateTime) {
    // Round down to the nearest 15-minute interval
    int currentMinute = dateTime.getMinute();
    int intervalMinute = (currentMinute / 15) * 15;
    LocalDateTime intervalDateTime = dateTime.withMinute(intervalMinute).withSecond(0).withNano(0);

    // Combine seed with the interval timestamp
    String combinedSeed = seed + "-" + intervalDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();

    // Hash the combined seed into an int
    int hash = combinedSeed.hashCode();

    // Use as seed for Random
    Random random = new Random(hash);

    // Generate 3 unique random numbers in [min, max]
    // If the range has fewer than 3 values, allow duplicates
    int[] results = new int[3];
    int rangeSize = max - min + 1;

    if (rangeSize < 3) {
      // Range too small for 3 unique values, allow duplicates
      for (int i = 0; i < 3; i++) {
        results[i] = random.nextInt(rangeSize) + min;
      }
    } else {
      // Generate 3 unique values
      boolean[] used = new boolean[rangeSize];
      for (int i = 0; i < 3; i++) {
        int randomIndex;
        do {
          randomIndex = random.nextInt(rangeSize) + min;
        } while (used[randomIndex - min]);

        used[randomIndex - min] = true;
        results[i] = randomIndex;
      }
    }

    return results;
  }
}
