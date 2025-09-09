package com.csse3200.game.cutscene.models.object;

/**
 * Stores data for background.
 * (id and image)
 */
public class Background {
    private String id;
    private String image;

    /**
     * Creates a {@code Background} object with specified id and image.
     *
     * @param id     The id of the background
     * @param image  The image address of the background
     */
    public Background(String id, String image) {
        this.id = id;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }
}
