package artificiallife;

import artificiallife.stateactions.DeadAction;
import artificiallife.stateactions.GetFoodAction;
import artificiallife.stateactions.HuntAction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Chris
 */
public class Carnivore extends Agent {

    public Carnivore(World world, int x, int y, int size, int speed, int naturalLifeSpan, int sightRange, int sightRadius, int smellRange,
            double maturityPercentage, int maxEnergy, int gestationPeriod, int maxChildren, Gender gender) {

        super(world, x, y, size, speed, naturalLifeSpan, sightRange, sightRadius,
                smellRange, maturityPercentage, maxEnergy, gestationPeriod, maxChildren, gender);
    }

    public void update() {

        if(this.isDead() && (currentState instanceof DeadAction)) {
            currentState.performAction();
            return;
        }

        super.update();

        if (!(currentState instanceof GetFoodAction)) {
            if (this.currentEnergy < (this.maxEnergy / 2)) {
                List<Agent> agentsInWorld = world.getAgents();

                ArrayList<Agent> herbivores = new ArrayList<Agent>();

                for(Agent a : agentsInWorld) {
                    if(a instanceof Herbivore) {
                        herbivores.add(a);
                    }
                }

                if (!herbivores.isEmpty()) {
                    Agent closestPrey = herbivores.get(0);

                    double deltaX = this.position.x - closestPrey.position.x;
                    double deltaY = this.position.y - closestPrey.position.y;

                    double smallestDistance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                    for (int c = 1; c < herbivores.size(); c++) {

                        Agent prey = herbivores.get(c);

                        deltaX = this.position.x - prey.position.x;
                        deltaY = this.position.y - prey.position.y;

                        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                        if (distance < smallestDistance) {
                            closestPrey = herbivores.get(c);
                            smallestDistance = distance;
                        }
                    }

                    if (smallestDistance <= this.getSmellRange() || smallestDistance <= this.getSightRange()) {
                        this.setCurrentState(new HuntAction(this, closestPrey));
                    }
                }
            }
        }

        currentState.performAction();
    }
}
