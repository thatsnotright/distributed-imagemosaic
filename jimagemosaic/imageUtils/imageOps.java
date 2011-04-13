/************************************************
 *  imageUtils/imageOps.java
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
 * imageOps offers some basic common imaging tools,
 * such as scaling, opening an image in a window, etc.
 ************************************************/

package imageUtils;

import java.awt.image.RenderedImage;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.widget.ScrollingImagePanel;
import javax.swing.*;
import javax.media.jai.Interpolation;

public class imageOps {

    // scale -- Scales a given RenderedImage to a specified width and height.
    public static RenderedImage scale(RenderedImage img, int width, int height) {
        float scaleX = (float) width / (float) img.getWidth();   // Create the X scale factor.
        float scaleY = (float) height / (float) img.getHeight(); // Create the Y scale factor.
        RenderedImage outImg = JAI.create("scale", img, scaleX, scaleY, 0.0F, 0.0F,
                    Interpolation.getInstance(Interpolation.INTERP_BILINEAR));  // Do the scale, using JAI, and
                                                                                // Bilinear interpolation.
        return outImg;
    }

    /* makeWindowImage -- This just opens an image, (given by fileName as a path/filename)
                          in a scrolling window.  Note, this does NOT make any assurances
                          that the image is valid!                                           */
    public static void makeWindowImage(String fileName) {
        RenderedOp img = JAI.create("fileload", fileName);  // Load up the image.
        JFrame jp = new JFrame(fileName);                   // Create the components.
        JPanel jpan = new JPanel();
        jp.getContentPane().add(new ScrollingImagePanel(img, img.getWidth(), img.getHeight()));  // Add the image.
        jp.pack();
        jp.show();   // Show it.
    }
   
}
