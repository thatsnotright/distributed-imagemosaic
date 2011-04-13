/************************************************
 *  plugins/radiusRender.java
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
 * radiusRender renders an image where tiles CAN be
 * repeated, but not within a certain "radius."  In
 * this implementation, the radius isn't a true radius,
 * but rather a box.  Otherwise, the tiles are selected
 * just as the medianEvaluator tiles are selected.
 * In future versions, this class may be removed, and
 * radius rendering may be implemented as an option to pass
 * into other render functions.
 ************************************************/

package plugins;

import imageUtils.*;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import javax.media.jai.JAI;
import java.awt.Color;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.ROIShape;
import java.awt.Rectangle;
import javax.media.jai.TiledImage;

public class radiusRender extends renderTaskPlugin {

    // Class variables.
    private int current;                 // Current place in the task.
    private int lengthOfTask;            // The length of the rendering task (number of tiles, usually).
    private int hTiles;                  // The number of tiles in the final image, horizontally.
    private int vTiles;                  // The number of tiles in the final image, vertically.
    private int pixWidth;                // The width of one tile.
    private int pixHeight;               // The height of one tile.
    private imageList tileLibrary;       // The library of tiles we can pull from.
    private RenderedImage baseImage;     // The base image.
    private TiledImage newImage;         // The final image we are creating.
    private Color[][] mapArray;          // The color map, showing how the base image pixillates.

    private final String pluginName = "Radius Render (Linear)";  // Overwrite this to give your plugin a name.

    /** Creates new radiusRender */
    public radiusRender (Color[][] ma, imageList tileLib, RenderedImage baseImg, int h, int v, int pw, int ph) {
        super.setTaskLen((h * v) + 1);  // The length of the task is the number of tiles plus one.
        current = super.getCurr();
        lengthOfTask = super.getTaskLen();
        this.mapArray = ma;   // Set up all of the class variables.
        this.hTiles = h;
        this.vTiles = v;
        this.pixWidth = pw;
        this.pixHeight = ph;
        this.tileLibrary = tileLib;
        this.baseImage = baseImg;
    }

    // Must have a default no-arg constructor.
    public radiusRender() {
        setPluginName(pluginName);
    }

    // Returns the completed image.
    public TiledImage getNewImage() {
        return this.newImage;
    }

/***********************************************************
    DO NOT EDIT THIS UNLESS YOU KNOW WHAT YOU ARE DOING!
************************************************************/
    public void go() {  // Thread stuff.
        current = 0;
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                Render render = null;
                try {
                    render =  new Render();
                } catch (Exception e) {
                    System.out.println("Thread Error!");
                    e.printStackTrace();
                }
                if (render == null) {
                    return null;
                } else {
                    return render;
                }
            }
        };
        worker.start();
    }
