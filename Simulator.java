import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple, but strange predator-prey simulator, based on a rectangular field
 * containing Rabbits, Foxes, Megalodons, Whales and Jellyfish
 * Yes, foxes and rabbits now live in the sea, and there can be droughts in the sea
 * Animals also mature and die in a matter of days :'(
 * 
 * @author Mohammad Talal Hassan and Luke Kensik
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 200;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 130;
    // The probability that a fox will be created in any given grid position.
    private static final double FOX_CREATION_PROBABILITY = 0.07;
    // The probability that a rabbit will be created in any given grid position.
    private static final double RABBIT_CREATION_PROBABILITY = 0.35;
    
    private static final double MEGALODON_CREATION_PROBABILITY = 0.03;
    
    // The probability that a rabbit will be created in any given grid position.
    private static final double JELLYFISH_CREATION_PROBABILITY = 0.001;
    
    // The probability that a rabbit will be created in any given grid position.
    private static final double WHALE_CREATION_PROBABILITY = 0.055;
    
    private static final int STEPS_IN_DAY = 24;

    // List of animals in the field.
    private List<Animal> animals;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    
    private int day;
    
    private int hour;
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        animals = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Rabbit.class, Color.ORANGE);
        view.setColor(Fox.class, Color.BLUE);
        view.setColor(Megalodon.class, Color.RED);
        view.setColor(Jellyfish.class, Color.CYAN);
        view.setColor(Whale.class, Color.MAGENTA);
                
        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }
    
    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            //delay(10);   // uncomment this to run more slowly            
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * animal.
     */
    public void simulateOneStep()
    {
        step++;
        hour++;
        incrementDay();
        checkTimeOfDay();
        field.updateWeather(); // May cause a change in weather
        

        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();        
        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals);
            if(! animal.isAlive()) {
                it.remove();
            }
        }
               
        // Add the newly born foxes and rabbits to the main lists.
        animals.addAll(newAnimals);        
        // Grows the krill in each grid sqaure
        field.growKrill();
                
        updateView();
    }    
   
    /**
     * Keeps count of the day based on the number of steps taken 
     * in the simulator
     */
    private void incrementDay()
    {
        int time = hour % STEPS_IN_DAY;
        if (time == 0){
            day++;
        }
    }
    
    /**
     * Checks the time of day and changes from day to night and vice-versa
     */
    private void checkTimeOfDay()
    {
        int time = hour % STEPS_IN_DAY;
        if ((time >= 21 && time <= 24) || (time >=0 && time <= 5)){
            if (field.getIsDay()){
               field.toggleDay(); 
            }            
        }
        else if (time >= 6 && time <= 20) {
            if (!field.getIsDay()) {
                field.toggleDay();
            }
        } 
    }
    
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        day = 1;
        hour = 6;
        animals.clear();
        populate();
        checkTimeOfDay();
        
        // Show the starting state in the view.
        updateView();
    }
    
    /**
     * Shows the most recent changes in the view
     */
    private void updateView()
    {
        String currentWeather = field.getWeather().getCurrentWeatherString();
        view.showStatus(step, field, day, currentWeather);
    }
    
    /**
     * Randomly populate the field with Foxes, Rabbits, Megalodons, Whales
     * and Jellyfish and reset the krill in the field
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        field.resetKrill();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= FOX_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Fox fox = new Fox(true, field, location);
                    animals.add(fox);
                }
                else if(rand.nextDouble() <= RABBIT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Rabbit rabbit = new Rabbit(true, field, location);
                    animals.add(rabbit);
                }
                else if(rand.nextDouble() <= MEGALODON_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Megalodon megalodon = new Megalodon(true, field, location);
                    animals.add(megalodon);
                }
                else if(rand.nextDouble() <= WHALE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Whale whale = new Whale(true, field, location);
                    animals.add(whale);
                }
                else if(rand.nextDouble() <= JELLYFISH_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Jellyfish jellyfish = new Jellyfish(true, field, location);
                    animals.add(jellyfish);
                }
                // else leave the location empty.
            }
        }
    }
    
    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
}
