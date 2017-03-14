package artificiallife.stateactions;

import artificiallife.Agent;
import artificiallife.SimulationInformation;

/**
 *
 * @author Chris
 */
public class MateAction implements StateAction {
    private Agent agent;
    private Agent partner;

    public MateAction(Agent agent, Agent partner) {
        this.agent = agent;
        this.partner = partner;

        partner.setCurrentState(new WaitForMateAction(partner));
        agent.setDestination(partner.getPosition().x.intValue(), partner.getPosition().y.intValue());
    }

    public void performAction() {
        agent.doMove(agent.getSpeed() / 2);
        agent.modifyEnergy(-((agent.getSpeed() / 2) * agent.getSize()) / 2);

        if(partner.getGender().equals(Agent.Gender.Female) && partner.isPregnant()) {
            agent.setCurrentState(new WanderAction(agent));
        }
        
        if(!agent.isMoving() && (partner.getCurrentState() instanceof WaitForMateAction)) {
            
            if(agent.getGender().equals(Agent.Gender.Female)) {
                if(!agent.isPregnant()) {
                    agent.createChildrenWith(partner);
                    SimulationInformation.getSimulationInformation().getAgentStatistics(partner).children++;
                }
            } else {
                if(!partner.isPregnant()) {
                    partner.createChildrenWith(agent);
                    SimulationInformation.getSimulationInformation().getAgentStatistics(agent).children++;
                }
            }

            agent.setCurrentState(new WanderAction(agent));
            partner.setCurrentState(new WanderAction(partner));
        }
    }
}
