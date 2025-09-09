package com.csse3200.game.cutscene.models.object;

import java.util.List;

/**
 * Stores cutscene information.
 */
public class Cutscene {
    private String id;
    private List<Character> characters;
    private List<Background> backgrounds;
    private List<Sound> sounds;
    private List<Beat> beats;

    /**
     * Creates a {@code Cutscene} object with specified id, characters, backgrounds, sounds, and beats
     * @param id           The ID of the cutscene (the file name)
     * @param characters   A list of {@link Character}s that are used in the cutscene
     * @param backgrounds  A list of {@link Background}s that are used in the cutscene
     * @param sounds       A list of {@link Sound}s that are used in the cutscene
     * @param beats        A list of {@link Beat}s that are in the cutscene
     */
    public Cutscene(String id, List<Character> characters, List<Background> backgrounds, List<Sound> sounds,
                    List<Beat> beats) {
        this.id = id;
        this.characters = characters;
        this.backgrounds = backgrounds;
        this.sounds = sounds;
        this.beats = beats;
    }

    public String getId() {
        return id;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public List<Background> getBackgrounds() {
        return backgrounds;
    }

    public List<Sound> getSounds() {
        return sounds;
    }

    public List<Beat> getBeats() {
        return beats;
    }
}
