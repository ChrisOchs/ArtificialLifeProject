package artificiallife;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Chris
 */
public class SimulationMenu extends JPanel {
    private SimulationSettings settings = SimulationSettings.getSettings();

    private World world;

    private Thread simThread;

    private boolean simReset = false;

    private JLabel currentUpdate = new JLabel();

    public SimulationMenu(World world) {
        this.world = world;

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(currentUpdate);

        this.add(new JLabel("SIMULATION OPTIONS"));

        final JCheckBox preyEvolve = new JCheckBox("Hold Prey as Constant");
        final JCheckBox predatorEvolve = new JCheckBox("Hold Predator as Constant");

        preyEvolve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                settings.setEvolvePrey(preyEvolve.isSelected());
            }
        });

        predatorEvolve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                settings.setEvolvePredator(predatorEvolve.isSelected());
            }
        });

        this.add(preyEvolve);
        this.add(predatorEvolve);

        final JSlider mutateAmountSlider = new JSlider(0, 100);
        mutateAmountSlider.setMajorTickSpacing(10);
        mutateAmountSlider.setPaintTicks(true);
        mutateAmountSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                settings.setMaxMutationAmount(mutateAmountSlider.getValue() / 100.0);
            }
        });

        final JSlider mutateSlider = new JSlider(0, 100);
        mutateSlider.setMajorTickSpacing(10);
        mutateSlider.setPaintTicks(true);
        mutateSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                double value = mutateSlider.getValue() / 100.0;

                settings.setMutationChance(value);

                if(value == 0) {
                    mutateAmountSlider.setEnabled(false);
                } else {
                    mutateAmountSlider.setEnabled(true);
                }
            }
        });

        this.add(new JLabel("Mutate Chance"));
        this.add(mutateSlider);

        this.add(new JLabel("Mutate Amount"));
        this.add(mutateAmountSlider);

        this.add(new JLabel("Crossover Type"));

        ButtonGroup bg = new ButtonGroup();

        final JRadioButton randomParent = new JRadioButton("Random Parent");
        final JRadioButton average = new JRadioButton("Average");
        final JRadioButton halfAndHalf = new JRadioButton("Half and Half");

        randomParent.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                settings.setReproductionType(SimulationSettings.ReproductionType.RandomParent);
            }
        });

        average.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                settings.setReproductionType(SimulationSettings.ReproductionType.Average);
            }
        });

        halfAndHalf.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                settings.setReproductionType(SimulationSettings.ReproductionType.HalfAndHalf);
            }
        });

        bg.add(randomParent);
        bg.add(average);
        bg.add(halfAndHalf);

        average.setSelected(true);

        this.add(randomParent);
        this.add(average);
        this.add(halfAndHalf);

        this.add(new JLabel("Simulation Run Information"));

        JPanel iterationsPanel = new JPanel();
        iterationsPanel.add(new JLabel("Generation Iterations"));

        final JSpinner iterationSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 100, 1));
        iterationsPanel.add(iterationSpinner);

        this.add(iterationsPanel);

        JPanel pickPanel = new JPanel();
        pickPanel.add(new JLabel("Choose Best"));

        final JSpinner pickSpinner = new JSpinner(new SpinnerNumberModel(10, 2, 20, 1));
        pickPanel.add(pickSpinner);

        this.add(pickPanel);

        JPanel generationPanel = new JPanel();
        generationPanel.add(new JLabel("# of Generations"));

        final JSpinner generationSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
        generationPanel.add(generationSpinner);

        this.add(generationPanel);

        JButton startButton = new JButton("Start Simulation");
        JButton resetButton = new JButton("Reset Simulation");


        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                SimulationMenu.this.world.reset();
                SimulationMenu.this.world.setRunning(false);
                SimulationInformation.getSimulationInformation().reset();

                simReset = true;
            }
        });

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                final int iterations = (Integer)iterationSpinner.getModel().getValue();
                final int chooseTop = (Integer)pickSpinner.getModel().getValue();
                final int generations = (Integer)generationSpinner.getModel().getValue();

                simReset = false;
                
                simThread = new Thread(new Runnable() {
                    public void run() {
                        runSimulation(iterations, chooseTop, generations);
                    }
                });

                simThread.start();
            }
        });

        this.add(startButton);
        this.add(resetButton);
    }

    private void runSimulation(int iterationsPerGeneration, int chooseTop, int generations) {

        ArrayList<Agent> prey = generatePrey(chooseTop, settings.getEvolvePrey());
        ArrayList<Agent> predators = generatePredator(chooseTop, settings.getEvolvePredator());

        boolean carnivoresAdded = false;

        SimulationInformation si = SimulationInformation.getSimulationInformation();

        for (int g = 0; g < generations; g++) {
            ArrayList<Agent> originalPrey = (ArrayList<Agent>)prey.clone();
            ArrayList<Agent> originalPredators = (ArrayList<Agent>)predators.clone();

            for (int c = 0; c < iterationsPerGeneration; c++) {
                carnivoresAdded = false;
                world.reset();
                world.setRunning(true);

                for (Agent agent : prey) {
                    world.addNewAgent(agent);
                }

                while (world.isRunning() && world.getUpdates() < 2000) {

                    currentUpdate.setText("Progress: " + world.getUpdates() + "/" + 2000);
                    if(!carnivoresAdded && world.getUpdates() > 300) {
                        world.setRunning(false);
                        for(Agent agent : predators) {
                            world.addNewAgent(agent);
                        }
                        world.setRunning(true);

                        carnivoresAdded = true;
                    }
                }

                if(simReset) {
                    return;
                }

                prey.clear();
                predators.clear();

                for(Agent a : originalPrey) {
                    prey.add(cloneAgent(a));
                }

                for(Agent a : originalPredators) {
                    predators.add(cloneAgent(a));
                }

            }

            prey = findBestPrey(si.getAgentStatistics(), chooseTop);
            predators = findBestPredators(si.getAgentStatistics(), chooseTop / 2);

            si.reset();

            for(Agent a : prey) {
                si.registerAgent(a);
            }
            
            for(Agent a : predators) {
                si.registerAgent(a);
            }
        }
    }

    private Agent cloneAgent(Agent a) {
        int x = (int)(Math.random() * settings.getWorldSize().x);
        int y = (int)(Math.random() * settings.getWorldSize().y);
        
        if(a instanceof Herbivore) {
            return new Herbivore(world, x, y, a.getSize(), a.getSpeed(), a.getNaturalLifespan(),
                    a.getSightRange(), a.getSightClarityRange(), a.getSmellRange(), a.getMaturityPercentage(), a.getMaxEnergy(),
                    a.getGestationPeriod(), a.getMaxChildren(), a.getGender());
        } else {
            return new Carnivore(world, x, y, a.getSize(), a.getSpeed(), a.getNaturalLifespan(),
                    a.getSightRange(), a.getSightClarityRange(), a.getSmellRange(), a.getMaturityPercentage(), a.getMaxEnergy(),
                    a.getGestationPeriod(), a.getMaxChildren(), a.getGender());
        }
    }

    private ArrayList<Agent> findBestPrey(HashMap<Agent, Statistics> simStats, int number) {
        final HashMap<Agent, Double> scores = new HashMap<Agent, Double>();

        for(Entry<Agent, Statistics> entry : simStats.entrySet()) {
            if (entry.getKey() instanceof Herbivore) {
                int age = entry.getKey().getAge() / 50;
                int children = entry.getValue().children;
                int consumed = entry.getValue().consumed;

                double score = Math.sqrt(age * age + children * children + consumed * consumed);

                scores.put(entry.getKey(), score);
            }
        }

        ArrayList<Agent> bestPrey = new ArrayList<Agent>(scores.keySet());

        Collections.sort(bestPrey, new Comparator<Agent>() {
            public int compare(Agent a, Agent b) {
                Double aScore = scores.get(a);
                Double bScore = scores.get(b);

                return aScore.compareTo(bScore);
            }
        });

        ArrayList<Agent> result = new ArrayList<Agent>();

        for(int c = 0; c < number; c++) {
            Agent a = bestPrey.get(c);
            result.add(cloneAgent(a));
        }

        return result;
    }

    private ArrayList<Agent> findBestPredators(HashMap<Agent, Statistics> simStats, int number) {
        final HashMap<Agent, Double> scores = new HashMap<Agent, Double>();

        for(Entry<Agent, Statistics> entry : simStats.entrySet()) {
            if (entry.getKey() instanceof Carnivore) {
                int age = entry.getKey().getAge() / 50;
                int children = entry.getValue().children;
                int consumed = entry.getValue().consumed;

                double score = Math.sqrt(age * age + children * children + consumed * consumed);

                scores.put(entry.getKey(), score);
            }
        }

        ArrayList<Agent> bestPredators = new ArrayList<Agent>(scores.keySet());

        Collections.sort(bestPredators, new Comparator<Agent>() {
            public int compare(Agent a, Agent b) {
                Double aScore = scores.get(a);
                Double bScore = scores.get(b);

                return aScore.compareTo(bScore);
            }
        });

        ArrayList<Agent> result = new ArrayList<Agent>();

        for(int c = 0; c < number; c++) {
            Agent a = bestPredators.get(c);

            result.add(cloneAgent(a));
        }

        return result;
    }

    private ArrayList<Agent> generatePrey(int numberOf, boolean uniform) {

        ArrayList<Agent> prey = new ArrayList<Agent>();

        if (uniform) {
            for (int c = 0; c < numberOf; c++) {

                prey.add(new Herbivore(
                        world,
                        (int) (Math.random() * world.getBounds().width),
                        (int) (Math.random() * world.getBounds().height),
                        (int) (Math.random() * 40 + 1),
                        (int) (Math.random() * 16 + 2),
                        (int) (Math.random() * 300 + 50),
                        (int) (Math.random() * 200 + 50),
                        (int) (Math.random() * 200 + 20),
                        (int) (Math.random() * 500 + 50),
                        Math.random() * 0.25 + 0.01,
                        (int) (Math.random() * 5000 + 100),
                        (int) (Math.random() * 80 + 10),
                        (int) (Math.random() * 5 + 1),
                        (c % 2 == 0) ? Agent.Gender.Male : Agent.Gender.Female));
            }
        } else {

            int size = (int) (Math.random() * 40 + 1);
            int speed = (int) (Math.random() * 16 + 2);
            int lifeSpan = (int) (Math.random() * 300 + 50);
            int sightRange = (int) (Math.random() * 200 + 50);
            int clarityRange = (int) (Math.random() * 200 + 50);
            int smellRange = (int) (Math.random() * 500 + 50);
            double maturity = Math.random() * 0.25 + 0.01;
            int energy = (int) (Math.random() * 5000 + 100);
            int gestation = (int) (Math.random() * 80 + 10);
            int maxChildren = (int)(Math.random() * 5 + 1);

            for (int c = 0; c < numberOf; c++) {
                prey.add(new Herbivore(
                        world,
                        (int) (Math.random() * world.getBounds().width),
                        (int) (Math.random() * world.getBounds().height),
                        size,
                        speed,
                        lifeSpan,
                        sightRange,
                        clarityRange,
                        smellRange,
                        maturity,
                        energy,
                        gestation,
                        maxChildren,
                        (c % 2 == 0) ? Agent.Gender.Male : Agent.Gender.Female));
            }
        }

        return prey;
    }

    private ArrayList<Agent> generatePredator(int numberOf, boolean uniform) {

        ArrayList<Agent> predators = new ArrayList<Agent>();

        if (uniform) {
            for (int c = 0; c < numberOf / 2; c++) {

                predators.add(new Carnivore(
                        world,
                        (int) (Math.random() * world.getBounds().width),
                        (int) (Math.random() * world.getBounds().height),
                        (int) (Math.random() * 40 + 1),
                        (int) (Math.random() * 16 + 2),
                        (int) (Math.random() * 300 + 50),
                        (int) (Math.random() * 200 + 50),
                        (int) (Math.random() * 200 + 50),
                        (int) (Math.random() * 500 + 50),
                        Math.random() * 0.25 + 0.01,
                        (int) (Math.random() * 5000 + 100),
                        (int) (Math.random() * 80 + 10),
                        (int) (Math.random() * 5 + 1),
                        (c % 2 == 0) ? Agent.Gender.Male : Agent.Gender.Female));
            }
        } else {

            int size = (int) (Math.random() * 40 + 1);
            int speed = (int) (Math.random() * 16 + 2);
            int lifeSpan = (int) (Math.random() * 300 + 50);
            int sightRange = (int) (Math.random() * 200 + 50);
            int clarityRange = (int) (Math.random() * 200 + 50);
            int smellRange = (int) (Math.random() * 500 + 50);
            double maturity = Math.random() * 0.25 + 0.01;
            int energy = (int) (Math.random() * 5000 + 100);
            int gestation = (int) (Math.random() * 80 + 10);
            int maxChildren = (int) (Math.random() * 5 + 1);

            for (int c = 0; c < numberOf / 2; c++) {
                predators.add(new Carnivore(
                        world,
                        (int) (Math.random() * world.getBounds().width),
                        (int) (Math.random() * world.getBounds().height),
                        size,
                        speed,
                        lifeSpan,
                        sightRange,
                        clarityRange,
                        smellRange,
                        maturity,
                        energy,
                        gestation,
                        maxChildren,
                        (c % 2 == 0) ? Agent.Gender.Male : Agent.Gender.Female));
            }
        }

        return predators;
    }
}
