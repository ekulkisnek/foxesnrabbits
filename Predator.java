import java.util.List;
import java.util.Random;
/**
 * A class representing shared characteristics of predators.
 *
 * @author Mohammad Talal Hassan and Luke Kensik
 */
public abstract class Predator extends Animal
{
    // Chance a predator catches the disease from prey
    private static final double DISEASED_PREY_INFECTION = 0.60;
    // A default value for food level
    private static final int DEFAULT_FOOD_LEVEL = 10;
    
    private static final Random rand = Randomizer.getRandom();
    // The predator's age.
    private int age;
    // The predator's food level, which is increased by eating other animals.
    private int foodLevel;
    /**
     * Constructor for objects of class Predatot
     */
    public Predator(boolean randomAge, boolean defaultFL, Field field, Location location, int maxAge, int hungerCap)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(maxAge);
            foodLevel = rand.nextInt(hungerCap);            
        }
        else {
            age = 0;
            if (defaultFL) {
                foodLevel = DEFAULT_FOOD_LEVEL;
            }
            else{
                foodLevel = hungerCap/3;
            }
        }
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals);
        
    /**
     * Look for food adjacent to its current position
     * Otherwise it tries to move to another position
    */
    protected void hunt()
    {
        // Move towards a source of food if found.
        Location newLocation = findFood();
        if(newLocation == null) { 
            // No food found - try to move to a free location.
            newLocation = getField().freeAdjacentLocation(getLocation());
        }
        // See if it was possible to move.
        if(newLocation != null) {
            setLocation(newLocation);
        }
        else {
            // Overcrowding.
            setDead();
        }
    }
    
    /**
     * Increase the age. This could result in the megalodon's death.
     */
    protected void incrementAge(int maxAge)
    {
        age++;
        if(age > maxAge) {
            setDead();
        }
    }
    
    /**
     * Make this megalodon more hungry. This could result in the megalodon's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Make the predator find another animal to eat
     */
    protected abstract Location findFood();
    
    /**
     * Increased the food level of the megalodon up to its hunger cap
     * @param food value of food to eat as int
     */
    protected void eat(int food, int hungerCap)
    {
        if (food+foodLevel > hungerCap){
            foodLevel = hungerCap;
        }
        else{
            foodLevel += food;
        }
    }
    
    /**
     * May infect the predator if the animal was diseased
     * @param an animal that may infect the predator 
     */
    protected void diseaseFromInfectedAnimal(Animal animal)
    {
        if (animal.isInfected() && rand.nextDouble() < DISEASED_PREY_INFECTION) {
            this.infectAnimal();
        }
    }
    
    /**
     * Returns the age of the predator animal
     * @return the age of the predator animal
     */
    protected int getAge()
    {
        return age;
    }
    
    /**
     * Returns the food level of the predator animal
     * @return the food level of the predator animal
     */
    protected int getFoodLevel()
    {
        return foodLevel;
    }
}
