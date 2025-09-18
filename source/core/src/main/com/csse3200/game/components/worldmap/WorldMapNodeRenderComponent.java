package com.csse3200.game.components.worldmap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.rendering.RenderComponent;
import com.csse3200.game.screens.WorldMapNode;
import com.csse3200.game.services.ServiceLocator;

/**
 * Renders a world map node using the engine's rendering system
 */
public class WorldMapNodeRenderComponent extends RenderComponent {
    private final WorldMapNode node;
    private final Vector2 worldSize;
    private final float nodeSize;
    private BitmapFont font;
    private boolean showPrompt = false;
    
    public WorldMapNodeRenderComponent(WorldMapNode node, Vector2 worldSize, float nodeSize) {
        this.node = node;
        this.worldSize = worldSize;
        this.nodeSize = nodeSize;
    }
    
    @Override
    public void create() {
        super.create();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);
    }
    
  @Override
  protected void draw(SpriteBatch batch) {
    String texturePath = getNodeTexture();
    Texture nodeTexture = ServiceLocator.getResourceService()
        .getAsset(texturePath, Texture.class);
        
        float x = node.getPositionX() * worldSize.x;
        float y = node.getPositionY() * worldSize.y;
        
        // Draw with slight enlargement if hovering/nearby
        float drawSize = nodeSize;
        float drawX = x;
        float drawY = y;
        
        if (showPrompt) {
            drawSize += 8f;
            drawX -= 4f;
            drawY -= 4f;
        }
        
        batch.draw(nodeTexture, drawX, drawY, drawSize, drawSize);
        
        // Draw prompt text if nearby
        if (showPrompt && node.isUnlocked()) {
            String prompt = "Press E to Enter";
            font.draw(batch, prompt, x, y + nodeSize + 20f);
        }
        
        // Draw lock reason if locked
        if (!node.isUnlocked() && node.getLockReason() != null) {
            font.setColor(Color.GRAY);
            font.draw(batch, node.getLockReason(), x, y - 20f);
            font.setColor(Color.WHITE);
        }
    }
    
    public void setShowPrompt(boolean showPrompt) {
        this.showPrompt = showPrompt;
    }
    
    @Override
    public int getLayer() {
        return 2; // Node layer, above background
    }
    
    private String getNodeTexture() {
        if (node.isCompleted()) {
            return "images/node_completed.png";
        } else if (node.isUnlocked()) {
            return "images/node_unlocked.png";
        } else {
            return "images/locked_level1.png";
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
        if (font != null) {
            font.dispose();
        }
    }
}
