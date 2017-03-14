package artificiallife.stateactions;

import artificiallife.Agent;

/**
 *
 * @author Chris
 */
public class StunnedAction implements StateAction {
    private StateAction nextState;
    private Agent agent;
    private int stunTime;
    private int updates = 0;

    public StunnedAction(Agent agent, StateAction nextState, int stunTime) {
        this.agent = agent;
        this.nextState = nextState;
        this.stunTime = stunTime;
    }

    public void performAction() {
        if(updates < stunTime) {
            updates++;
            return;
        }

        agent.setCurrentState(nextState);
    }
}
