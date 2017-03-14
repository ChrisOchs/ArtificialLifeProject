package artificiallife;

import artificiallife.stateactions.DeadAction;
import artificiallife.stateactions.MateAction;
import artificiallife.stateactions.StateAction;
import artificiallife.stateactions.WanderAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Chris
 */
public abstract class Agent extends MovingEntity {

    protected World world;

    public enum Gender { Male, Female };

    protected SimulationSettings settings = SimulationSettings.getSettings();
    protected SimulationInformation simInfo = SimulationInformation.getSimulationInformation();
    
    private int sightRange = 0;
    private int sightClarityRange = 0;

    private int smellRange = 0;
    
    private int size = 0;

    private int naturalLifespan = 0;

    private double maturityPercentage = 0.0;

    private int gestationPeriod = 0;

    private int pregnantForUpdates = 0;
    private boolean isPregnant = false;
    private List<Agent> children = Collections.synchronizedList(new ArrayList<Agent>());
            
    private int maturityAge;
    private int maxChildren;

    private int speed;

    protected int maxEnergy;
    protected int currentEnergy;

    private int age = 0;

    private Gender gender;

    protected StateAction currentState = new WanderAction(this);

    protected boolean isDead = false;

    protected Agent(World world, int x, int y, int size, int speed, int naturalLifeSpan, int sightRange, int sightClarity, int smellRange,
            double maturityPercentage, int maxEnergy, int gestationPeriod, int maxChildren, Gender gender) {

        super(x, y, world.getBounds());

        this.world = world;

        this.size = size;
        this.naturalLifespan = naturalLifeSpan;
        this.smellRange = smellRange;
        this.sightRange = sightRange;
        this.sightClarityRange = sightClarity;
        this.maturityPercentage = maturityPercentage;

        this.maturityAge = (int)(naturalLifeSpan * maturityPercentage);
        this.gestationPeriod = gestationPeriod;
        this.maxChildren = maxChildren;

        this.speed = speed;

        this.maxEnergy = maxEnergy;
        this.currentEnergy = maxEnergy;
        this.gender = gender;

        simInfo.registerAgent(this);
    }
    
    public void createChildrenWith(Agent agent) {
        this.isPregnant = true;
        int childCount = (int)(Math.random() * maxChildren);

        for(int c = 0; c < childCount; c++) {
            children.add(reproduceWith(agent));
        }
    }

