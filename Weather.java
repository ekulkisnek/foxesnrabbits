import java.util.Random;
/**
 * A simple weather system
 * The different weather types are rain and droughts
 * 
 * @author Mohammad Talal Hassan and Luke Kensik
 */
public class Weather
{
    // probability of rain
    private static final double RAIN_PROBABILITY = 0.02;
    // probability of droughts
    private static final double DROUGHT_PROBABILITY = 0.007;
    // The max duration of a weather condition
    private static final int MAX_STEPS_WEATHER = 36;
    
    private static final Random rand = Randomizer.getRandom();
    
    private boolean isRaining;
    
    private boolean isDrought;
    
    private int count;
    // Keeps track of how long a weather condtion should last
    private int weatherSteps;

    /**
     * Sets the weather so that there are normal conditions
     */
    public Weather()
    {
        resetWeather();
    }
    
    /**
     * If there are no other current weather conditions, there is a
     * random chance it will rain or there will be a drought
     */
    public void generateWeather()
    {
        if (rand.nextDouble() <= RAIN_PROBABILITY) {
            isRaining = true;
            // determines how long the weather will last
            weatherSteps = rand.nextInt(MAX_STEPS_WEATHER) +1; // +1 prevents 0 being generated,
                    // which would cause an eternal rain (in this case)
            return;
        }
        else if (rand.nextDouble() <= DROUGHT_PROBABILITY){
            isDrought = true;
            weatherSteps = rand.nextInt(MAX_STEPS_WEATHER) + 1;
            return;
        }

    }
    
    /**
     * Checks whether there are any current weather condtions
     * @return true if there are any weather conditions, otherwise false
     */
    public boolean checkWeather()
    {
        if (isDrought || isRaining) {
            return true;
        }
        return false;
    }
    
    /**
     * If there are no weather conditions , there's a chance it will change.
     * Otherwise, if there already is a weather conditon, it determines how long 
     * it will last before it resets to normal conditions
     */
    public void simulateWeather()
    {
        if (!checkWeather()) {
            generateWeather();
        }
        else {
            count++;
            if (count == weatherSteps) {
                resetWeather();
            }
        }
        
    }
    
    /**
     * Resets the weather back to normal conditions
     */
    private void resetWeather()
    {
         isRaining = false;
         isDrought = false;
         count = 0;
         weatherSteps = 0;
    }
     
    /**
     * Returns a string based on the current weather
     * E.g if it's raining it will return "Rain"
     * @return A string of the current weather type e.g "rain"
     */
    public String getCurrentWeatherString()
    {
        if (!checkWeather()) {
            return "Normal";
        }
        else if (isRaining) {
            return "Rain";
        }
        else if (isDrought) {
            return "Drought";
        }
        return null;
    }
    
    /**
     * Returns isRaining
     * @return returns true if it's raining
     */
    public boolean getIsRaining()
    {
        return isRaining;
    }
    
    /**
     * Returns isDrought
     * @return Returns true if there's a drought
     */
    public boolean getIsDrought()
    {
        return isDrought;
    }
    
    /**
    Returns the count of number of days there has been weather
    @return Returns number of days of weather so far as int
    */
    
    public int getCount()
    {
        return count;
    }
    
    /**
     * Returns the duration of the current weather condition
     * @return Returns how many steps weather will last
     */
    public int getWeatherSteps()
    {
        return weatherSteps;
    }    
}
