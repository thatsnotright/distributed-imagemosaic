/************************************************
 *  plugins/scalarEvaluator.java
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
 * This is the default evaluation class.  It takes
 * a list of images, and finds a simple mean RBG
 * value for each image, using JAI's "mean" function.
 ************************************************/

package plugins;

import imageUtils.*;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.RenderedImage;
import java.awt.image.Raster;
import javax.media.jai.JAI;
import java.awt.Color;

public class scalarEvaluator extends evaluatorPlugin {

    // Class variables.
    private imageList images;  // The list of images we are evaluating.
    private int current;       // Our current spot in the evaluation task.
    private int lengthOfTask;  // The length of the evaluation task.
    private colorList colors;  // List of colors representing the image list, but with their avg. colors.

    /** Creates new medianEvaluator */
    public scalarEvaluator(imageList il) {
        this.images = il;
        super.setTaskLen(images.getSize());
        current = super.getCurr();
        lengthOfTask = super.getTaskLen();
        colorList colors = new colorList();
    }

/***********************************************************
    DO NOT EDIT THIS UNLESS YOU KNOW WHAT YOU ARE DOING!
************************************************************/
    public void go() {  // Thread stuff.
        current = 0;
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                Evaluate eval = null;
                try {
                    eval =  new Evaluate();
                } catch (Exception e) {
                    System.out.println("Thread Error!");
                    e.printStackTrace();
                }
                if (eval == null) {
                    return null;
                } else {
                    return eval;
                }
            }
        };
        worker.start();
    }
//************************************************************
    class Evaluate {  // overwrite this class to include your algorithm.
                      // This is the actual evaluation algorithm.
      Evaluate() {
        double[] mean;  // The average color array.
        int nextSlot = 0;  // A counter, since not ALL images in the list will be kept.
        RenderedImage img;
        for (int count = 0; count < lengthOfTask; count++) {  // Cycle through all the images in the list...
                try {
                    Thread.sleep(5);
                    img = imageLoader.getRenderedImage(images.getName(count));  // Load the current image.
                    if (img != null) {
                        scalarImageInfo newImg = new scalarImageInfo();         // Set up an imageInfo record with the data.
                        newImg.setFileName(images.getName(count));
                        newImg.setRasterVal(imageOps.scale(img,64,48).getData());
                        images.replace(nextSlot, newImg);  // Put the record in the next slot in the list.
                        setCurr(count);                    // Update task stuff, for progress indicators.
                        setMsg(images.getName(count));
                        nextSlot++;
                    }
                } catch (Exception e) {  // Debugging.
                    System.out.println("Error on image: " +count);
                    System.out.println("EXCEPTION: " + e.toString());
                    e.printStackTrace();
                }
            }
            for (nextSlot = nextSlot; nextSlot < lengthOfTask-1; nextSlot++) {  // Clean up the list, to take care of
                images.remove(nextSlot);                                        // Images not included.
            }
        setCurr(lengthOfTask);
        setMsg("DONE!");
        System.out.println("Done!");
        }
    }
}