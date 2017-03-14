package artificiallife;

/**
 *
 * @author Chris
 */
public abstract class Entity {
    protected Vector2<Double> position;

    protected boolean isActive = true;

    public Entity(int x, int y) {
        position = new Vector2<Double>((double)x, (double)y);
    }

    public Vector2<Double> getPosition() {
        return position;
    }
    
    public boolean isActive() {
        return isActive;
    }

    public void setEntityActive(boolean value) {
        this.isActive = value;
    }
}
