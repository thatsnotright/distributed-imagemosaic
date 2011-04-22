package edu.cu.csci5673.caete

import grails.test.*

class ImageHeaderServiceIntegrationTests extends GroovyTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSimpleSelectImagesNearLocation() {
        // Add some test data
        removeAllImageHeaders();        // cleanup previous data
        ImageHeaderService service = new ImageHeaderService()
        service.saveImage("URL",2,2,2,2,2)
        service.saveImage("URL2",2,2,2,2,2)

        // make sure we respect numImagesToSelect param
        Set<ImageHeader> results = service.selectImagesNearLocation(2,2,100)
        assertEquals 2,results.size()                       // select all images
        results = service.selectImagesNearLocation(2,2,1)
        assertEquals 1,results.size()                       // select 1 image
        results = service.selectImagesNearLocation(2,2,0)
        assertEquals 0,results.size()                       // select no images
        results = service.selectImagesNearLocation(2,2,-1)
        assertEquals 0,results.size()                       // select no images

        // Test out of bounds input params
        try{
            service.saveImage("URL",Coordinate.MAX_LONGITUDE+1,2,2,2,2)
            fail("Invalid longitude")
        } catch (Exception e) {
            // exception expected
        }

        try{
            service.saveImage("URL",3,Coordinate.MIN_LATITUDE-1,2,2,2)
            fail("Invalid latitude")
         } catch (Exception e) {
            // exception expected
        }
    }

    /* test searching for images using longitude */
    void testSelectImagesNearLocationLongitudeRangeExpansion() {
        // Add some test data
        Integer base = 0;
        removeAllImageHeaders();        // cleanup previous data
        ImageHeaderService service = new ImageHeaderService()
        service.saveImage("firstSelect",base,0,2,2,2)
        service.saveImage("secondSelect",getCoordInSelect(base,true,2,true),0,2,2,2)
        service.saveImage("finalSelect",getCoordInSelect(base,true,99,false),0,2,2,2)

        Set<ImageHeader> results = service.selectImagesNearLocation(base,0,1)
        assertContainsAllUrls(results, "firstSelect")
        results = service.selectImagesNearLocation(base,0,2)
        assertContainsAllUrls(results, "firstSelect","secondSelect")
        results = service.selectImagesNearLocation(base,0,3)
        assertContainsAllUrls(results, "firstSelect","secondSelect","finalSelect")
    }

    /* test searching for images using latitude */
    void testSelectImagesNearLocationLatitudeRangeExpansion() {
        // Add some test data
        Integer base = 0;
        removeAllImageHeaders();        // cleanup previous data
        ImageHeaderService service = new ImageHeaderService()
        service.saveImage("firstSelect",0,base,2,2,2)
        service.saveImage("secondSelect",0,getCoordInSelect(base,false,2,false),2,2,2)
        service.saveImage("finalSelect",0,getCoordInSelect(base,true,99,true),2,2,2)

        Set<ImageHeader> results = service.selectImagesNearLocation(0,base,1)
        assertContainsAllUrls(results, "firstSelect")
        results = service.selectImagesNearLocation(0,base,2)
        assertContainsAllUrls(results, "firstSelect","secondSelect")
        results = service.selectImagesNearLocation(0,base,3)
        assertContainsAllUrls(results, "firstSelect","secondSelect","finalSelect")
    }

    /* test searching for images using longitude AND wrapping around the date/time line*/
    void testSelectImagesNearLocationInternationalDateTimeBoundryAsian() {
        // Add some test data
        Integer base = Coordinate.MAX_LONGITUDE-2; // just on asian side of boundry
        removeAllImageHeaders();        // cleanup previous data
        ImageHeaderService service = new ImageHeaderService()
        service.saveImage("finalSelectPositive",getCoordInSelect(base,true,99,true),0,2,2,2)
        service.saveImage("finalSelectNegative",getCoordInSelect(base,true,99,false),0,2,2,2)
        service.saveImage("firstSelect",base,0,2,2,2)
        service.saveImage("secondSelectPositive",getCoordInSelect(base,true,2,true),0,2,2,2)
        service.saveImage("secondSelectNegative",getCoordInSelect(base,true,2,false),0,2,2,2)
        
        // should select the three items closest to the boundry.  
        Set<ImageHeader> results = service.selectImagesNearLocation(base,0,3)
        assertContainsAllUrls(results, "firstSelect","secondSelectPositive","secondSelectNegative")
    }

    /* test searching for images using longitude AND wrapping around the date/time line*/
    void testSelectImagesNearLocationInternationalDateTimeBoundryAmerican() {
        // Add some test data
        Integer base = Coordinate.MIN_LONGITUDE+2; // just on american side of boundry
        removeAllImageHeaders();        // cleanup previous data
        ImageHeaderService service = new ImageHeaderService()
        service.saveImage("finalSelectPositive",getCoordInSelect(base,true,99,true),0,2,2,2)
        service.saveImage("finalSelectNegative",getCoordInSelect(base,true,99,false),0,2,2,2)
        service.saveImage("firstSelect",base,0,2,2,2)
        service.saveImage("secondSelectPositive",getCoordInSelect(base,true,2,true),0,2,2,2)
        service.saveImage("secondSelectNegative",getCoordInSelect(base,true,2,false),0,2,2,2)

        // should select the three items closest to the boundry.
        Set<ImageHeader> results = service.selectImagesNearLocation(base,0,3)
        assertContainsAllUrls(results, "firstSelect","secondSelectPositive","secondSelectNegative")
    }

    /*
     *  get a coordinate that falls in given search expansion range 
     *  baseCoord = center of search
     *  longitudeExpansion = if true then use ImageHeaderService.LONG_EXPANSION else ImageHeaderService.LAT_EXPANSION
     *  selectRound = Round you want to select in (1 based)
     *  positive = true if you want to move coordinate "after" baseCoord 
     */
    private Integer getCoordInSelect(int baseCoord, boolean longitudeExpansion, int selectRound, boolean positive){
        // determine if we want to move coordinate forward or backwards
        int sign = 1
        if (!positive){sign = -1}

        // determine which expansion to use
        List<Integer> expansion = ImageHeaderService.LONG_EXPANSION
        if (!longitudeExpansion){
            expansion = ImageHeaderService.LAT_EXPANSION
        }

        // determine coordinate adjustment.
        int index = selectRound-2
        int adjustment = 1*sign
        if (expansion.size()>index){
            adjustment += (expansion.getAt(index)*sign) // if selectRound in bounds
        } else {// if selectRound index out of bounds put last
            adjustment += (expansion.getAt(expansion.size()-1)*sign)
        }

        Coordinate coord = null
        Integer retVal = 0
        if (longitudeExpansion){
            coord = new Coordinate(baseCoord,0)
            coord = coord.add(adjustment, 0)
            retVal = coord.getLongitude()
        } else {
            coord = new Coordinate(0,baseCoord)
            coord = coord.add(0, adjustment)
            retVal = coord.getLatitude()
        }
        return retVal
    }

    /* make sure results contain all urls in list */
    private void assertContainsAllUrls(Set<ImageHeader> results, String... expectedUrls){
        assertNotNull results
        Set<String> actualUrls = new ArrayList<String>()
        results.each {
            actualUrls.add(it.getUrl())
        }
        String msg = "Actual   : "+actualUrls+" Expected :"+expectedUrls
        assertEquals msg,results.size(),expectedUrls.length // lists must be same length
        assertTrue msg,actualUrls.containsAll(expectedUrls)
    }

    /* clear all items from ImageHeader table */
    private void removeAllImageHeaders(){
       Set<ImageHeader> clearAll = ImageHeader.list()
       for (ImageHeader ih : clearAll){
           ih.delete()
       }
    }
}
