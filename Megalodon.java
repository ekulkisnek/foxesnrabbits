import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a megalodon.
 * Megalodon age, move, breed,eat rabbits, foxes, whales and die.
 * They can get a disease and die
 * 
 * @author Mohammad Talal Hassan and Luke Kensik
 * @version 2016.02.29 (2)
 */
public class Megalodon extends Predator
{
    // Characteristics shared by all megalodones (class variables).
    
    // The age at which a megalodon can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a megalodon can live.
    private static final int MAX_AGE = 500;
    // The likelihood of a megalodon breeding.
    private static final double BREEDING_PROBABILITY = 0.10;
    // Likelihood that a megalodon may move at night
    private static final double NIGHT_MOVING_PROB = 0.1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single rabbit.
    private static final int RABBIT_FOOD_VALUE = 10;
    // The food value of a single fox.
    private static final int FOX_FOOD_VALUE = 25;
    // The food value of a single whale.
    private static final int WHALE_FOOD_VALUE = 100;
    // Chance they catch the disease from prey
    private static final double DISEASED_PREY_INFECTION = 0.60;
    // Maximum amount of food a megalodon can eat
    private static final int HUNGER_CAP = 200;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();


    /**
     * Create a megalodon. A megalodon can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the megalodon will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Megalodon(boolean randomAge, Field field, Location location)
    {
        super(randomAge, true ,field, location, MAX_AGE, HUNGER_CAP);

    }
    
    /**
     * This is what the megalodon does most of the time: it hunts for
     * rabbits, foxes, and whales. In the process, it might breed, die of hunger,
     * die of old age, or die of disease
     * @param field The field currently occupied.
     * @param newMegalodon A list to return newly born megalodones.
     */
    public void act(List<Animal> newMegalodons)
    {
        incrementAge(MAX_AGE);
        incrementHunger();
        if(isAlive() && getField().getIsDay()) {
            animalIsDiseased();
            if(deathByDisease()){
                return;
            }
            findMate(newMegalodons);  
            
            hunt();
        }
        else if (isAlive() && !getField().getIsDay()){ 
            actNight();
        }
    }
    
    /**
     * What the megalodon does at night
     * It may hunt if its food level is low or may hunt randomly
     */
    private void actNight()
    {
        if (getFoodLevel() < WHALE_FOOD_VALUE/2){
            hunt();
        }
        else if (rand.nextDouble() <= NIGHT_MOVING_PROB) {
            hunt();
        }
    }
    
    /**
     * Look for animals adjacent to the current location.
     * Only the first live animal (rabbit, whale, or fox) is eaten.
     * There is also a chance that if they ate a diseased animal, they 
     * could catch the disease as well
     * @return Where food was found, or null if it wasn't.
     */
    public Location findFood()
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
                    eat(RABBIT_FOOD_VALUE, HUNGER_CAP);
                    diseaseFromInfectedAnimal(rabbit);
                    rabbit.setDead();
                    return where;
                }
            }
            else if (animal instanceof Whale){
                Whale whale = (Whale) animal;
                if (whale.isAlive()){
                    eat(WHALE_FOOD_VALUE, HUNGER_CAP);
                    diseaseFromInfectedAnimal(whale);
                    whale.setDead();                    
                    return where;
                }
            }
            else if (animal instanceof Fox){
                Fox fox = (Fox) animal;
                if (fox.isAlive()){
                    eat(FOX_FOOD_VALUE, HUNGER_CAP);
                    diseaseFromInfectedAnimal(fox);
                    fox.setDead();                    
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Look for megalodons adjacent to the current location
     * Only mates with another megalodon of the oppsite sex
     * @param newMegalodon A list to return newly born megalodones.
     */
    private void findMate(List<Animal> newMegalodon)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Megalodon) {
                Megalodon megalodon = (Megalodon) animal;
                if (megalodon.isMale() != this.isMale()){
                    giveBirth(newMegalodon);
                    matingDisease(megalodon);
                    return;
                }
            }
        }
    }
    
    /**
     * Check whether or not this megalodon is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newMegalodon A list to return newly born megalodones.
     */
    private void giveBirth(List<Animal> newMegalodon)
    {
        // New megalodones are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Megalodon young = new Megalodon(false, field, loc);
            newMegalodon.add(young);
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
     * A megalodon can breed if it has reached the breeding age.
     * @return true if able to breed
     */
    private boolean canBreed()
    {
        return getAge() >= BREEDING_AGE;
    }
}
