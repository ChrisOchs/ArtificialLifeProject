package artificiallife.stateactions;

import artificiallife.Agent;

/**
 *
 * @author Chris
 */
public class DeadAction implements StateAction {
    private Agent agent;

    private int updates = 0;
    private int decomposeTime;

    public DeadAction(Agent agent) {
        this.agent = agent;
        this.decomposeTime = agent.getSize() * 50;
    }

    public void performAction() {
        updates++;

        if(agent.isActive()) {
            if(updates >= decomposeTime) {
                agent.setEntityActive(false);
            }
        }
    }
}
