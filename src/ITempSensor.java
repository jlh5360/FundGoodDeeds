public interface ITempSensor {
        // get current temp in degrees celsius
    public double getCelsius();
    
    //get temp in farenheit
    public double getFarenheit();
    
    //get temp in kelvin
    public double getKelvin();

    public int reading();
}
