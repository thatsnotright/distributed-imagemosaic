/************************************************
 *  plugins/medianEvaluator.java
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
import javax.media.jai.JAI;
import java.awt.Color;

public class medianEvaluator extends evaluatorPlugin {

    // Class variables.
    private imageList images;  // The list of images we are evaluating.
    private int current;       // Our current spot in the evaluation task.
    private int lengthOfTask;  // The length of the evaluation task.
    private colorList colors;  // List of colors representing the image list, but with their avg. colors.

    /** Creates new medianEvaluator */
    public medianEvaluator(imageList il) {
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
        for (int count = 0; count < lengthOfTask; count++) {  // Cycle through all the images in the list...
                try {
                    Thread.sleep(5);
                    RenderedImage img = imageLoader.getRenderedImage(images.getName(count));  // Load the current image.
                    if (img != null) {
                        ParameterBlock pb = new ParameterBlock();  // Set up the parameters to pass to "mean".
                                pb.addSource(img);
                                pb.add(null);
                                pb.add(1);
                                pb.add(1);
                        RenderedImage meanImage = JAI.create("mean", pb, null);  // Find the mean value of the colors in the img.
                        mean = (double[])meanImage.getProperty("mean");
                        if (mean.length == 3) {  // If there were 3 bands of Data (RBG, not Monochrome..)
                            Color c = new Color((int)mean[0], (int)mean[1], (int)mean[2]);  // Make a color from it.
                                if (c != null) {
                                    imageInfo newImg = new imageInfo();         // Set up an imageInfo record with the data.
                                    newImg.setFileName(images.getName(count));
                                    newImg.setRBGVal(c);
                                    images.replace(nextSlot, newImg);  // Put the record in the next slot in the list.
                                    setCurr(count);                    // Update task stuff, for progress indicators.
                                    setMsg(images.getName(count));
                                    nextSlot++;
                                }
                        } else {  // Debugging.
                            System.out.println("Wrong Number Of Bands On Image: " + images.getName(count));
                        }
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