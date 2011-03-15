package edu.cu.csci5673.caete

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
class ImageHeader {
    // TODO see if this can be the DB key
    String  url
    Integer longitude
    Integer latitude 
    Integer red
    Integer blue
    Integer green
}
