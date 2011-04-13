/************************************************
 *  plugins/mapCreatePlugin.java
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
 * This is the generic map creation class.  All algorithms
 * to create tile maps should extend this class.
 * This class extends the generic plugin.
 ************************************************/

package plugins;

public class mapCreatePlugin extends plugin{

    // Class variables.
    private int taskLen;    // The length of the mapCreate task.
    private int curr = 0;   // The current place in the map creation task.
    private String outMsg = "";

    /** Creates new mapCreatePlugin */
    public mapCreatePlugin() {
    }

    // Sets the length of the map creation task.
    public void setTaskLen(int tl) {
        taskLen = tl;
    }

    // Sets the current location in the map creation task.
    public void setCurr(int c) {
        curr = c;
    }

    // Sets the output message.
    public void setMsg(String s) {
        outMsg = s;
    }

    // Thread stuff.
    public void go() {
        curr = 0;
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return new createMap();
            }
        };
        worker.start();
    }

    // Checks to see if the map creation is done.
    public boolean done() {
        if (curr >= taskLen) {
            return true;
        } else {
            return false;
        }
    }

    // Returns the length of the map creation task.
    public int getTaskLen() {
        return taskLen;
    }

    // Returns the current position in the map creation task.
    public int getCurr() {
        return curr;
    }

    // Ends the map creation task.
    public void stop() {
        curr = taskLen;
    }

    // Returns the output message.
    public String getMessage() {
        return outMsg;
    }

    // The actual map creation algorithm.
    class createMap {  // overwrite this class to include your algorithm.
      createMap() {
           curr = taskLen;
           outMsg = "Empty Evaluation Plugin.";
           System.out.println(outMsg);
    }
  }
}
