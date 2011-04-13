/************************************************
 *  imageUtils/colorCorrect.java
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
 ************************************************/


package imageUtils;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.RenderedImage;
import javax.media.jai.TiledImage;
import java.awt.Color;

public class colorCorrect {

    public static TiledImage Correct(Color c, RenderedImage img, int correctPercent) {
    if (c == null) { System.out.println("color is null");}
    if (img == null) { System.out.println("img is null");}
    if (correctPercent < 0) { System.out.println("% is null");}
        Raster rast = img.getData();
    if (rast == null) { System.out.println("raster is null");}
        TiledImage returnImage;
        double percentCorrect = (double) correctPercent / 100.0;
        returnImage = new TiledImage(img, rast.getWidth(), rast.getHeight());
        WritableRaster outRast = rast.createCompatibleWritableRaster();
        try {
        for (int x=rast.getMinX(); x<(rast.getMinX() + rast.getWidth()); x++) {
            for (int y=rast.getMinY(); y<(rast.getMinY() + rast.getHeight()); y++) {
                int thisColor[] = new int[3];
                int newColor[] = new int[3];
                thisColor[0] = rast.getSample(x,y,0);
                thisColor[1] = rast.getSample(x,y,1);
                thisColor[2] = rast.getSample(x,y,2);
                newColor[0] = Math.abs(thisColor[0] - c.getRed());
                newColor[1] = Math.abs(thisColor[1] - c.getGreen());
                newColor[2] = Math.abs(thisColor[2] - c.getBlue());
                if (thisColor[0] < c.getRed()) {
                    newColor[0] = thisColor[0] + (int)(percentCorrect * (double)newColor[0]);
                } else {
                    newColor[0] = thisColor[0] - (int)(percentCorrect * (double)newColor[0]);
                }
                if (thisColor[1] < c.getGreen()) {
                    newColor[1] = thisColor[1] + (int)(percentCorrect * (double)newColor[1]);
                } else {
                    newColor[1] = thisColor[1] - (int)(percentCorrect * (double)newColor[1]);
                }
                if (thisColor[2] < c.getBlue()) {
                    newColor[2] = thisColor[2] + (int)(percentCorrect * (double)newColor[2]);
                } else {
                    newColor[2] = thisColor[2] - (int)(percentCorrect * (double)newColor[2]);
                }
                try {
                    outRast.setSample(x, y, 0, newColor[0]);
                    outRast.setSample(x, y, 1, newColor[1]);
                    outRast.setSample(x, y, 2, newColor[2]);
                } catch (Exception e) {
                    System.out.println("Error!  Tried printing to coordinate (" + x + "," + y + ")");
                    System.out.println("But coordinates limited to X: (" + outRast.getMinX() + " --> " + outRast.getWidth() + ")");
                    System.out.println("                       and Y: (" + outRast.getMinY() + " --> " + outRast.getHeight() + ")");
                }
            }
        }
        } catch (Exception e) { e.printStackTrace(); }

        returnImage.setData(outRast);
        return returnImage;
    }
}