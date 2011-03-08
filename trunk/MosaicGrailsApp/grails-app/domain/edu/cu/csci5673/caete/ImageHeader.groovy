package edu.cu.csci5673.caete

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
class ImageHeader {
    // TODO see if this can be the DB key
    String  url
    // TODO figure out who converts
    Integer longitude
    Integer latitude
    // TODO is this the correct format. 
    Integer red
    Integer blue
    Integer green
}