    protected Agent reproduceWith(Agent a) {
        if (a.getClass().equals(this.getClass()) && a.gender != this.gender) {
            int newSightRange = 0;
            int newSightRadius = 0;

            int newSmellRange = 0;

            int newSize = 0;
            int newSpeed = 0;
            int newMaxEnergy = 0;

            int newNaturalLifespan = 0;

            double newMaturityPercentage = 0;

            int newGestationPeriod = 0;

            int newMaxChildren = 0;

            Gender newGender = Math.random() < 0.5 ? Agent.Gender.Male : Agent.Gender.Female;

            if (this instanceof Carnivore && !settings.getEvolvePredator()) {
                return new Carnivore(world, -1, -1, this.size, this.speed, this.naturalLifespan, this.sightRange,
                        this.sightClarityRange, this.smellRange, this.maturityPercentage,
                        this.maxEnergy, this.gestationPeriod, maxChildren, newGender);
            } else if (this instanceof Herbivore && !settings.getEvolvePrey()) {
                return new Herbivore(world, -1, -1, this.size, this.speed, this.naturalLifespan, this.sightRange,
                        this.sightClarityRange, this.smellRange, this.maturityPercentage,
                        this.maxEnergy, this.gestationPeriod, maxChildren, newGender);
            }
            

            if (settings.getReproductionType() == SimulationSettings.ReproductionType.Average) {
                newSightRange = (this.getSightRange() + a.getSightRange()) / 2;
                newSightRadius = (this.getSightClarityRange() + a.getSightClarityRange()) / 2;

                newSmellRange = (this.getSmellRange() + a.getSmellRange()) / 2;

                newSize = (this.getSize() + a.getSize()) / 2;
                newSpeed = (this.getSpeed() + a.getSpeed()) / 2;

                newMaxEnergy = (this.getMaxEnergy() + a.getMaxEnergy()) / 2;

                newNaturalLifespan = (this.getNaturalLifespan() + a.getNaturalLifespan()) / 2;

                newMaturityPercentage = (this.getMaturityPercentage() + a.getMaturityPercentage()) / 2;

                newGestationPeriod = (this.getGestationPeriod() + a.getGestationPeriod()) / 2;

                newMaxChildren = (this.getMaxChildren() + a.getMaxChildren()) / 2;

            } else if (settings.getReproductionType() == SimulationSettings.ReproductionType.RandomParent) {
                newSightRange = Math.random() < 0.5 ? this.getSightRange() : a.getSightRange();
                newSightRadius = Math.random() < 0.5 ? this.getSightClarityRange() : a.getSightClarityRange();

                newSmellRange = Math.random() < 0.5 ? this.getSmellRange() : a.getSmellRange();

                newSize = Math.random() < 0.5 ? this.getSize() : a.getSize();
                newSpeed = Math.random() < 0.5 ? this.getSpeed() : a.getSpeed();

                newMaxEnergy = Math.random() < 0.5 ? this.getMaxEnergy() : a.getMaxEnergy();

                newNaturalLifespan = Math.random() < 0.5 ? this.getNaturalLifespan() : a.getNaturalLifespan();

                newMaturityPercentage = Math.random() < 0.5 ? this.getMaturityPercentage() : a.getMaturityPercentage();
                newGestationPeriod = Math.random() < 0.5 ? this.getGestationPeriod() : a.getGestationPeriod();

                newMaxChildren = Math.random() < 0.5 ? this.getMaxChildren() : a.getMaxChildren();

            } else if(settings.getReproductionType() == SimulationSettings.ReproductionType.HalfAndHalf) {
                ArrayList<Integer> par1 = new ArrayList<Integer>();
                ArrayList<Integer> par2 = new ArrayList<Integer>();

                ArrayList<Integer> geneOrder = new ArrayList<Integer>();

                for(int c = 0; c < 5; c++) {
                    par1.add(0);
                    par2.add(1);
                }

                while(!par1.isEmpty() && !par2.isEmpty()) {
                    if(Math.random() < 0.5) {
                        geneOrder.add(0);
                        par1.remove(0);
                    } else {
                        geneOrder.add(1);
                        par2.remove(0);
                    }
                }

                while(!par1.isEmpty()) {
                    geneOrder.add(0);
                    par1.remove(0);
                }

                while(!par2.isEmpty()) {
                    geneOrder.add(1);
                    par2.remove(0);
                }

                newSightRange = geneOrder.get(0) == 0 ? this.getSightRange() : a.getSightRange();
                newSightRadius = geneOrder.get(1) == 0 ? this.getSightClarityRange() : a.getSightClarityRange();

                newSmellRange = geneOrder.get(2) == 0 ? this.getSmellRange() : a.getSmellRange();

                newSize = geneOrder.get(3) == 0 ? this.getSize() : a.getSize();
                newSpeed = geneOrder.get(4) == 0 ? this.getSpeed() : a.getSpeed();
                newMaxEnergy = geneOrder.get(5) == 0 ? this.getMaxEnergy() : a.getMaxEnergy();

                newNaturalLifespan = geneOrder.get(6) == 0 ? this.getNaturalLifespan() : a.getNaturalLifespan();

                newMaturityPercentage = geneOrder.get(7) == 0 ? this.getMaturityPercentage() : a.getMaturityPercentage();
                
                newGestationPeriod = geneOrder.get(8) == 0 ? this.getGestationPeriod() : a.getGestationPeriod();

                newMaxChildren = geneOrder.get(9) == 0 ? this.getMaxChildren() : a.getMaxChildren();
            }

            if (settings.getMutationChance() > 0) {
                double mutateChance = settings.getMutationChance();

                double mutateAmount = this.getRandomMutatePercentage();

                newSightRange = Math.random() < mutateChance ? (int)(newSightRange * mutateAmount) : newSightRange;

                mutateAmount = this.getRandomMutatePercentage();
                
                newSightRadius = Math.random() < mutateChance ? (int)(newSightRadius * mutateAmount) : newSightRadius;

                mutateAmount = this.getRandomMutatePercentage();

                newSmellRange = Math.random() < mutateChance ? (int)(newSmellRange * mutateAmount): newSmellRange;

                mutateAmount = this.getRandomMutatePercentage();

                newSize = Math.random() < mutateChance ? (int)(newSize * mutateAmount) : newSize;

                newSpeed = Math.random() < mutateChance ? (int)(newSpeed * mutateAmount) : newSpeed;

                newMaxEnergy = Math.random() < mutateChance ? (int)(newMaxEnergy * mutateAmount) : newMaxEnergy;

                mutateAmount = this.getRandomMutatePercentage();

                newNaturalLifespan = Math.random() < mutateChance ? (int)(newNaturalLifespan * mutateAmount) : newNaturalLifespan;

                mutateAmount = this.getRandomMutatePercentage();

                newMaturityPercentage = Math.random() < mutateChance ? newMaturityPercentage * mutateAmount: newMaturityPercentage;

                mutateAmount = this.getRandomMutatePercentage();

                newGestationPeriod = Math.random() < mutateChance ? (int)(newGestationPeriod * mutateAmount) : newGestationPeriod;
            
                mutateAmount = this.getRandomMutatePercentage();

                newMaxChildren = Math.random() < mutateChance ? (int)(newMaxChildren * mutateAmount) : newMaxChildren;
            }

            if(a instanceof Carnivore) {
                return new Carnivore(world, -1, -1, newSize, newSpeed, newNaturalLifespan, newSightRange, newSightRadius,
                        newSmellRange, newMaturityPercentage, newMaxEnergy, newGestationPeriod, newMaxChildren, newGender);
            } else {
                return new Herbivore(world, -1, -1, newSize, newSpeed, newNaturalLifespan, newSightRange, newSightRadius,
                        newSmellRange, newMaturityPercentage, newMaxEnergy, newGestationPeriod, newMaxChildren, newGender);
            }
        }

        return null;
    }

