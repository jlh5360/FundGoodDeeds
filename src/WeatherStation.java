/*
 * Initial Author
 *      Michael J. Lutz
 *
 * Other Contributers
 *
 * Acknowledgements
 */
 
/*
 * Class for a simple computer based weather station that reports the current
 * temperature (in Celsius) every second. The station is attached to a
 * sensor that reports the temperature as a 16-bit number (0 to 65535)
 * representing the Kelvin temperature to the nearest 1/100th of a degree.
 *
 * This class is implements Runnable so that it can be embedded in a Thread
 * which runs the periodic sensing.
 *
 * The class also extends Observable so that it can notify registered
 * objects whenever its state changes. Convenience functions are provided
 * to access the temperature in different schemes (Celsius, Kelvin, etc.)
 */
import java.util.Observable ;

public class WeatherStation extends Observable implements Runnable {

    private final ITempSensor sensor ; // Temperature sensor.
    private final IBarometer barometer ;     // Atmospheric pressure.

    private final long PERIOD = 1000 ;      // 1 sec = 1000 ms

    private double currentCelsius ;
    private double currentPressure ;

    /*
     * When a WeatherStation object is created, it in turn creates the sensor
     * object it will use.
     */
    public WeatherStation(IBarometer barometer, ITempSensor sensor) {
        this.sensor = sensor;
        this.barometer = barometer ;
    }

    /*
     * The "run" method called by the enclosing Thread object when started.
     * Repeatedly sleeps a second, acquires the current temperature from its
     * sensor, and notifies registered Observers of the change.
     */
    public void run() {
        while( true ) {
            try {
                Thread.sleep(PERIOD) ;
            } catch (Exception e) {}    // ignore exceptions

            /*
             * Get next reading and notify any Observers.
             */
            synchronized(this) {
                currentCelsius = sensor.getCelsius();
                currentPressure = barometer.pressure();
            }
            setChanged() ;
            notifyObservers() ;
        }
    }

    /*
     * Return the current reading in degrees celsius as a
     * double precision number.
     */
    public synchronized double getCelsius() {
        return currentCelsius;
    }

    /*
     * Return the current reading in degrees Kelvin as a
     * double precision number.
     */
    public synchronized double getKelvin() {
        return getCelsius() + 273.15;
    }

    /*
     * Return the current reading in degrees Fahrenheit as a
     * double precision number.
     */
    public synchronized double getFahrenheit() {
        //Celsius to Fahrenheit conversion formula        
        return (getCelsius() * 9/5 ) + 32;
    }

    /*
     * Return the current pressure in inches of mercury as a
     * double precision number.
     */
    public synchronized double getInches() {
        return currentPressure ;
    }

    /*
     * Return the current pressure in millibars as a
     * double precision number.
     */
    public synchronized double getMillibars() {
        return currentPressure * 33.864 ;
    }
}
