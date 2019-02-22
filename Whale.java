import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a rabbit.
 * Whales age, move, breed, eat krill, sleep and die.
 * They can get a disease and die
 * 
 * @author Mohammad Talal Hassan and Luke Kensik
 * @version 2016.02.29 (2)
 */
public class Whale extends Prey
{
    // Characteristics shared by all whales (class variables).

    // The age at which a whale can start to breed.
    private static final int BREEDING_AGE = 50;
    // The age to which a whale can live.
    private static final int MAX_AGE = 1000;
    // The likelihood of a whale breeding.
    private static final double BREEDING_PROBABILITY = 0.15;//.05
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // Maximum amount of food a whale can eat
    private static final int HUNGER_CAP = 75;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();    


    /**
     * Create a new whale. A whale may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the whale will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Whale(boolean randomAge, Field field, Location location)
    {
        super(randomAge,field, location, MAX_AGE, HUNGER_CAP);        
    }
    
    /**
     * This is what the whale does most of the time - it swims 
     * around, and eats krill. Sometimes it will breed or die of old age,
     * die of hunger, or die of disease
     * @param newWhales A list to return newly born whales.
     */
    public void act(List<Animal> newWhales)
    {
        incrementAge(MAX_AGE);
        incrementHunger();
        
        if(isAlive() && getField().getIsDay()) {
            animalIsDiseased();
            if(deathByDisease()){
                return;
            }
            findMate(newWhales);
                       
            findFood(HUNGER_CAP);
            
            moveToNewLocation();
        }
        else if (isAlive() && !getField().getIsDay()) {
            actNight();
        }
    }
    
    /**
     * Checks for adjacent whales of opposite sex to mate with
     * May spread disease to partner if current whale has disease
     * @param newWhales list of whales to populate with offspring 
     */
    private void findMate(List<Animal> newWhales)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Whale) {
                Whale whale = (Whale) animal;
                if (whale.isMale() != this.isMale()){
                    giveBirth(newWhales);
                    matingDisease(whale);
                    return;
                }
            }
        }
    }
    
    /**
     * Check whether or not this whale is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newWhales A list to return newly born whales.
     */
    private void giveBirth(List<Animal> newWhales)
    {
        // New whales are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Whale young = new Whale(false, field, loc);
            newWhales.add(young);
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A whale can breed if it has reached the breeding age.
     * @return true if the whale can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return getAge() >= BREEDING_AGE;
    }
}
