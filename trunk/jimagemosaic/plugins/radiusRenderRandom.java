/************************************************
 *  plugins/radiusRenderRandom.java
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
 * radiusRenderRandom renders an image where tiles CAN be
 * repeated, but not within a certain "radius."  In
 * this implementation, the radius isn't a true radius,
 * but rather a box.  Otherwise, the tiles are selected
 * just as the medianEvaluator tiles are selected.
 * In future versions, this class may be removed, and
 * radius rendering may be implemented as an option to pass
 * into other render functions.  Also, rather than
 * rendering the tiles sequentially, it randomly
 * selects which tile to render next.  The commented out
 * "Rendering in parts" thing will be pulled out into
 * a different class later.  It allows for rendering of
 * extremely large images (10,000 x 10,000 pixels, etc.)
 ************************************************/

package plugins;

import imageUtils.*;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import javax.media.jai.JAI;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Random;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.ROIShape;
import java.awt.Rectangle;
import javax.media.jai.TiledImage;


public class radiusRenderRandom extends renderTaskPlugin {

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
    private int colorCorrectPercent;
    private int tileRepType;
    private int minDist;
    private int tileOrder;              // 1 for random, 0 for linear.

    private final String pluginName = "Radius Render (Random)";  // Overwrite this to give your plugin a name.


    /** Creates new radiusRenderRandom */
    public radiusRenderRandom (Color[][] ma, imageList tileLib, RenderedImage baseImg, int h, int v, int pw, int ph, int ccp, int trt, int md, int to) {
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
        this.colorCorrectPercent = ccp;
        this.tileRepType = trt;
        this.minDist = md;
        this.tileOrder = to;
    }

