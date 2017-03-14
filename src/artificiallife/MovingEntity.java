
package artificiallife;

import java.awt.Rectangle;

/**
 *
 * @author Chris
 */
public abstract class MovingEntity extends Entity {
    private Vector2<Integer> destination;

    private Vector2<Double> direction;

    private Rectangle worldBounds;

    protected boolean isMoving = false;

    public MovingEntity(int x, int y, Rectangle worldBounds) {
        super(x, y);

        this.worldBounds = worldBounds;
    }

    public Vector2<Integer> getDestination() {
        return destination;
    }

    public void setDestination(int x, int y) {
        if(x < 0) {
            x = 0;
        } else if(x > worldBounds.width) {
            x = worldBounds.width;
        }

        if(y < 0) {
            y = 0;
        } else if(y > worldBounds.height) {
            y = worldBounds.height;
        }

        destination = new Vector2<Integer>(x, y);
        direction = null;
        isMoving = true;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void stopMoving() {
        isMoving = false;
    }

    public void doMove(int speed) {

        if(direction == null) {
            double deltaX = -(this.position.x - destination.x);
            double deltaY = -(this.position.y - destination.y);

            double magnitude = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            direction = new Vector2<Double>(deltaX / magnitude, deltaY / magnitude);
        }
        
        if(!isMoving) {
            return;
        }

        double xToDest = this.position.x - destination.x;
        double yToDest = this.position.y - destination.y;
        double distanceToDestination = Math.sqrt(xToDest * xToDest + yToDest * yToDest);

        if(distanceToDestination > 16) {
            this.position.x += direction.x * speed;
            this.position.y += direction.y * speed;

            if(this.position.x < 0) {
                this.position.x = 0.0;
                this.direction.x = - this.direction.x;
            } else if (this.position.x > worldBounds.width) {
                this.position.x = (double)worldBounds.width;
                this.direction.x = - this.direction.x;
            }

            if(this.position.y < 0) {
                this.position.y = 0.0;
                this.direction.y = - this.direction.y;
            } else if (this.position.y > worldBounds.height) {
                this.position.y = (double)worldBounds.height;
                this.direction.y = - this.direction.y;
            }
        } else {
            stopMoving();
        }
    }
}
