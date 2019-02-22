import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a fox.
 * Foxes age, move, breed, eat rabbits, and die.
 * They can get a disease and die
 * 
 * @author Mohammad Talal Hassan and Luke Kensik
 * @version 2016.02.29 (2)
 */
public class Fox extends Predator
{
    // Characteristics shared by all foxes (class variables).
    
    // The age at which a fox can start to breed.
    private static final int BREEDING_AGE = 12;
    // The age to which a fox can live.
    private static final int MAX_AGE = 300;
    // The likelihood of a fox breeding.
    private static final double BREEDING_PROBABILITY = 0.21;
    // Likelihood that a fox may move at night
    private static final double NIGHT_MOVING_PROB = 0.50;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single rabbit.
    private static final int RABBIT_FOOD_VALUE = 18;
    // Chance they catch the disease from prey
    private static final double DISEASED_PREY_INFECTION = 0.60;
    // Maximum amount of food a fox can eat
    private static final int HUNGER_CAP = 60;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    

    /**
     * Create a fox. A fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Fox(boolean randomAge, Field field, Location location)
    {
        super(randomAge, false, field, location, MAX_AGE ,HUNGER_CAP);
    }
    
    /**
     * This is what the fox does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * die of old age, or die of disease
     * @param field The field currently occupied.
     * @param newFoxes A list to return newly born foxes.
     */
    public void act(List<Animal> newFoxes)
    {
        incrementAge(MAX_AGE);
        incrementHunger();
        if(isAlive() && getField().getIsDay()) {
            animalIsDiseased();
            if(deathByDisease()){
                return;
            }
            findMate(newFoxes);
            
            hunt();
        }
        else if (isAlive() && !getField().getIsDay()){ 
            actNight();
        }
    }
    
    /**
     * This determines the behaviour of the fox at night.
     * It may hunt if its food level is low or may hunt randomly
     */
    private void actNight()
    {
        if (getFoodLevel() < (3/4) * RABBIT_FOOD_VALUE){
            hunt();
        }
        else if (rand.nextDouble() <= NIGHT_MOVING_PROB) {
            hunt();
        }
        return;
    }
    
    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live rabbit is eaten.
     * There is also a chance that if they ate a diseased animal, they 
     * could catch the disease as well
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isAlive()) { 
                    rabbit.setDead();
                    diseaseFromInfectedAnimal(rabbit);
                    eat(RABBIT_FOOD_VALUE, HUNGER_CAP);
                    return where;
                }
            }
        }
        return null;
    }
           
    /**
     * Look for foxes adjacent to the current location
     * Only mates with another fox of the oppsite sex
     * @param newFoxes A list to return newly born foxes.
     */
    private void findMate(List<Animal> newFoxes)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Fox) {
                Fox fox = (Fox) animal;
                if (fox.isMale() != this.isMale()){
                    giveBirth(newFoxes);
                    matingDisease(fox);
                    return;
                }
            }
        }
    }
    
    /**
     * Check whether or not this fox is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFoxes A list to return newly born foxes.
     */
    private void giveBirth(List<Animal> newFoxes)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Fox young = new Fox(false, field, loc);
            newFoxes.add(young);
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
     * A fox can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return getAge() >= BREEDING_AGE;
    }
}
