/************************************************
 *  imageUtils/colorList.java
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
 * This class is just a simple implementation of
 * Java's LinkedList, made specific to storing
 * Colors, and retrieving Colors.  This class,
 * in JImage Mosaic's current implementation is
 * not entirely necessary.
 ************************************************/


package imageUtils;

import java.util.*;
import java.util.LinkedList.*;
import imageUtils.imageInfo;
import java.awt.Color;

public class colorList {

    // Constructor  -- Initializes variables.
    public colorList() {
        this.size = 0;
        this.cs = new LinkedList();
    }

    // Clear the entire list, and set its size to zero.
    public void clear() {
        this.size = 0;
        this.cs.clear();
        
    }

    // Add another color to the list, and update the size.
    public void put(Color c) {
        cs.add(c);
        this.size++;
    }

    // Retrieve a color from the list at position "i".
    public Color get(int i) {
        Color c = (Color) cs.get(i);
        return c;
    }

    // Dispose of the color in position "i" in the list.
    public void remove(int i) {
        cs.remove(i);
        size--;
    }

    // Find the size of the list.
    public int getSize() {
        return size;
    }

    // Class variables.
    private LinkedList cs;
    private int size;
}
