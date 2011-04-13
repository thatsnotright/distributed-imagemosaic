/************************************************
 *  plugins/renderTaskPlugin.java
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
 * This class is the generic class which is later
 * extended by individual rendering classes.
 ************************************************/

package plugins;

public class renderTaskPlugin extends plugin{

    // Class variables.
    private int taskLen;            // The length of the render task.
    private int curr = 0;           // The current spot in the render task.
    private String outMsg = "";     // Output messages for use in progress indicators.

    /** Creates new evaluatorPlugin */
    public renderTaskPlugin() {
    }

    // Sets the length of the render task.
    public void setTaskLen(int tl) {
        taskLen = tl;
    }

    // Sets the current spot in the render task.
    public void setCurr(int c) {
        curr = c;
    }

    // Sets the current message to be output (to progress indicators, etc.)
    public void setMsg(String s) {
        outMsg = s;
    }

    // Start the rendering task.
    public void go() {
        curr = 0;
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return new Render();
            }
        };
        worker.start();
    }

    // Checks to see if the task is done, by seeing if the current is past the task length.
    public boolean done() {
        if (curr >= taskLen) {
            return true;
        } else {
            return false;
        }
    }

    // Returns the length of the render task.
    public int getTaskLen() {
        return taskLen;
    }

    // Returns the current location in the render task.
    public int getCurr() {
        return curr;
    }

    // Stops the render task by setting the current location to the end.
    public void stop() {
        curr = taskLen;
    }

    // Returns the output message.
    public String getMessage() {
        return outMsg;
    }

    // The actual rendering algorithm.  This one is basically empty.
    class Render {  // overwrite this class to include your algorithm.
      Render() {
           curr = taskLen;
           outMsg = "Empty Evaluation Plugin.";
           System.out.println(outMsg);
    }
  }
}
