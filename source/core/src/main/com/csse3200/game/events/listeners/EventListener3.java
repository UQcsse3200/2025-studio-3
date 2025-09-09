package com.csse3200.game.events.listeners;

/**
 * An event listener with 3 arguments
 *
 * @param <T0> The type of the first event argument
 * @param <T1> The type of the second event argument
 * @param <T2> The type of the third event argument
 */
@FunctionalInterface
public interface EventListener3<T0, T1, T2> extends EventListener {
  void handle(T0 arg0, T1 arg1, T2 arg2);
}
