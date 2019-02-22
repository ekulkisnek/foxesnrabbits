import java.util.List;
import java.util.Random;
/**
 * A class representing shared characteristics of prey.
 *
 * @author Mohammad Talal Hassan and Luke Kensik
 */
public abstract class Prey extends Animal
{
    // instance variables - replace the example below with your own

    private static final Random rand = Randomizer.getRandom();    
    // The prey animal's age.
    private int age;
    // The prey animal's food level, which is increased by eating krill.
    private int foodLevel;
    
    /**
     * Constructor for objects of class Prey
     */
    public Prey(boolean randomAge, Field field, Location location, int maxAge, int hungerCap)
    {
       super(field, location);
       if(randomAge) {
            age = rand.nextInt(maxAge);
            foodLevel = rand.nextInt(hungerCap +1);
        }
        else {
            age = 0;
            foodLevel = hungerCap/2;
        }
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals);
    
    /**
     * Move the prey animal to a nearby location
     */
    public void moveToNewLocation()
    {
        // Try to move into a free location.
        Location newLocation = getField().freeAdjacentLocation(getLocation());
        if(newLocation != null) {
            setLocation(newLocation);
        }
        else {
            // Overcrowding.
            setDead();
        }
    }
    
    /**
     * What the whale does at night. It does not move and food level stays
     * constant. Still can spread and die from disease.
     */
    protected void actNight()
    {
        foodLevel++;
        animalIsDiseased();
        deathByDisease();
    }
    
    /**
     * Eats krill if availible
     */
    protected void findFood(int hungerCap)
    {
        Location currentLocation = this.getLocation();
        int foodCount = getField().eatKrill(hungerCap - foodLevel, this.getLocation());
        foodLevel += foodCount;
    }
    
    /**
     * Increase the age.
     * This could result in the whale's death.
     */
    protected void incrementAge(int maxAge)
    {
        age++;
        if(age > maxAge) {
            setDead();
        }
    }
    
    /**
     * Food level decreases, dies if out of food
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Returns the age of the prey animal
     * @return the age of the prey animal
     */
    protected int getAge()
    {
        return age;
    }
}
