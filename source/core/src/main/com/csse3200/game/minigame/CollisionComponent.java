package com.csse3200.game.minigame;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.components.Component;
import com.csse3200.game.entities.Entity;

public class CollisionComponent extends Component {
    private Image target;

    public CollisionComponent(Image target) {
        this.target = target;
    }

    public void update(float delta) {
        Entity owner=entity;
        if(owner==null) return;

        BallComponent ball=owner.getComponent(BallComponent.class);
        if (ball==null) return;

        Rectangle ballRect= new Rectangle(
                ball.getImage().getX(),
                ball.getImage().getY(),
                ball.getImage().getWidth(),
                ball.getImage().getHeight()
        );

        Rectangle targetRect= new Rectangle(
                target.getX(),
                target.getY(),
                target.getWidth(),
                target.getHeight()
        );
        if(ballRect.overlaps(targetRect)) {
            ball.reverseY();

            ball.getImage().setY(target.getY()+target.getHeight());
        }
    }
}
