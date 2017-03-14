package artificiallife;

/**
 *
 * @author Chris
 */
public class Food extends Entity {

    private int amount;

    public Food(int x, int y, int amount) {
        super(x, y);

        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public int doEat(int amountEaten) {

        if(amountEaten >= amount) {
            amountEaten = amount;
            this.isActive = false;
        }

        this.amount -= amountEaten;

        return amountEaten;
    }
}
