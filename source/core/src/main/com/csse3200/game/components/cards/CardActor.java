package com.csse3200.game.components.cards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;

/**
 * 可拖拽的“空卡牌”Actor。
 * - 自适应屏幕短边尺寸（默认 14%）
 * - 可响应拖拽，松手时触发 CardPlacedEvent
 */
public class CardActor extends Image {

    public CardActor(TextureRegion region) {
        super(region);
        setOrigin(Align.center);
        setTouchable(Touchable.enabled);
        getColor().a = 0f; // 初始透明，方便做淡入动画

        // 自适应尺寸
        float shortEdge = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float targetW = shortEdge * 0.14f;
        float scale = targetW / getDrawable().getMinWidth();
        float targetH = getDrawable().getMinHeight() * scale;
        setSize(targetW, targetH);

        addDrag();
    }

    private void addDrag() {
        addListener(new DragListener() {
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                // 让卡片跟随鼠标/手指移动
                moveBy(x - getWidth() / 2f, y - getHeight() / 2f);
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                // 松手时广播一个事件，外部监听后决定是否放置成功
                if (getStage() != null) {
                    getStage().getRoot().fire(new CardPlacedEvent(getX(Align.center), getY(Align.center)));
                }
            }
        });
    }

    /** 便捷构造：直接从 png 路径载入 */
    public static CardActor fromPng(String internalPath) {
        Texture tex = new Texture(Gdx.files.internal(internalPath));
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return new CardActor(new TextureRegion(tex));
    }

    /** 自定义事件：卡牌松手 */
    public static class CardPlacedEvent extends com.badlogic.gdx.scenes.scene2d.Event {
        public final float worldX, worldY;
        public CardPlacedEvent(float worldX, float worldY) {
            this.worldX = worldX;
            this.worldY = worldY;
        }
    }
}
