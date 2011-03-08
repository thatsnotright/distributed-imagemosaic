package edu.cu.csci5673.caete

class ImageHeaderService {
    /* Arrays control the expansion of the serach to retreive new images
     * They should be the same length.
     * The sum of the elements should not exceed Coordinate.DEGREES_LONGITUDE
     */
    protected int[] LONG_EXPANSION = [1000,10000,100000] 
    protected int[] LAT_EXPANSION  = [500,5000,50000]

    static expose=['cxf']

//    boolean saveImage(ImageHeader saveMe){
//        // TODO save me is null in this signature.
//        Id and version are also added to the wsdl
//        saveMe.save()
//        return false
//    }

    /* Save a new image */
    void saveImage(String url, Integer longitude, Integer latitude,
        Integer red, Integer blue, Integer green){
        new ImageHeader(url:url,longitude:longitude,latitude:latitude,red:red,blue:blue,green:green).save()
    }

    /* Select images near location (longitude,latitude).
     * The routine will select the numImagesToSelect unless the database
     *      contains fewer items then numImagesToSelect
     * The function will return the URL of the images selected
     */
    Set<ImageHeader> selectImagesNearLocation(Integer longitude, Integer latitude, Integer numImagesToSelect){
        
        return ImageHeader.list(max:numImagesToSelect)


//        Set<ImageHeader> results = new HashSet<ImageHeader>()
//        /* progressivly select images in larger search areas until
//         * numImagesToSelect images are found.
//         */
//        Coordinate oldUpperBounds = new Coordinate(longitude,latitude)
//        Coordinate oldLowerBounds = new Coordinate(longitude,latitude)
//        for (int pos = 0; pos < LONG_EXPANSION.length()-1; pos++) {
//            if (numImagesToSelect<=0){
//                break; // found enough images
//            }
//
//            // update maximum to widen search
//            Coordinate newUpperBounds = oldUpperBounds.add(LONG_EXPANSION[pos],LAT_EXPANSION[pos])
//            Coordinate newLowerBounds = oldUpperBounds.add(LONG_EXPANSION[pos]*-1,LAT_EXPANSION[pos]*-1)
//
//            // TODO IMPLEMENT SEARCH
//            // to query http://grails.org/doc/latest/guide/single.html#5.4%20Querying%20with%20GORM section 5.4.2
//
//            // update minimums so we do not reselect old images.
//            oldUpperBounds = newUpperBounds
//            oldUpperBounds = newLowerBounds
//        }
//        return results;
    }
}
