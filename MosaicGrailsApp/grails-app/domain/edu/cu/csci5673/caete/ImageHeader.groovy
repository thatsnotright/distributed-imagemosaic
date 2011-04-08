package edu.cu.csci5673.caete

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
class ImageHeader {
    /* make the url column the primary key of the table.
     * The value must be assigned by the application (not auto generated) */
    static mapping = {
        id generator:'assigned', name:'url'
    }

    String  url=""
    Integer longitude
    Integer latitude 
    Integer red
    Integer blue
    Integer green
}
