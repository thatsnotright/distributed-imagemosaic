package edu.cu.csci5673.caete

class ImageHeaderService {
    /* Arrays control the expansion of the serach to retreive new images
     * They should be the same length.
     * The sum of the elements should not exceed Coordinate.DEGREES_LONGITUDE
     */
    public static List<Integer> LONG_EXPANSION = [1000,10000,100000]
    public static List<Integer> LAT_EXPANSION  = [500,5000,50000]

    static expose=['cxf']

    String ping(){
        return "Web Service contacted."
    }

    /* Save a new image */
    void saveImage(String url, Integer longitude, Integer latitude,
        Integer red, Integer blue, Integer green){

        /*
         * The longitudes in the database appear to be too large by a factor
         * of 10.  Currently DB latitude are bigger than longitude.  This
         * Does not make since as latitude ranges from 90 to -90 and
         * Longitude ranges from 180 to -180.  So longitude should be bigger
         * than latitude on average however it never is.  As a quick fix,
         * I am going to divide by 10 until the DB format is fixed.
         */
        if (latitude>Coordinate.MAX_LATITUDE || latitude<Coordinate.MIN_LATITUDE){
            latitude = latitude / 10
        }

        ImageHeader old = ImageHeader.get(url);
        if (old==null){
            // create a new object as one does not exist in db
            new ImageHeader(url:url,longitude:longitude,latitude:latitude,red:red,blue:blue,green:green).save()
        } else {
            // update old object
            old.setLongitude(longitude);
            old.setLatitude(latitude);
            old.setRed(red);
            old.setBlue(blue);
            old.setGreen(green);
            old.save();
        }
    }

    /* Select images near location (longitude,latitude).
     * The routine will select the numImagesToSelect unless the database
     *      contains fewer items then numImagesToSelect
     * The function will return the URL of the images selected
     */
    Set<ImageHeader> selectImagesNearLocation(Integer longitude, Integer latitude, Integer numImagesToSelect){
        
        /* progressivly select images in larger search areas until
         * numImagesToSelect images are found.
         */
        Set<ImageHeader> results = new HashSet<ImageHeader>()
        Coordinate lowerBounds = new Coordinate(longitude,latitude)
        Coordinate upperBounds = new Coordinate(longitude,latitude)
        for (int pos = 0; pos < LONG_EXPANSION.size-1; pos++) {
            if (results.size()>=numImagesToSelect){
                break; // found enough images
            }
            
            /* Widen search area then query DB
             * Note we never search for a single coordinate.
             * http://grails.org/doc/latest/guide/single.html#5.4%20Querying%20with%20GORM section 5.4.2
             */
            lowerBounds = lowerBounds.add(LONG_EXPANSION[pos]*-1,LAT_EXPANSION[pos]*-1)
            upperBounds = upperBounds.add(LONG_EXPANSION[pos],LAT_EXPANSION[pos])
            if (isCrossingDateBoundry(lowerBounds.getLongitude(), upperBounds.getLongitude())){
                // need two selects as the area crosses the international date line
                selectImages(results, numImagesToSelect,
                    lowerBounds.getLongitude(), Coordinate.MAX_LONGITUDE,
                    lowerBounds.getLatitude(), upperBounds.getLatitude())
                selectImages(results, numImagesToSelect,
                    Coordinate.MIN_LONGITUDE, upperBounds.getLongitude(),
                    lowerBounds.getLatitude(), upperBounds.getLatitude())
            } else {
                selectImages(results, numImagesToSelect,
                    lowerBounds.getLongitude(), upperBounds.getLongitude(),
                    lowerBounds.getLatitude(), upperBounds.getLatitude())
            }
            
        }
        if (results.size()<numImagesToSelect){
            // If we have not found enough images select from all images in DB
            Set<ImageHeader> searchResults = ImageHeader.list(max:numImagesToSelect)
            mergeImages(results, searchResults, numImagesToSelect)
        }
        return results;
    }

    /* execute select */
    private void selectImages(Set<ImageHeader> results, Integer numImagesToSelect,
        Integer westLongitude, Integer eastLongitude,
        Integer southLatitude, Integer northLatitude){
        def critera = ImageHeader.createCriteria()
        Set<ImageHeader> searchResults = critera {
                between("longitude", westLongitude, eastLongitude)
                between("latitude", southLatitude, northLatitude)
                maxResults(numImagesToSelect)
         }
         mergeImages(results, searchResults, numImagesToSelect)
    }

    /* return true if you cross the International date/time boundry when
     * traveling from west to east given the two longitudes.
     */
    private boolean isCrossingDateBoundry(Integer westLongitude, Integer eastLongitude){
        // if west is positive and east is negative
        if (westLongitude>0 && eastLongitude<0){
            return true
        }
        return false
    }
    /*
     * merge addMe with addToMe.  Do not allow the addToMe's size to exceed
     * numImagesToSelect
     */
    private void mergeImages(Set<ImageHeader> addToMe, Set<ImageHeader> addMe, Integer numImagesToSelect){
        if (addToMe==null){
            addToMe = new HashSet<ImageHeader>()
        }

        if (addMe!=null){
            for (ImageHeader header : addMe){
                if (addToMe.size()>=numImagesToSelect){
                    break; // Enough images found stop adding images
                }
                addToMe.add(header)
            }
        }
    }
}
