import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 * 
 * @author Mohammad Talal Hassan and Luke Kensik
 * @version 2016.02.29 (2)
 */
public abstract class Animal
{
    // The probability an animal becomes infected without contact
    private static final double INFECTED_PROBABILITY = 0.001;
    // Probability an animal will die if dieseased
    private static final double DISEASE_DEATH_PROBABILITY = 0.01;
    // Probability disease will spread when mating
    private static final double MATING_DISEASE_SPREAD = 0.15;
    // Probability that disease spreads between animals
    private static double diseaseSpreadProbability;
    
    private static final Random rand = Randomizer.getRandom();
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.        
    private Location location;
    // The animal's sex
    private boolean isMale;
    // Whether the animal is infected or not
    private boolean isInfected;
    
    
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        alive = true;
        setSex();
        this.field = field;
        setLocation(location);
        diseaseSpreadProb();
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals);
    
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Check whether the animal is male or female
     * @return true if the animal is a male
     */
    protected boolean isMale()
    {
        return isMale;
    }
    
    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }
    
    /**
     * Randomly assigns an animal to be either Male or Female
     */
    private void setSex()
    {
        isMale = rand.nextBoolean();
    }
    
    /**
     * The probability that the disease spreads depends on the weather
     * It is more likely to spread if it's raining
     */
    private void diseaseSpreadProb()
    {
        if (field.getWeather().getIsRaining()) {
            diseaseSpreadProbability = 0.025;
        }
        else {
            diseaseSpreadProbability = 0.01;
        }
    }
    
    /**
     * This gives a low chance of an animal catching a disease 
     */
    protected void catchDisease() 
    {
        if(rand.nextDouble() <= INFECTED_PROBABILITY) {
            isInfected = true;
        }
        else {
            isInfected = false;
        }
    }
    
    /**
     * Checks whether the animal is infected or not
     * @return True if the animal is infected
     */
    protected boolean isInfected()
    {
        return isInfected;
    }
    
    /**
     * There is a chance that an animal may become infected
     * This sets isInfected to 'true' if a random double is 
     * less than diseaseSpreadProbability
     */
    protected void infect() 
    {
        if(rand.nextDouble() <= diseaseSpreadProbability) {
            isInfected = true;
        }
    }
    
    /**
     * This sets isInfected to 'true'
     */
    protected void infectAnimal()
    {
        isInfected = true;
    }
    
    /**
     * This infects other animals around an animal if those other
     * animals aren't infected
     */
    protected void spreadDisease()
    {
        diseaseSpreadProb();
        List<Location> adjacent = getField().adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Animal) {
                Animal adjAnimal = (Animal) animal;
                if (!adjAnimal.isInfected()) {
                    adjAnimal.infect();
                }
            }
        }
    }
    
    /**
     * If the animal is diseased, it tries to infect other animals
     * around it
     * But if the animal isn't diseased, then there's a very small chance
     * it will get the disease
     */
    protected void animalIsDiseased()
    {
        if (this.isInfected()){
            spreadDisease();
        }
        else {
            catchDisease();
        }
    }
    
    /**
     * When animals mate, there's a chance that the disease will spread
     * @param animal partner that may receive disease
     */
    protected void matingDisease(Animal animal)
    {
        if (this.isInfected()){
            if (rand.nextDouble() <= MATING_DISEASE_SPREAD) {
                animal.infectAnimal();
            }
        }
    }
    
    /**
     * There is a low chance that an animal will die if it
     * is diseased
     * @return true if the animal dies
     */
    protected boolean deathByDisease()
    {
        if (isInfected() && rand.nextDouble() <= DISEASE_DEATH_PROBABILITY) {
            setDead();
            return true;
        }
        return false;
    }
}
