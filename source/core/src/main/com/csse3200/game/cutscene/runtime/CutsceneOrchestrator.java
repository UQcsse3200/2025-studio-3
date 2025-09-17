package com.csse3200.game.cutscene.runtime;

import com.csse3200.game.cutscene.models.object.Cutscene;

/** Responsible for managing the state of a cutscene. */
public interface CutsceneOrchestrator {
  /**
   * Loads a cutscene from a {@link Cutscene} object
   *
   * @param cutscene The cutscene to load
   */
  void load(Cutscene cutscene);

  /**
   * Updates the state of the cutscene by delta time (dt)
   *
   * @param dt Delta time between game frames
   */
  void update(float dt);

  /** Key or click to advance */
  void advance();

  /**
   * Selects a choice (from choice events)
   *
   * @param id The id of the choice made
   */
  void choose(String id);

  /**
   * Get the current state of the orchestrator
   *
   * @return The current state of the orchestrator
   */
  OrchestratorState state();

  /**
   * Check if the orchestrator is running
   *
   * @return true if paused, false if running
   */
  boolean paused();

  /**
   * Returns the running state of the orchestrator (to dispose)
   *
   * @return true if running, false otherwise
   */
  boolean isRuning();

  /**
   * Set the pause state of the orchestrator
   *
   * @param pause True to pause execution, false to continue execution
   */
  void setPause(boolean pause);

  /** Permanently stops execution, required load to restart cutscene */
  void stop();
}