//************************************************************
    class Render {  // overwrite this class to include your algorithm.

    // findBestFit returns the index of a tile that best matches a certain color.
    // And it also makes sure it is not a duplicate image in the radius.
    private int findBestFit(Color c, int[][] tileArray, int x, int y) {
        int radiusTiles = 10;  // The number of radius tiles... this WILL be user-configurable.
        int closestSoFar = 0;  // Index of the tile that best matches the color so far.
        int redDiff, greenDiff, blueDiff, totalDiff = 0;
        totalDiff = (256*3);  // Initialize the total difference to the largest reasonable number.
        if (c==null) {  // assume that the color is black...
            c = new Color(0,0,0);
        }
        for (int count = 0; count < tileLibrary.getSize(); count++) {  // Cycle through all of the library tiles.
            if (tileLibrary.get(count).getColor() != null) {

/*********  UNCOMMENT THE FOLLOWING TO USE THE NO-REP ALGORITHM  ***********/
    /*  // This is the no-rep algorithm.
                redDiff = Math.abs(c.getRed() - images.get(count).getColor().getRed());
                blueDiff = Math.abs(c.getBlue() - images.get(count).getColor().getBlue());
                greenDiff = Math.abs(c.getGreen() - images.get(count).getColor().getGreen());
                if (((redDiff + blueDiff + greenDiff) < totalDiff) && (used[count] == false)) {
                    totalDiff = redDiff + blueDiff + greenDiff;
                    closestSoFar = count;
                }
    */
/**************************************************************************/

/********** COMMENT OUT TO USE NO-REP ALGORITHM INSTEAD *******************/
            // Is this image in our no-dice radius?
                boolean inRadius = false;
                for (int newX = (x - radiusTiles); newX < (x+radiusTiles); newX++) {  // Cycle through the width of the box...
                    for (int newY = (y - radiusTiles); newY < (y+radiusTiles); newY++) {  // Cycle through the height of the box...
                        // If we are still within the bounds of the image....
                        if ((newX >= 0) && (newY >= 00) && (newX < hTiles) && (newY < vTiles)) { // && !((newX >= x) && (newY >= y))) {
                            try {
                            if (tileArray[newX][newY] == count) {  // If that tile is already in the box...
                                inRadius = true;
                            }
                            } catch (Exception e) {System.out.println(e);}
                        }
                    }
                }
                if (inRadius == false) {  // If this tile isn't in the box, find the difference in color.
                    redDiff = Math.abs(c.getRed() - tileLibrary.get(count).getColor().getRed());
                    blueDiff = Math.abs(c.getBlue() - tileLibrary.get(count).getColor().getBlue());
                    greenDiff = Math.abs(c.getGreen() - tileLibrary.get(count).getColor().getGreen());
                    if (((redDiff + blueDiff + greenDiff) < totalDiff)) { // if this is closer than the previous closest...
                        totalDiff = redDiff + blueDiff + greenDiff;
                        closestSoFar = count;  // Keep track of this tile.
                    }
                }
            }
        }
        return closestSoFar;  // return the tile we chose.
    }

      // This is the actual rendering algorithm.
      Render() {
        int imgNum;
        SampleModel sm = baseImage.getSampleModel();   // Set up the attributes for the new image.
        newImage = new TiledImage(baseImage.getMinX(),
                                       baseImage.getMinY(),
                                       (pixWidth * hTiles),
                                       (pixHeight * vTiles),
                                       baseImage.getTileGridXOffset(),
                                       baseImage.getTileGridYOffset(),
                                       baseImage.getSampleModel(),
                                       baseImage.getColorModel());
        RenderedImage smallImage;   // Variable for holding the scaled down image to be placed in the large image.
        int tileArray[][] = new int[hTiles][vTiles];  // The array of how the tiles are arranged.
        // First, we select what tiles go where...
        for (int x = 0; x < hTiles; x++) {  // Cycle through the image tiles, horizontally.
            for (int y = 0; y < vTiles; y++) {  // Cycle through the image tiles, vertically.
                try {
                    Thread.sleep(5);
                    tileArray[x][y] = findBestFit(mapArray[x][y], tileArray, x, y);  // Find the tile to go there.
                    setCurr((x * vTiles) + y);                                       // Update task stuff.
                    setMsg("Choosing Image For Tile #" + ((x * vTiles) + y));
                } catch (Exception e) {}
            }
        }

        setCurr(0);  // Task stuff, for progress indicators...
        setMsg("Done Selecting Tiles... Generating Image");

        // Next, we actually build the image based on the tiles we chose.
        for (int x = 0; x < hTiles; x++) { // Again, cycle horizonally,
            for (int y = 0; y < vTiles; y++) { // And vertically. ( for every tile in the image )
                try {
                    Thread.sleep(5);
                    smallImage = imageLoader.getRenderedImage(tileLibrary.get(tileArray[x][y]).getFileName()); // Load the image from the tile we selected.
                    smallImage = imageOps.scale(smallImage, pixWidth, pixHeight); // Scale the image to the appropriate size.
                    // Create a region of interest on the large image to paste the small image into.
                    ROIShape myROI = new ROIShape(new Rectangle((x*pixWidth), (y*pixHeight), smallImage.getWidth(), smallImage.getHeight()));
                    ParameterBlock pb = new ParameterBlock();  // Move the image to the appropriate spot...
                        pb.addSource(smallImage);
                        pb.add((float)(x*pixWidth));
                        pb.add((float)(y*pixHeight));
                        pb.add(new InterpolationNearest());
                    smallImage = JAI.create("translate", pb, null);  // Do the translation.
                    newImage.setData(smallImage.getData(), myROI); // Stick the tile image into the large image.
                    setCurr((x * vTiles) + y);                      // Update task stuff.
                    setMsg("Building Tile# " + ((x * vTiles) + y));
                } catch (Exception e) {}
            }
        }
        setCurr(lengthOfTask);  // Finish up the task stuff.
        setMsg("DONE!");
        }
    }
}