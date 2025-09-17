package com.csse3200.game.components.cards;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

/** 可拖拽卡牌：尺寸/位置用“百分比”记忆，窗口变化时自适应缩放但相对位置不变。 */
public class CardActor extends Image {
  private final Stage stage;
  private float percentW = 0.12f; // 卡牌宽度占屏幕宽度比例（默认不大）
  private float posPx = 0.50f; // 中心点相对屏宽的比例
  private float posPy = 0.25f; // 中心点相对屏高的比例
  private int lastW = -1, lastH = -1;

  /** 用 TextureRegion 构造（一般从 atlas region 来）。 */
  public CardActor(Stage stage, TextureRegion region) {
    super(new TextureRegionDrawable(region));
    this.stage = stage;
    setOrigin(Align.center);
    setTouchable(Touchable.enabled);
    addDrag();
    applyLayout(); // 初次布局（按百分比）
  }

  /** 从 atlas 的 region 创建（推荐）。 */
  public static CardActor fromAtlas(Stage stage, TextureAtlas atlas, String regionName) {
    TextureRegion region = atlas.findRegion(regionName);
    if (region == null) throw new IllegalArgumentException("Region not found: " + regionName);
    return new CardActor(stage, region);
  }

  /** 调整宽度占比（0.05~0.25 更安全）。 */
  public void setPercentWidth(float p) {
    this.percentW = Math.max(0.05f, Math.min(0.25f, p));
    applyLayout();
  }

  /** 设置中心点的相对位置（0~1）。 */
  public void setPercentPosition(float px, float py) {
    this.posPx = px;
    this.posPy = py;
    applyLayout();
  }

  private void addDrag() {
    addListener(
        new DragListener() {
          float grabX, grabY;

          @Override
          public void dragStart(InputEvent event, float x, float y, int pointer) {
            toFront();
            grabX = x;
            grabY = y;
          }

          @Override
          public void drag(InputEvent event, float x, float y, int pointer) {
            // 将本地坐标位移换算为父容器上的绝对位置
            float nx = getX() + (x - grabX);
            float ny = getY() + (y - grabY);
            setPosition(nx, ny);
            // 同步更新“相对位置百分比”（记忆中心点）
            posPx = (nx + getWidth() / 2f) / stage.getWidth();
            posPy = (ny + getHeight() / 2f) / stage.getHeight();
          }
        });
  }

  /** 每帧检查舞台尺寸变化，变化则重算尺寸与位置。 */
  @Override
  public void act(float delta) {
    super.act(delta);
    int sw = (int) stage.getWidth(), sh = (int) stage.getHeight();
    if (sw != lastW || sh != lastH) applyLayout();
  }

  /** 按“百分比”计算尺寸与位置（中心点）。 */
  private void applyLayout() {
    int sw = (int) stage.getWidth(), sh = (int) stage.getHeight();
    if (sw <= 0 || sh <= 0 || getDrawable() == null) return;

    float targetW = sw * percentW;
    float aspect = getDrawable().getMinHeight() / getDrawable().getMinWidth();
    float targetH = targetW * aspect;
    setSize(targetW, targetH);

    float cx = sw * posPx, cy = sh * posPy;
    setPosition(cx - targetW / 2f, cy - targetH / 2f);

    lastW = sw;
    lastH = sh;
  }
}
