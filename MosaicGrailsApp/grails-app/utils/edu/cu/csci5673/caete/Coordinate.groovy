
package edu.cu.csci5673.caete

/* Static class to make dealing with long/lat easier */
class Coordinate {
    public static final int MIN_LATITUDE  = -90000000
    public static final int MAX_LATITUDE  =  90000000
    public static final int MIN_LONGITUDE = -180000000
    public static final int MAX_LONGITUDE = 180000000
    private static final int DEGREES_LONGITUDE = 360000000

    private Integer longitude  // getter no setters
    private Integer latitude

    public Coordinate(Integer longitude, Integer latitude){
        if (MIN_LONGITUDE > longitude || longitude > MAX_LONGITUDE){
            throw new IllegalArgumentException("invalid longitude :"+longitude)
        }
        if (MIN_LATITUDE > latitude || latitude > MAX_LATITUDE){
            throw new IllegalArgumentException("invalid latitude :"+latitude)
        }
        this.longitude = longitude
        this.latitude = latitude
    }

    /* Add long/lat to this Coordinate returning results in new coordinate)*/
    public Coordinate add(Integer longitudeToAdd, Integer latitudeToAdd){
        return new Coordinate(addLongitude(longitudeToAdd),addLatitude(latitudeToAdd))
    }

    /*
     * Add to longitude. Wrap results that exceed MIN_LONGITUDE or MAX_LONGITUDE
     */
    private Integer addLongitude(Integer longitudeToAdd){
        // Adjust for numbers that circumnavigate the globe
        if (Math.abs(longitudeToAdd)>=DEGREES_LONGITUDE){
            longitudeToAdd = (longitudeToAdd%DEGREES_LONGITUDE)*getSign(longitudeToAdd)
        }
        Integer newLongitude = longitude+longitudeToAdd;

        // Adjust for crossing the date time barrier
        if (newLongitude>MAX_LONGITUDE){
            newLongitude = newLongitude-DEGREES_LONGITUDE
        } else if (newLongitude<MIN_LONGITUDE){
            newLongitude = newLongitude+DEGREES_LONGITUDE
        }
        return newLongitude
    }

    /* return sign of number */
    private Integer getSign(Integer number){
        if (number>=0){
            return 1
        }
        return -1
    }

    /*
     * latitude can be between MIN_LATITUDE and MAX_LATITUDE.
     * Do not wrap results if these values are exceeded
     */
    private Integer addLatitude(Integer latitudeToAdd){
        Integer newLatitude = latitude+latitudeToAdd;
        if (newLatitude>MAX_LATITUDE){
            newLatitude=MAX_LATITUDE;
        }
        if (newLatitude<MIN_LATITUDE){
            newLatitude=MIN_LATITUDE;
        }
        return newLatitude
    }

    // allow user to access not set long/lat
    public Integer getLongitude(){
        return longitude;
    }
    public Integer getLatitude(){
        return latitude;
    }

    @Override
    public String toString(){
        return "long :"+longitude+", lat :"+latitude
    }
}

