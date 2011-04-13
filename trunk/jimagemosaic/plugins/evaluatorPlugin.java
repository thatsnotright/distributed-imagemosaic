/************************************************
 *  plugins/evaluatorPlugin.java
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
 * This is the generic evaluator class.  All algorithms
 * to evaluate images into an average color, etc. should
 * extend this class.  This class extends the generic plugin.
 ************************************************/

package plugins;

import imageUtils.*;
import java.awt.Color;

public class evaluatorPlugin extends plugin{

    // Class variables.
    private int taskLen;   // Length of the evaluation task.
    private int curr = 0;  // Current spot in the evaluation task.
    private String outMsg = "";
    private colorList colors = new colorList();

    /** Creates new evaluatorPlugin */
    public evaluatorPlugin() {
    }

    // Adds a color to the color list.
    public void addColor(Color c) {
        colors.put(c);
    }

    // Returns the list of colors.
    public colorList getColors() {
        return colors;
    }

    // Changes the task length.
    public void setTaskLen(int tl) {
        taskLen = tl;
    }

    // Sets the current position in the task.
    public void setCurr(int c) {
        curr = c;
    }

    // Sets an output message to use in a progress indicator, etc.
    public void setMsg(String s) {
        outMsg = s;
    }

    // Thread stuff.
    public void go() {
        curr = 0;
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return new Evaluate();
            }
        };
        worker.start();
    }

    // Checks to see if the evaluation is done.
    public boolean done() {
        if (curr >= taskLen) {
            return true;
        } else {
            return false;
        }
    }

    // Returns the length of the evaluation task.
    public int getTaskLen() {
        return taskLen;
    }

    // Returns the current position in the evaluation task.
    public int getCurr() {
        return curr;
    }

    // Stops the evaluation by setting the current position to the end.
    public void stop() {
        curr = taskLen;
    }

    // Gets the output message.
    public String getMessage() {
        return outMsg;
    }

    // This class holds the actual evaluation algorithm.
    // Since the evaluatorPlugin class is generic, this "Evaluate"
    // class needs to be overwritten.
    class Evaluate {  // overwrite this class to include your algorithm.
      Evaluate() {
           curr = taskLen;
           outMsg = "Empty Evaluation Plugin.";
           System.out.println(outMsg);
    }
  }
}