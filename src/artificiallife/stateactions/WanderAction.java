package artificiallife.stateactions;

import artificiallife.Agent;
import artificiallife.SimulationSettings;
import artificiallife.Vector2;

/**
 *
 * @author Chris
 */
public class WanderAction implements StateAction {
    private Agent agent;

    private int updates = 0;

    public WanderAction(Agent agent) {
        this.agent = agent;
    }

    public void performAction() {

        if(updates % (Math.random() * 1000) == 0) {
            setRandomDestination();
        }

        updates++;

        agent.doMove(agent.getSpeed() / 2);
        agent.modifyEnergy(-((agent.getSpeed() / 2) * agent.getSize()) / 2);

        if(!agent.isMoving()) {
            setRandomDestination();
        }
    }

    private void setRandomDestination() {
        Vector2<Integer> worldSize = SimulationSettings.getSettings().getWorldSize();
        int destinationX = (int) (Math.random() * worldSize.x);
        int destinationY = (int) (Math.random() * worldSize.y);

        agent.setDestination(destinationX, destinationY);
    }
}
