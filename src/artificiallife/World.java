package artificiallife;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Chris
 */
public class World {

    private int updates = 0;

    private Rectangle boundingRect;

    private ArrayList<Food> foodInWorld = new ArrayList<Food>();

    private List<Agent> agentsInWorld = Collections.synchronizedList(new ArrayList<Agent>());

    private boolean running = false;

    public World(int width, int height) {
        boundingRect = new Rectangle(0, 0, width, height);
        
        SimulationSettings.getSettings().setWorldSize(width, height);

        this.reset();
    }

    public void update() {

        if(!running) {
            return;
        }

        updates++;

        for(int c = foodInWorld.size() - 1; c >= 0; c--) {
            if(!foodInWorld.get(c).isActive()) {
                foodInWorld.remove(c);
            }
        }

        for(int c = agentsInWorld.size() - 1; c >= 0; c--) {
            if(agentsInWorld.get(c).isDead()) {
                agentsInWorld.remove(c);
            }
        }

        boolean predatorExists = false;
        boolean preyExists = false;

        for(int c = agentsInWorld.size() - 1; c >= 0; c--) {
            Agent a = agentsInWorld.get(c);
            
            if(a instanceof Carnivore) {
                Carnivore predator = (Carnivore)a;
                
                predator.update();
                predatorExists = true;
            } else {
                Herbivore h = (Herbivore)a;

                h.update();
                preyExists = true;
            }
        }

        if( (updates > 500 && !predatorExists) || (!preyExists) ) {
            this.running = false;
            return;
        }

        if(updates % 10 == 0) {
            if(Math.random() < 0.7) {
                foodInWorld.add(new Food((int)(Math.random() * boundingRect.width), (int)(Math.random() * boundingRect.height),
                    (int)(Math.random() * 900) + 100));
            }
        }
    }

    public ArrayList<Food> getFood() {
        return foodInWorld;
    }

    public void setRunning(boolean value) {
        this.running = value;
    }

    public void addNewAgent(Agent agent) {
        agentsInWorld.add(agent);
    }

    public List<Agent> getAgents() {
        return agentsInWorld;
    }

    public Rectangle getBounds() {
        return boundingRect;
    }

    public final void reset() {
        this.updates = 0;

        this.agentsInWorld.clear();
        this.foodInWorld.clear();

        for(int c = 0; c < 1000; c++) {
            foodInWorld.add(new Food((int)(Math.random() * boundingRect.width), (int)(Math.random() * boundingRect.height),
                    (int)(Math.random() * 2000) + 500));
        }
    }

    public int getUpdates() {
        return updates;
    }
    
    public boolean isRunning() {
        return running;
    }
}
