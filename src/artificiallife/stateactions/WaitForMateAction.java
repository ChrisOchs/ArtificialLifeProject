package artificiallife.stateactions;

import artificiallife.Agent;

/**
 *
 * @author Chris
 */
public class WaitForMateAction implements StateAction {
    private Agent agent;

    private int updates = 0;

    public WaitForMateAction(Agent agent) {
        this.agent = agent;
    }

    public void performAction() {
        updates++;

        if(updates > 20) {
            agent.setCurrentState(new WanderAction(agent));
        }
    }
}
