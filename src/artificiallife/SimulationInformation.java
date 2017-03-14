package artificiallife;

import java.util.HashMap;

/**
 *
 * @author Chris
 */
public class SimulationInformation {

    private static SimulationInformation si;

    public static SimulationInformation getSimulationInformation() {
        if(si == null) {
            si = new SimulationInformation();
        }
        
        return si;
    }

    private HashMap<Agent, Statistics> simulationStats = new HashMap<Agent, Statistics>();

    private SimulationInformation() {
        
    }

    public void registerAgent(Agent agent) {
        simulationStats.put(agent, new Statistics());
    }

    public Statistics getAgentStatistics(Agent agent) {
        return simulationStats.get(agent);
    }

    public HashMap<Agent, Statistics> getAgentStatistics() {
        return simulationStats;
    }

    public void reset() {
        simulationStats.clear();
    }
}
