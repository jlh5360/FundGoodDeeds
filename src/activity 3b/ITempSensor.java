//Author: Connor Bashaw
//date: 9/29/25
//ItempSensor.java

/**
 * general temp sensor, that any concree temperature sensor, can implement or be adapted to
 * values and returned in Celsuis which is a double, like in the requested weatherstation.
 */

public interface ITempSensor {

    // get current temp in degrees celsius
    public double getCelsius();

}
