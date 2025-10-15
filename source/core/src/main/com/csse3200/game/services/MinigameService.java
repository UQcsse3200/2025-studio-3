package com.csse3200.game.services;

/**
 * Service for managing minigame data.
 */
public class MinigameService {
  private int score;
  private boolean gameOver;
  private boolean paused;
  private float finalTime;
  
  /**
   * Creates a new MinigameService.
   */
  public MinigameService() {
    this.score = 0;
    this.gameOver = false;
    this.paused = false;
  }

  /**
   * Gets the score.
   * 
   * @return the score
   */
  public int getScore() {
    return score;
  }
  
  /**
   * Checks if the game is over.
   * 
   * @return true if the game is over, false otherwise
   */
  public boolean isGameOver() {
    return gameOver;
  }

  /**
   * Sets the score.
   * 
   * @param score the score
   */
  public void setScore(int score) {
    this.score = score;
  }
  
  /**
   * Sets the game over state.
   * 
   * @param gameOver the game over state
   */
  public void setGameOver(boolean gameOver) {
    this.gameOver = gameOver;
    if (gameOver) {
      // Store the final time when game ends
      this.finalTime = com.csse3200.game.services.ServiceLocator.getTimeSource().getTime();
    }
  }

  /**
   * Checks if the game is paused.
   * 
   * @return true if the game is paused, false otherwise
   */
  public boolean isPaused() {
    return paused;
  }

  /**
   * Sets the paused state.
   * 
   * @param paused the paused state
   */
  public void setPaused(boolean paused) {
    this.paused = paused;
  }

  /**
   * Gets the final time when the game ended.
   * 
   * @return the final time in milliseconds
   */
  public float getFinalTime() {
    return finalTime;
  }

  /**
   * Resets the minigame service.
   */
  public void reset() {
    this.score = 0;
    this.gameOver = false;
    this.paused = false;
    this.finalTime = 0f;
  }
}
