package artificiallife.stateactions;

import artificiallife.Agent;
import artificiallife.SimulationInformation;

/**
 *
 * @author Chris
 */
public class HuntAction implements StateAction {
    private Agent agent;
    private Agent target;

    public HuntAction(Agent agent, Agent target) {
        this.agent = agent;
        this.target = target;
    }

    public void performAction() {
        if(target.isDead()) {
            agent.setCurrentState(new WanderAction(agent));
            return;
        }

        agent.setDestination(target.getPosition().x.intValue(), target.getPosition().y.intValue());
        agent.doMove(agent.getSpeed());
        agent.modifyEnergy(-(agent.getSpeed() * agent.getSize()) / 2);

        if(!agent.isMoving()) {
            double sizeDifference = target.getSize() / agent.getSize();

            sizeDifference = Math.abs(sizeDifference);

            boolean doKill = false;

            if (sizeDifference <= 1) {
                doKill = true;
            } else {
                if(Math.random() > sizeDifference) {
                    doKill = true;
                } else {
                    agent.modifyEnergy((int)(-agent.getCurrentEnergy() / sizeDifference));
                    agent.setCurrentState(new StunnedAction(agent, this, 30));
                    return;
                }
            }

            if (doKill) {
                if(!target.isDead()) {
                    target.kill();
                    target.modifyEnergy(0);
                    target.modifyEnergy(target.getSize() * 100);
                }

                int requiredEnergy = agent.getMaxEnergy() - agent.getCurrentEnergy();
                int availableEnergy = target.getCurrentEnergy();

                if (availableEnergy < requiredEnergy) {
                    requiredEnergy = availableEnergy;
                }

                target.modifyEnergy(-requiredEnergy);
                agent.modifyEnergy(requiredEnergy);
                
                SimulationInformation.getSimulationInformation().getAgentStatistics(agent).consumed++;
                SimulationInformation.getSimulationInformation().getAgentStatistics(target).status = "Killed";
            }
        }
    }
}
