/************************************************
 *  imageUtils/imageLoaderThread.java
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
 * This class is not used in this version of JImage Mosaic.
 ************************************************/

/***************  THIS CLASS IS NOT CURRENTLY BEING USED! ********************/

package imageUtils;

import plugins.*;
import java.awt.image.*;
import javax.imageio.ImageReader;

public class imageLoaderThread {

    private int taskLen;
    private int curr = 0;
    private String outMsg = "";
    private String fileName;
    private RenderedImage outImage;

    /** Creates new evaluatorPlugin */
    public imageLoaderThread(int tl, String fn) {
        this.taskLen = tl;
        this.fileName = fn;
    }

    public imageLoaderThread(String fn) {
        this.taskLen = 100;
        this.fileName = fn;
    }

    public void go() {
        curr = 0;
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return new loadImage();
            }
        };
        worker.start();
    }

    public boolean done() {
        if (curr >= taskLen) {
            return true;
        } else {
            return false;
        }
    }

    private void setCurr(int c) {
        this.curr = c;
    }

    private void setMsg(String msg) {
        this.outMsg = msg;
    }

    public int getTaskLen() {
        return taskLen;
    }

    public int getCurr() {
        return curr;
    }

    public void stop() {
        curr = taskLen;
    }

    public String getMessage() {
        return outMsg;
    }

    public RenderedImage getOutImage() {
        return outImage;
    }

    class loadImage {  // overwrite this class to include your algorithm.
      loadImage() {
        ImageReader reader = imageLoader.getReaderOnly(fileName);
        float percentDone = 0.0F;
        try {
            System.out.println("Reading image...");
            outImage = reader.read(0);
        } catch (java.io.IOException ioe) {
            System.out.println("There was an error loading the image" + fileName);
        }
        while (imageLoader.getDone() == false) {
            try {
                Thread.sleep(5);
                //reader.processImageComplete();
                percentDone = imageLoader.getReadProgress();
                //percentDone = 100.0F;
                System.out.println(percentDone);
                setCurr((int)percentDone);
                setMsg("Loading Image... ");
            } catch (Exception e) {}
        }
        reader.dispose();
        System.out.println("Done");
        setCurr(taskLen);
        setMsg("Done Loading Image!");
    }
  }
}
