package artificiallife;

/**
 *
 * @author Chris
 */
public class SimulationSettings {

    private static SimulationSettings settings;

    public static SimulationSettings getSettings() {
        if(settings == null) {
            settings = new SimulationSettings();
        }
        
        return settings;
    }

    public enum ReproductionType { RandomParent, Average, HalfAndHalf };

    private ReproductionType currentType = ReproductionType.Average;

    private double mutationChance = 0.0;

    private double maxMutationAmount = 0.0;

    private boolean evolvePrey = true;
    private boolean evolvePredator = true;

    private Vector2<Integer> worldSize = new Vector2<Integer>(0, 0);

    private SimulationSettings() {
        
    }

    public Vector2<Integer> getWorldSize() {
        return worldSize;
    }

    public ReproductionType getReproductionType() {
        return currentType;
    }

    public void setReproductionType(ReproductionType type) {
        this.currentType = type;
    }

    public double getMutationChance() {
        return mutationChance;
    }
    
    public void setMutationChance(double mutationChance) {
        this.mutationChance = mutationChance;
    }

    public void setMaxMutationAmount(double maxMutationAmount) {
        this.maxMutationAmount = maxMutationAmount;
    }

    public double getMaxMutationAmount() {
        return maxMutationAmount;
    }

    public void setWorldSize(int width, int height) {
        this.worldSize = new Vector2<Integer>(width, height);
    }

    public void setEvolvePrey(boolean value) {
        this.evolvePrey = value;
    }

    public void setEvolvePredator(boolean value) {
        this.evolvePredator = value;
    }
    
    public boolean getEvolvePrey() {
        return evolvePrey;
    }

    public boolean getEvolvePredator() {
        return evolvePredator;
    }
}
