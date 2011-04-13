/************************************************
 *  plugins/saveTypes/BMPSaveType.java
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
 * The BMPSaveType class is used to make saving to BMPs
 * easy inside the program, rather than having to program
 * special code for each codec.
 * (BMP is the only one right now, because BMPs are really easy to encode, because they suck.)
 *
 * TO DO:
 * Add extention adding, if it is left off in the saveImageToFile method.
 ************************************************/

package plugins.saveTypes;

import plugins.saveTypePlugin;
import javax.media.jai.TiledImage;
import com.sun.media.jai.codec.BMPEncodeParam;
import java.io.FileOutputStream;
import javax.media.jai.JAI;

public class BMPSaveType extends saveTypePlugin {

    // Constructor -- I may remove this later.
    public BMPSaveType() {
    }

    // Constructor -- I may remove this later.
    public BMPSaveType(TiledImage outputImage, String fileName) {
        this.outImg = outputImage;
        this.outFile = fileName;
    }

    // saveImageToFile -- Takes a TiledImage, and saves it to
    // a file specified by the path/filename of fileName.
    public boolean saveImageToFile(TiledImage outputImage, String fileName) {
        try {
            FileOutputStream stream = new FileOutputStream(fileName);  // Create the output stream.
            BMPEncodeParam param = new BMPEncodeParam();               // Encode the image.
            JAI.create("encode", outputImage, stream, "BMP", param);
            JAI.create("filestore", outputImage, fileName, "BMP", null);  // Store it.
            return true;    // Success!
        } catch (java.io.FileNotFoundException fnfe) { return false;}  // If the file can't be save to that path.
    }

    // This just calls the saveImageToFile method if an image is already stored in a BMPSaveType object.
    public boolean saveImageToFile() {
        if (outImg == null || outFile == null) {
            return false;
        } else {
            return (saveImageToFile(outImg, outFile));
        }
    }

    // Class variables.
    private TiledImage outImg;
    private String outFile;

}