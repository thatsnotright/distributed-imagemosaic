/************************************************
 *  imageUtils/imageLoader.java
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
 * This class provides basic image loading functionality.
 * It handles all of the errors, and based on a file
 * name and path, it will return either a BufferedImage,
 * RenderedImage, or just an ImageReader.
 * Uses the IIO API to better handle loading errors and bad files.
 ************************************************/

package imageUtils;

import java.awt.image.*;
import java.io.File;
import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.event.*;
import java.util.Iterator;

public class imageLoader {

    // NO DOCUMENTATION FOR SUBCLASS -- Not currently used in this version of JImage Mosaic.
    private static class MyReadProgressListener implements IIOReadProgressListener {
        private static float percentComplete = 0.0F;
        private static boolean doneLoading = false;

        public void imageProgress(javax.imageio.ImageReader imageReader, float param) {
            this.percentComplete = param;
        }

        public void readAborted(ImageReader source) {}
        public void imageComplete(ImageReader source) { this.doneLoading = true;}
        public void imageStarted(ImageReader source, int a) { this.doneLoading = false;}
        public void imageAborted(ImageReader source) {}
        public void sequenceComplete(ImageReader source) {}
        public void sequenceStarted(ImageReader source, int a) {}
        public void thumbnailComplete(ImageReader source) {}
        public void thumbnailProgress(ImageReader source, float a) {}
        public void thumbnailStarted(ImageReader source, int a, int b) {}

        public static float getPercentComplete() {
            return percentComplete;
        }
        
        public static boolean getDoneLoading() {
            return doneLoading;
        }
         
    }
    // END NO DOCUMENTATION!

   // Class MyWarningListener.
   // Implements the IIOReadWarningListener to handle/log any errors
   // encountered while loading images.
   static class MyWarningListener implements IIOReadWarningListener {
        public String fileName;

        // Constructor.
        public MyWarningListener() {
            this.fileName = null;
        }

        // Constructor, initializing fileName.
        public MyWarningListener(java.lang.String str) {
            this.fileName = str;
        }

        // The warningOccured method - Gets called when imageReader finds
        // a problem with an image (truncated, corrupt, etc.)
        // For now, we just display a warning to the Console.
        public void warningOccurred(javax.imageio.ImageReader imageReader, java.lang.String str) {
            System.out.println("MyWarningListener found a potential problem!!!!");
            System.out.println("Image Name: " + this.fileName);
            System.out.println("Problem Description: " + str);
        }
   }
   
   // getReader -- Instantiates an ImageReader with a file name/path.
   // For now, it only handles JPEG files.  Listens for errors, and
   // returns the reader.
   private static ImageReader getReader(String fileName) {
    try {
        File source = new File(fileName);  // Create a File from the path string.
        Iterator readers = ImageIO.getImageReadersByFormatName("jpeg");  // Read JPEGs.
        ImageReader reader = (ImageReader)readers.next();
        ImageInputStream iis = ImageIO.createImageInputStream(source);  // Create the input stream.
        reader.setInput(iis, true);                                     // And assign it to the reader.
        IIOReadWarningListener rwl = new MyWarningListener(fileName);   // Set up the listeners...
        mrpl = new MyReadProgressListener();
        reader.addIIOReadProgressListener(mrpl);
        reader.addIIOReadWarningListener(rwl);
        return reader;                                                  // Give back the reader.
    } catch (java.io.IOException ioe) {
         System.out.println("There was an error loading the image: " + fileName);  // Debugging.
         return null;
    }
   }

   // getRenderedImage -- Returns a RenderedImage from a reader, based on the
   // path/filename specified in fileName.
   // Generally, use this method if you want to do JAI routines on the image later.
   public static RenderedImage getRenderedImage(String fileName) {
        RenderedImage img;
        ImageReader reader = getReader(fileName);  // Load up the image.
        try {
            img = reader.readAsRenderedImage(0, null);  // Try to assign the RenderedImage to the output image.
        } catch (java.io.IOException ioe) {
            System.out.println("There was an error loading the image: " + fileName);  // Debugging.
            return null;
        }
        reader.dispose();  // Get rid of the image reader, to avoid memory leaks.
        return img;        // Return the output image (either an image, or NULL).
    }

   // getBufferedImage -- Returns a BufferedImage from a reader, based on the
   // path/filename specified in fileName.
   // Generally, use this method if you are not doing advanced imaging functions.
    public static BufferedImage getBufferedImage(String fileName) {
        BufferedImage img;
        ImageReader reader = getReader(fileName);   // Load up the image.
        try {
            img = reader.read(0);    // Try to assign the BufferedImage to the output image.
        } catch (java.io.IOException ioe) {
            System.out.println("There was an error loading the image" + fileName); // Debugging.
            return null;
        }
        reader.dispose();  // Get rid of the image reader, to avoid memory leaks.
        return img;        // Return the output image (either an image, or NULL.)
    }

    // getReaderOnly -- If you want to do other imageReader functions other
    // than just image loading.  (i.e. monitoring, etc.)
    public static ImageReader getReaderOnly(String fileName) {
        return getReader(fileName);
    }

/*********  Following 2 methods not used in this version of JImage Mosaic. **********/
    public static float getReadProgress() {
        return mrpl.getPercentComplete();
    }

    public static boolean getDone() {
        return mrpl.getDoneLoading();
    }
/************************************************************************************/

    private static MyReadProgressListener mrpl;  // Not used in this version.
}