    private double getRandomMutatePercentage() {
        double result = Math.random() * settings.getMaxMutationAmount();

        if(Math.random() < 0.5) {
            result += 1;
        }

        return result;
    }

    protected void update() {
        if(!isActive) {
            return;
        }

        this.age++;

        if(isPregnant) {
            this.pregnantForUpdates++;

            if(this.pregnantForUpdates > this.gestationPeriod) {

                for(Agent a : children) {
                    world.addNewAgent(a);
                    SimulationInformation.getSimulationInformation().getAgentStatistics(this).children++;
                    a.position.x = this.position.x;
                    a.position.y = this.position.y;
                }

                this.setPregnant(false);
            }
        } else {
            List<Agent> agentsInWorld = world.getAgents();

            for (int c = agentsInWorld.size() - 1; c >= 0; c--) {
                Agent agent = agentsInWorld.get(c);

                if(agent == this) {
                    continue;
                }

                if (agent.getClass().equals(this.getClass()) && agent.isMature() && !agent.isPregnant()
                        && !agent.getGender().equals(this.getGender())) {
                    Vector2<Double> matePosition = agent.getPosition();

                    double deltaX = matePosition.x - this.position.x;
                    double deltaY = matePosition.y - this.position.y;

                    double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                    if (distance < this.getSightRange()) {
                        this.setCurrentState(new MateAction(this, agent));
                        break;
                    }
                }
            }
        }
        
        if(age > naturalLifespan) {
            this.kill();
            SimulationInformation.getSimulationInformation().getAgentStatistics(this).status = "Died";
        }
    }

    public Gender getGender() {
        return gender;
    }

    /**
     * @return the sightRange
     */
    public int getSightRange() {
        return sightRange;
    }

    /**
     * @param sightRange the sightRange to set
     */
    public void setSightRange(int sightRange) {
        this.sightRange = sightRange;
    }

    /**
     * @return the sightRadius
     */
    public int getSightClarityRange() {
        return sightClarityRange;
    }

    /**
     * @param sightRadius the sightRadius to set
     */
    public void setSightClarityRang(int sightRadius) {
        this.sightClarityRange = sightRadius;
    }

    /**
     * @return the smellRange
     */
    public int getSmellRange() {
        return smellRange;
    }

    /**
     * @param smellRange the smellRange to set
     */
    public void setSmellRange(int smellRange) {
        this.smellRange = smellRange;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    public int getSpeed() {
        return speed;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the naturalLifespan
     */
    public int getNaturalLifespan() {
        return naturalLifespan;
    }

    /**
     * @param naturalLifespan the naturalLifespan to set
     */
    public void setNaturalLifespan(int naturalLifespan) {
        this.naturalLifespan = naturalLifespan;
    }

    /**
     * @return the maturityPercentage
     */
    public double getMaturityPercentage() {
        return maturityPercentage;
    }

    public boolean isMature() {
        return age >= maturityAge;
    }

    /**
     * @param maturityPercentage the maturityPercentage to set
     */
    public void setMaturityPercentage(double maturityPercentage) {
        this.maturityPercentage = maturityPercentage;
    }

    public void setCurrentState(StateAction action) {
        this.currentState = action;
    }

    public StateAction getCurrentState() {
        return currentState;
    }
    
    public int getMaxEnergy() {
        return maxEnergy;
    }

    public int getMaxChildren() {
        return maxChildren;
    }

    public void kill() {
        this.isDead = true;
        this.setCurrentState(new DeadAction(this));
    }

    public boolean isDead() {
        return isDead;
    }

    public void modifyEnergy(int amount) {
        this.currentEnergy += amount;
        
        if(currentEnergy < 0) {
            this.isActive = false;
        }
    }

    public int getCurrentEnergy() {
        return currentEnergy;
    }

    public int getGestationPeriod() {
        return gestationPeriod;
    }

    public boolean isPregnant() {
        return isPregnant;
    }

    public void setPregnant(boolean value) {
        this.isPregnant = value;

        if(!isPregnant)
        {
            this.pregnantForUpdates = 0;
            this.children.clear();
        }
    }

    public int getAge() {
        return age;
    }
}
