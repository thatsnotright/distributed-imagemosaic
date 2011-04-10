package edu.cu.csci5673.caete

import grails.test.*

class CoordinateTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testConstructor(){
        // make sure only coordinates with valid long/lat are accepted
        new Coordinate(Coordinate.MAX_LONGITUDE,Coordinate.MAX_LATITUDE)
        buildIllegalCoordinate(Coordinate.MAX_LONGITUDE+1,Coordinate.MAX_LATITUDE)
        buildIllegalCoordinate(Coordinate.MAX_LONGITUDE,Coordinate.MAX_LATITUDE+1)

        new Coordinate(Coordinate.MIN_LONGITUDE,Coordinate.MIN_LATITUDE)
        buildIllegalCoordinate(Coordinate.MIN_LONGITUDE-1,Coordinate.MIN_LATITUDE)
        buildIllegalCoordinate(Coordinate.MIN_LONGITUDE,Coordinate.MIN_LATITUDE-1)
    }

    /* create a coordinate object with illegal parameter */
    private void buildIllegalCoordinate(Integer longitude, Integer latitude){
        try {
           new Coordinate(longitude,latitude)
           assert fail("Constructed invalid coordinate "+c)
        } catch (IllegalArgumentException e){
            // expected
        }
    }

    void testAddLatitude() {
        // make sure latitude addition does not go past poles
        Coordinate c = new Coordinate(0,Coordinate.MAX_LATITUDE-5)
        c = c.add(0,10)
        assertEquals Coordinate.MAX_LATITUDE,c.getLatitude()
        c = new Coordinate(0,Coordinate.MIN_LATITUDE+5)
        c = c.add(0,-10)
        assertEquals Coordinate.MIN_LATITUDE,c.getLatitude()

        // make sure going past equater works
        c = new Coordinate(0,-4)
        c = c.add(0,10)
        assertEquals 6,c.getLatitude()
    }

    void testAddLongitude() {
        assertEquals 150000000,addLongitude(Coordinate.MAX_LONGITUDE,-30000000)
        assertEquals new Integer(-3000000),addLongitude(0,-3000000)
        assertEquals 150000000,addLongitude(Coordinate.MIN_LONGITUDE,-30000000)  // Cross dt/tm boundry
        assertEquals new Integer(-150000000),addLongitude(Coordinate.MAX_LONGITUDE,+30000000) // Cross dt/tm boundry
        assertEquals 3000000,addLongitude(0,3000000)
        assertEquals new Integer(-150000000),addLongitude(Coordinate.MIN_LONGITUDE,30000000)
        assertEquals 0,addLongitude(0,360000000) // around the world
    }

    private Integer addLongitude(Integer startValue, Integer adjustment){
        Coordinate c = new Coordinate(startValue,0)
        c = c.add(adjustment,0)
        return c.getLongitude()
    }
}