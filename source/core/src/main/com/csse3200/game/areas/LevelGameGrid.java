package com.csse3200.game.areas;

import com.csse3200.game.entities.Entity;

public class LevelGameGrid {
    private Entity[] grid_data;
    private final int rows;
    private final int cols;
    private final int num_tiles;

    public LevelGameGrid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.num_tiles = rows * cols;
        this.grid_data = new Entity[num_tiles];
    }

    public void addTile(int index, Entity tile) {grid_data[index] = tile;}

    public Entity getTile(int row, int col) {
        return grid_data[col * cols + row];
    }

    public void setTile(int row, int col, Entity tile) {
        grid_data[col * cols + row] = tile;
    }
}

