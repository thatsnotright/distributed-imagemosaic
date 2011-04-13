/************************************************
 *  imageUtils/scalarImageInfo.java
 *  Author: Jim Drewes, 2002
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 *
 * imageInfo is simply the record structure for
 * an image/tile stored in an image library.
 * Class is serializable for storage purposes.
 ************************************************/

package imageUtils;

import java.awt.image.Raster;
import java.io.Serializable;

public class scalarImageInfo extends imageInfo implements Serializable {

    // Constructor.
    public scalarImageInfo() {
    }

    // Constructor -- Initializing with image's file name and RBG Value.
    public scalarImageInfo(String fn, Raster rv) {
        this.fileName = fn;
        this.rasterVal = rv;
    }

    // Accessor to set the file name/path for the image.
    public void setFileName(String fn) {
        this.fileName = fn;
    }

    // Accessor to set the RBG value for the image.
    public void setRasterVal(Raster rv) {
        this.rasterVal = rv;
    }

    // Accessor to retrieve the filename for the image.
    public String getFileName() {
        return this.fileName;
    }

    // Accessor to retrieve the RBG value for the image.
    public Raster getRasterVal() {
        return this.rasterVal;
    }

    // Class variables.
    private String fileName;  // The full path and file name for the image.
    private Raster rasterVal;     // The RBG value for the image.

}