package artificiallife;

import artificiallife.stateactions.DeadAction;
import artificiallife.stateactions.EvadeAction;
import artificiallife.stateactions.GetFoodAction;
import artificiallife.stateactions.WanderAction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Chris
 */
public class Herbivore extends Agent {
    
    public Herbivore(World world, int x, int y, int size, int speed, int naturalLifeSpan, int sightRange, int sightRadius, int smellRange,
            double maturityPercentage, int maxEnergy, int gestationPeriod, int maxChildren, Gender gender) {

        super(world, x, y, size, speed, naturalLifeSpan, sightRange, sightRadius, smellRange,
                maturityPercentage, maxEnergy, gestationPeriod, maxChildren, gender);
    }

    public void update() {
        if(this.isDead() && (currentState instanceof DeadAction)) {
            currentState.performAction();
            return;
        }

        super.update();

        List<Agent> agentsInWorld = world.getAgents();

        ArrayList<Agent> threatsSeen = new ArrayList<Agent>();

        boolean smellThreat = false;

        for(int c = agentsInWorld.size() - 1; c >= 0; c--) {
            Agent agent = agentsInWorld.get(c);
            
            if(agent instanceof Carnivore) {
                Vector2<Double> threatPosition = agent.getPosition();

                double deltaX = threatPosition.x - this.position.x;
                double deltaY = threatPosition.y - this.position.y;

                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                if(distance < this.getSightRange()) {

                    if((agent.getSize() > this.getSize()) ||
                            (this.getSize() > agent.getSize() / 2) && distance < this.getSightRange() / 2) {
                        threatsSeen.add(agent);
                    }
                }

                if(distance < this.getSmellRange()) {
                    smellThreat = true;
                }
            }
        }

        if(!threatsSeen.isEmpty()) {
            Vector2<Double> commonThreatDirection = new Vector2<Double>(0.0, 0.0);

            for(Agent threat : threatsSeen) {
                Vector2<Double> vector = new Vector2<Double>(threat.position.x - this.position.x,
                        threat.position.y - this.position.y);

                commonThreatDirection.x = (commonThreatDirection.x + vector.x) / 2;
                commonThreatDirection.y = (commonThreatDirection.y + vector.y) / 2;
            }

            commonThreatDirection.x = -commonThreatDirection.x;
            commonThreatDirection.y = -commonThreatDirection.y;

            this.setCurrentState(new EvadeAction(this, commonThreatDirection));
        } else if (smellThreat) {
            if(!(this.currentState instanceof WanderAction)) {
                this.setCurrentState(new WanderAction(this));
            }
        }

        if (!(currentState instanceof GetFoodAction) && !(currentState instanceof EvadeAction))  {
            if (this.currentEnergy < (this.maxEnergy / 2)) {
                ArrayList<Food> foodInWorld = world.getFood();

                Food smallestFood = foodInWorld.get(0);

                double deltaX = this.position.x - smallestFood.position.x;
                double deltaY = this.position.y - smallestFood.position.y;

                double smallestDistance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                for(int c = 1; c < foodInWorld.size(); c++) {
                    
                    Food food = foodInWorld.get(c);

                    deltaX = this.position.x - food.position.x;
                    deltaY = this.position.y - food.position.y;

                    double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                    if(distance < smallestDistance) {
                        smallestFood = foodInWorld.get(c);
                        smallestDistance = distance;
                    }
                }

                if(smallestDistance <= this.getSmellRange() || smallestDistance <= this.getSightRange()) {
                    this.setCurrentState(new GetFoodAction(this, smallestFood));
                }
            }
        }

        currentState.performAction();
    }
}
