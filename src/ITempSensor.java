//Author: Connor Bashaw
//date: 9/29/25
//ItempSensor.java

/**
 * general temp sensor, that any concree temperature sensor, can implement or be adapted to
 * values are returned in doubles, in the requested temp scale.
 */

public interface ITempSensor {

    // get current temp in degrees celsius
    double getCelsius();
    
    //get temp in farenheit
    double getFarenheit();
    
    //get temp in kelvin
    double getKelvin();

}
