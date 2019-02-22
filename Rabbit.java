import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a rabbit.
 * Rabbits age, move, breed, eat krill, sleep and die.
 * They can get a disease and die
 * 
 * @author Mohammad Talal Hassan and Luke Kensik
 * @version 2016.02.29 (2)
 */
public class Rabbit extends Prey
{
    // Characteristics shared by all rabbits (class variables).

    // The age at which a rabbit can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a rabbit can live.
    private static final int MAX_AGE = 30;
    // The likelihood of a rabbit breeding.
    private static final double BREEDING_PROBABILITY = 0.35;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // Maximum amount of food a rabbit can eat
    private static final int HUNGER_CAP = 8;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();   

    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Rabbit(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, MAX_AGE, HUNGER_CAP);        
    }
    
    /**
     * This is what the rabbit does most of the time - it hops 
     * around, and eats krill. Sometimes it will breed or die of old age,
     * die of hunger, or die of disease
     * @param newRabbits A list to return newly born rabbits.
     */
    public void act(List<Animal> newRabbits)
    {
        incrementAge(MAX_AGE);
        incrementHunger();
        
        if(isAlive() && getField().getIsDay()) {
            animalIsDiseased();
            if(deathByDisease()){
                return;
            }
            findMate(newRabbits);
            
            findFood(HUNGER_CAP);
            
            moveToNewLocation();
        }
        else if (isAlive() && !getField().getIsDay()){
            actNight();
        }
    }
    
    /**
     * Look for rabbits adjacent to the current location
     * Only mates with another rabbit of the oppsite sex
     * @param newRabbits A list to return newly born rabbits.
     */
    private void findMate(List<Animal> newRabbits)
    {
        List<Location> adjacent = getField().adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = getField().getObjectAt(where);
            if(animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if (rabbit.isMale() != this.isMale()){
                    giveBirth(newRabbits);
                    matingDisease(rabbit);
                    return;
                }
            }
        }
    }
    
    /**
     * Check whether or not this rabbit is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newRabbits A list to return newly born rabbits.
     */
    private void giveBirth(List<Animal> newRabbits)
    {
        // New rabbits are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Rabbit young = new Rabbit(false, field, loc);
            newRabbits.add(young);
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
     * A rabbit can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return getAge() >= BREEDING_AGE;
    }
}
