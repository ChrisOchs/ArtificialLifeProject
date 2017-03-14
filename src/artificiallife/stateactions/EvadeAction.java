package artificiallife.stateactions;

import artificiallife.Agent;
import artificiallife.Vector2;

/**
 *
 * @author Chris
 */
public class EvadeAction implements StateAction {
    
    private Agent agent;
    private Vector2<Double> direction;

    public EvadeAction(Agent agent, Vector2<Double> direction) {
        this.agent = agent;

        double alter = Math.random();

        if(alter < 0.25) {
            this.direction = new Vector2<Double>(-direction.y, direction.x);
        } else if(alter < 0.5) {
            this.direction = new Vector2<Double>(direction.y, -direction.x);
        } else if(alter < 0.75) {
            this.direction = new Vector2<Double>(direction.y, direction.x);
        }
        
        this.direction = direction;
    }

    public void performAction() {
        agent.setDestination((int)(agent.getPosition().x + direction.x * 50), (int)(agent.getPosition().y + direction.y * 50));
        agent.doMove(agent.getSpeed());
        agent.modifyEnergy(-(agent.getSpeed() * agent.getSize()) / 2);
    }
}
