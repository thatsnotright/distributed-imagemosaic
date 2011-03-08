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
        removeAllImageHeaders();
        ImageHeaderService service = new ImageHeaderService()


        service.saveImage("URL",2,2,2,2,2)
        service.saveImage("URL2",2,2,2,2,2)
        Set<ImageHeader> results = service.selectImagesNearLocation(2,2,100)
        assertEquals 2,results.size()
        results = service.selectImagesNearLocation(2,2,1)
        assertEquals 1,results.size()
        removeAllImageHeaders()
        results = service.selectImagesNearLocation(2,2,100)
        assertEquals 0,results.size()
    }

    /* clear all items from ImageHeader table */
    private void removeAllImageHeaders(){
       Set<ImageHeader> clearAll = ImageHeader.list()
       for (ImageHeader ih : clearAll){
           ih.delete()
       }
    }
}
