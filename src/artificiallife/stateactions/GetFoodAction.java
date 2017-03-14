package artificiallife.stateactions;

import artificiallife.Agent;
import artificiallife.Food;
import artificiallife.SimulationInformation;

/**
 *
 * @author Chris
 */
public class GetFoodAction implements StateAction {
    private Agent agent;
    private Food food;

    public GetFoodAction(Agent agent, Food food) {
        this.agent = agent;
        this.food = food;

        agent.setDestination(food.getPosition().x.intValue(), food.getPosition().y.intValue());
    }

    public void performAction() {
        if(!food.isActive()) {
            agent.setCurrentState(new WanderAction(agent));
            return;
        }

        agent.doMove(agent.getSpeed() / 2);
        agent.modifyEnergy(-((agent.getSpeed() / 2) * agent.getSize()) / 2);

        if(!agent.isMoving()) {
            int neededAmount = agent.getMaxEnergy() - agent.getCurrentEnergy();
            int availableAmount = food.getAmount();

            if(neededAmount > availableAmount) {
                food.doEat(availableAmount);
                agent.modifyEnergy(availableAmount);
            } else {
                food.doEat(neededAmount);
                agent.modifyEnergy(neededAmount);
            }

            SimulationInformation.getSimulationInformation().getAgentStatistics(agent).consumed++;

            agent.setCurrentState(new WanderAction(agent));
        }
    }
}
