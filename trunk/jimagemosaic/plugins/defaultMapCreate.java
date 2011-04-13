/************************************************
 *  plugins/defaultMapCreate.java
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
 * This class extends the mapCreatePlugin, in order
 * to provide the default way of creating Maps.
 * The class uses threads to provide the ability for
 * progress indicators to be easily made.
 ************************************************/

package plugins;

import java.awt.image.renderable.ParameterBlock;
import java.awt.image.*;
import javax.media.jai.*;
import java.awt.Color;
import java.awt.Rectangle;

public class defaultMapCreate extends mapCreatePlugin {

    private int current;                                // The current place in the task.
    private int lengthOfTask;                           // The length of the task.
    private RenderedImage baseImage;                    // The base image, from MainForm.
    private RenderedImage outImage;                     // A visual representation of the map.
    private int hTiles;                                 // The number of tiles to divide into, horizontally.
    private int vTiles;                                 // The number of tiles to divide into, vertically.
    private Color[][] mapArray = new Color[500][500];   // The array of colors, representing the map.
                                                        // This is limiting the map to 500x500 right now.
                                                        // I intend to replace this.

    // Constructor.
    public defaultMapCreate (RenderedImage ri, int h, int v) {  // the base image, hTiles and vTiles.
        super.setTaskLen(h*v);              // The length of the task is the number of tiles (hTiles * vTiles).
        current = super.getCurr();          // Make sure we are in sync.
        lengthOfTask = super.getTaskLen();  // This should be what we just set it to.
        this.baseImage = ri;
        this.hTiles = h;
        this.vTiles = v;
    }

    // Returns the map image that we just produced.
    public RenderedImage getOutImage() {
        return this.outImage;
    }

    // Returns the map array of colors we just produced.
    public Color[][] getMapArray() {
        return mapArray;
    }

/***********************************************************
    DO NOT EDIT THIS UNLESS YOU KNOW WHAT YOU ARE DOING!
************************************************************/
    public void go() {
        current = 0;        // Start the task at zero.
        final SwingWorker worker = new SwingWorker() {  // Thread stuff.
            public Object construct() {
                createMap newMap = null;
                try {
                    newMap =  new createMap();  // Do the process.
                } catch (Exception e) {
                    System.out.println("Thread Error!");  // Debugging.
                    e.printStackTrace();
                }
                if (newMap == null) {
                    return null;
                } else {
                    return newMap;
                }
            }
        };
        worker.start();  // Start the thread.
    }
//**********************************************************

    class createMap {  // overwrite this class to include your algorithm.
                       // This is the actual map generation algorithm.
      createMap() {

    // Calculate how tall and wide the chunks we are finding the average color for are.
    // A "pixel" is a large average-color representation of an area on the base image.
    int pixelWidth = (int) (baseImage.getWidth() / hTiles);
    int pixelHeight = (int) (baseImage.getHeight() / vTiles);

    // Create a new image, used as the output.  Base it on the same color model and size as the original.
    SampleModel sm = baseImage.getSampleModel();
    TiledImage newImage = new TiledImage(sm, baseImage.getWidth(), baseImage.getHeight());

    // Now, cycle through all of the map tile areas we are creating.
    // This part of the algorithm is going to be re-written for speed.
      for (int smallY = 0; smallY < vTiles; smallY++) {
        for (int smallX = 0; smallX < hTiles; smallX++) {

            // Calculate the starting pixels for the block.
            int yDim = smallY * pixelHeight;
            int xDim = smallX * pixelWidth;

            try {
                
                Thread.sleep(10);  // Wait for a 10 ms.
                // Create a region of interest on the source image that starts at the starting pixels
                // we just calculated, and extends the height and width of the block.
                ROIShape myROI = new ROIShape(new Rectangle(xDim,yDim,pixelWidth,pixelHeight));

                // Set the parameters for doing the JAI "mean" calculation.
                ParameterBlock pb = new ParameterBlock();
                pb.addSource(baseImage);
                pb.add(myROI);
                pb.add(1);
                pb.add(1);

                // Find the mean value of the area.
                RenderedImage meanImage = JAI.create("mean", pb, null);
                double[] mean = (double[])meanImage.getProperty("mean");  // Extract the data as doubles.
                
                // This assumes we are using color JPEGs, which have 3 bands of data.. Red, Green, and Blue.
                Color c = new Color((int)mean[0], (int)mean[1], (int)mean[2]);

                // Put that color into the relevant pixels on the new image.
                // There might be a JAI way of doing this, using ROIs.
                for (int x = 0; x < pixelWidth; x++) {
                    for (int y=0; y < pixelHeight; y++) {
                        newImage.setSample(xDim+x, yDim+y, 0, c.getRed());
                        newImage.setSample(xDim+x, yDim+y, 1, c.getGreen());
                        newImage.setSample(xDim+x, yDim+y, 2, c.getBlue());
                    }
                }

                // Update how far we are in our task.
                setCurr((smallY * hTiles) + smallX);
                setMsg("Generating Map");

                // Assign the color to the map array.
                mapArray[(xDim / pixelWidth)][(yDim / pixelHeight)] = c;
            } catch (Exception e) {}
        }
    }
        outImage = newImage;    // Put the image we just made as the outImage.
        setCurr(lengthOfTask);  // Finish up the task.
        setMsg("DONE!");
        }
    }
}