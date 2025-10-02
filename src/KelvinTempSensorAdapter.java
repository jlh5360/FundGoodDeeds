//Author: Connor Bashaw
//date: 9/29/25
//KelvinTempSensorAdapter.java

/** Adapter that wraps kelvinTempSensor and exposes to general temp sensor.
 * Assumptions (based on the WeatherStation code):
 *  - KelvinTempSensor.reading() returns an integer: Kelvin * 100 (i.e., centi-Kelvin).
 *  - Celsius = Kelvin - 273.15
 */

public class KelvinTempSensorAdapter implements ITempSensor {
    private final KelvinTempSensor adaptee;

    //conversion constants
    private static final int KTOC = -27315; 
    private static final double HUNDRED = 100.0;
    
    public KelvinTempSensorAdapter(KelvinTempSensor adaptee){
        if (adaptee == null) {
            throw new IllegalArgumentException("Adaptee KelvinTempSensor cannot be null");
        }
        this.adaptee = adaptee;
    }

    @Override
    public double getCelsius() {
        // ( kelvin + KTOC) / 100
        return ((adaptee.reading() + KTOC) / HUNDRED);
    }


}
