import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single animal and a number of Krill in it.
 * Also keeps track of day and night and weather conditions affect the growth
 * of krill
 * 
 * @author Mohammad Talal Hassan and Luke Kensik
 */
public class Field
{
    // Maximum number of krill in a postion
    private static final int MAX_KRILL = 40;
    // The rate at which the number of krill increases per step
    private static final int GROWTH_RATE = 2;
    // The number of krill in every position in the beginning
    private static final int STARTING_NUM_OF_KRILL = 20;
    // Probability of krill growing
    private static final double GROWTH_PROBABILTY = 0.80;
    
    private static final double RAIN_GROWTH_PROBABILTY = 0.99;
    
    private static final double DROUGHT_GROWTH_PROBABILTY = 0.10;
        
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    
    // The depth and width of the field.
    private int depth, width;
    // Storage for the animals.
    private Object[][] field;
    // Storage for the number of krill in a position
    private int[][] krill;
    // Keeps track of day and night
    private boolean isDay;
    
    private Weather weather;

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        field = new Object[depth][width];
        krill = new int[depth][width];
        weather = new Weather();
        populateKrill(depth, width);
        isDay= true;
    }
    
    /**
     * Calls simulateWeather method of weather class
     * If no current state of weather has random chance weather occurs
     * Otherwise checks if weather is due to end and if so, ends it
     */
    public void updateWeather()
    {
        weather.simulateWeather();        
    }
    
    /**
     * Returns the weather of the field
     * @return the weather of the field
     */
    public Weather getWeather()
    {
        return weather;
    }
    
    /**
     * Toggles between day and night
     */
    public void toggleDay()
    {
        isDay = !isDay;
    }
    
    /**
     * Returns true if it's daytime
     * @return true if it's day
     */
    public boolean getIsDay()
    {
        return isDay;
    }
    
    /**
     * Empty the field.
     */
    public void clear()
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                field[row][col] = null;
            }
        }
    }
    
    /**
     * Assigns a random number of krill to each position in the field
     * @param depth of field 
     * @param width of field 
     */
    private void populateKrill(int depth, int width)
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                krill[row][col] = STARTING_NUM_OF_KRILL;
            }
        }
    }
    
    /**
     * Resets
     */
    public void resetKrill()
    {
        populateKrill(getDepth(), getWidth());
    }
    
    /**
     * Checks the current weather and then grows krill in each position
     * based on the current weather
     */
    public void growKrill()
    {
        if (!weather.checkWeather()) {
            growKrill(GROWTH_PROBABILTY);
        }
        else if (weather.getIsRaining()) {
            growKrill(RAIN_GROWTH_PROBABILTY);
        }
        else if (weather.getIsDrought()) {
            growKrill(DROUGHT_GROWTH_PROBABILTY);
        }
    }
    
    /**
     * Grows krill in each position determined by the probabilty passed into
     * the parameter
     * @param krill grow probability as double
     */
    public void growKrill(double probabilty)
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                if (rand.nextDouble() <= probabilty && krill[row][col] < MAX_KRILL){
                    krill[row][col] += GROWTH_RATE;                    
                }
                if (krill[row][col] > MAX_KRILL) {
                        krill[row][col] = MAX_KRILL;
                    }
            }
        }
    }
    
    /**
     * Checks how much krill is available in a position and returns 
     * how much was requested if no.ofKrill>=appetite or the remaining 
     * krill in that position
     * @param the number of krill you want to eat
     * @param location of krill to eat
     * @return a number of krill in a position in the field
     */
    public int eatKrill(int appetite, Location location)
    {
        int krill = getKrillAt(location);
        eatenKrill(appetite, location);
        if (krill > 0) {
            if (appetite > krill){
                eatenKrill(krill, location);
                return krill;
            }
            else {
                eatenKrill(appetite, location);
                return appetite;
            }
        }
        return 0;        
    }
    
    /**
     * Removes the amount of krill that was eaten from specified location
     * @param amount of eaten krill as int
     * @param location of krill as location
     */
    private void eatenKrill(int amount, Location location)
    {
        krill[location.getRow()][location.getCol()] -= amount;
    }

    /**
     * Clear the given location.
     * @param location The location to clear.
     */
    public void clear(Location location)
    {
        field[location.getRow()][location.getCol()] = null;
    }
    
    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * @param animal The animal to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(Object animal, int row, int col)
    {
        place(animal, new Location(row, col));
    }
    
    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * @param animal The animal to be placed.
     * @param location Where to place the animal.
     */
    public void place(Object animal, Location location)
    {
        field[location.getRow()][location.getCol()] = animal;
    }
    
    /**
     * Return the animal at the given location, if any.
     * @param location Where in the field.
     * @return The animal at the given location, or null if there is none.
     */
    public Object getObjectAt(Location location)
    {
        return getObjectAt(location.getRow(), location.getCol());
    }
    
    /**
     * Return the animal at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The animal at the given location, or null if there is none.
     */
    public Object getObjectAt(int row, int col)
    {
        return field[row][col];
    }
    
    /**
     * Return the number of krill at the given location, if any.
     * @param location Where in the field.
     * @return The number of krill at the given location, or null if there is none.
     */
    public int getKrillAt(Location location)
    {
        return krill[location.getRow()][location.getCol()];
    }
    
    /**
     * Return the number of krill at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The number of krill at the given location, or null if there is none.
     */
    public int getKrillAt(int row, int col)
    {
        return krill[row][col];
    }
    
    /**
     * Generate a random location that is adjacent to the
     * given location, or is the same location.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location randomAdjacentLocation(Location location)
    {
        List<Location> adjacent = adjacentLocations(location);
        return adjacent.get(0);
    }
    
    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);
        for(Location next : adjacent) {
            if(getObjectAt(next) == null) {
                free.add(next);
            }
        }
        return free;
    }
    
    /**
     * Try to find a free location that is adjacent to the
     * given location. If there is none, return null.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location freeAdjacentLocation(Location location)
    {
        // The available free ones.
        List<Location> free = getFreeAdjacentLocations(location);
        if(free.size() > 0) {
            return free.get(0);
        }
        else {
            return null;
        }
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location)
    {
        assert location != null : "Null location passed to adjacentLocations";
        // The list of locations to be returned.
        List<Location> locations = new LinkedList<>();
        if(location != null) {
            int row = location.getRow();
            int col = location.getCol();
            for(int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            
            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }
}