    // Must have a default no-arg constructor.
    public radiusRenderRandom() {
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

        // record object class.
        // Used to keep track of the x/y values for a tile.
/*        private class recObj {
            public int xVal;
            public int yVal;
        }

    // Randomly shuffles the order the tiles are rendered.
    // Returns the randomized list.
    private LinkedList getRandomTileList() {
        LinkedList tileList = new LinkedList();  // The new, random list of tiles.
        // These loops are just to populate the list with the tiles in order.
        for (int x = 0; x < hTiles; x++) {         // Cycle through all the tiles (Horiz. and Vertic.)
            for (int y = 0; y < vTiles; y++) {
                recObj ro = new recObj();       // Create a new record with the coordinates of the tile.
                ro.xVal = x;
                ro.yVal = y;
                tileList.add(ro);
            }
        }

        // Now we shuffle up that list.
        Random rand = new Random();
        int randInt = 0;
        for (int count = 0; count < tileList.size(); count++) {  // Go through all of the objects.
            recObj ro = new recObj();
            ro = (recObj) tileList.get(count);
            randInt = rand.nextInt(tileList.size());
            tileList.set(count, tileList.get(randInt));    // Swap the next tile with a random other tile.
            tileList.set(randInt, ro);
            
        }
        return tileList;
    }
  */  
    // findBestFit returns the index of a tile that best matches a certain color.
    // And it also makes sure it is not a duplicate image in the radius.
    private int findBestFit(Color c, int[][] tileArray, int x, int y) {
        int radiusTiles = 5;  // The number of radius tiles... this WILL be user-configurable.
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
/*                boolean inRadius = false;
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
                } */

                if ( !tileChecker.checkPlacement( tileRepType, minDist, tileArray, x, y, count, hTiles, vTiles) ) {  // If this tile isn't in the box, find the difference in color.
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

      // The actual rendering algorithm.
      Render() {
        int imgNum;
        SampleModel sm = baseImage.getSampleModel();
/******* Comment the following TiledImage block to render in parts. ********/
        newImage = new TiledImage(baseImage.getMinX(),
                                       baseImage.getMinY(),
                                       (pixWidth * hTiles),
                                       (pixHeight * vTiles),
                                       baseImage.getTileGridXOffset(),
                                       baseImage.getTileGridYOffset(),
                                       baseImage.getSampleModel(),
                                       baseImage.getColorModel());
/***************************************************************************/
        RenderedImage smallImage;
        int tileArray[][] = new int[hTiles][vTiles];
        //LinkedList ll = Utils.getRandomTileList(1, hTiles, vTiles); // Get the shuffled list of tiles.
        LinkedList ll = tileRenderList.getRandomTileList(tileOrder, hTiles, vTiles);
        tileRenderList.recObj ro = new tileRenderList.recObj();
        // First, select all the tiles.
        for (int count = 0; count < ll.size(); count++) {  // Go through the tile list.
                ro = (tileRenderList.recObj) ll.get(count);  // Get the x/y data from this tile.
                int x = ro.xVal;
                int y = ro.yVal;
                try {
                    Thread.sleep(5);
                    tileArray[x][y] = findBestFit(mapArray[x][y], tileArray, x, y);  // Find an image for this tile.
                    setCurr(count);           // Task stuff.
                    setMsg("Choosing Image For Tile #" + count);
                } catch (Exception e) {}
        }
        setCurr(0);   // Task stuff.
        setMsg("Done Selecting Tiles... Generating Image");

/*********  Uncomment following to render in parts.  ************/
        /*int numBreaks = 10;
        int hBreak = hTiles / numBreaks;
        int hMin = 0;
        int hMax = hBreak;*/
/****************************************************************/
        // Now, we actually build the image (sequentially) from the chosen tiles.
        for (int x = 0; x < hTiles; x++) {  // Cycle through the image horizontally.

/*********  Uncomment following to render in parts.  ************/
            /*if ((x % hBreak) == 0) {
                    hMax = x + hBreak;
                    hMin = x;
                    if ((hTiles - hMax) < hBreak) {
                        hMax = hTiles;
                    }

                    if (hMin != 0) {
                    // Save Image....
                        System.out.println("Saving image to file...");
                        BMPSaveType bst = new BMPSaveType();
                        bst.saveImageToFile(newImage, "/home/jimdrewes/Images/new" + hMin + ".bmp");
                        System.out.println("Done saving!");
                    }
                    
                    newImage = new TiledImage(baseImage.getMinX(),
                                       baseImage.getMinY(),
                                       (pixWidth * (hMax - hMin)),
                                       (pixHeight * vTiles),
                                       baseImage.getTileGridXOffset(),
                                       baseImage.getTileGridYOffset(),
                                       baseImage.getSampleModel(),
                                       baseImage.getColorModel());
             } */  
/****************************************************************/

            for (int y = 0; y < vTiles; y++) {  // Cycle through the horizontal tiles.
                try {
                    Thread.sleep(5);
                    smallImage = imageLoader.getRenderedImage(tileLibrary.get(tileArray[x][y]).getFileName());  // load the tile image.
                    smallImage = imageOps.scale(smallImage, pixWidth, pixHeight);  // Scale it down.
                    TiledImage outTest = colorCorrect.Correct(tileLibrary.get(tileArray[x][y]).getColor(), smallImage, colorCorrectPercent);
                    // Set a region of interest to paste the tile into on the final image.
                    ROIShape myROI = new ROIShape(new Rectangle((x*pixWidth), (y*pixHeight), smallImage.getWidth(), smallImage.getHeight()));
/*********  Uncomment following to render in parts.  ************/
//                    ROIShape myROI = new ROIShape(new Rectangle(((x-hMin)*pixWidth), (y*pixHeight), smallImage.getWidth(), smallImage.getHeight()));
/****************************************************************/
                    ParameterBlock pb = new ParameterBlock();
                        pb.addSource(outTest);
/*********  Uncomment following to render in parts.  ************/
//                        pb.add((float)((x-hMin)*pixWidth));
/****************************************************************/
                        pb.add((float)(x*pixWidth));
                        pb.add((float)(y*pixHeight));
                        pb.add(new InterpolationNearest());
                    smallImage = JAI.create("translate", pb, null);  // Move the image to the correct spot.
                    newImage.setData(smallImage.getData(), myROI);   // Paste the small tile image into the final image.
                    setCurr((x * vTiles) + y);   // Task stuff.
                    setMsg("Building Tile# " + ((x * vTiles) + y));
                } catch (Exception e) {e.printStackTrace();}
            }
        }
/*********  Save Image -- Uncomment following to render in parts.  ************/
        // Save Image -- Uncomment for partial rendering...
/*                        System.out.println("Saving image to file...");
                        BMPSaveType bst = new BMPSaveType();
                        bst.saveImageToFile(newImage, "/home/jimdrewes/Images/new_last.bmp");
                        System.out.println("Done saving!");
*/
/******************************************************************************/
        setCurr(lengthOfTask);  // Task stuff.
        setMsg("DONE!");
        }
    }
}