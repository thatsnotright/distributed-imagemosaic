/************************************************
 *  imageUtils/imageList.java
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
 * imageList is a class that utilizes Java's
 * LinkedList functionality, and uses it for
 * storing image records (imageInfo.class) in
 * a list.
 ************************************************/

package jimagemosaic.imageutils;

import java.util.*;
import java.util.LinkedList.*;
import java.awt.Color;

public class imageList implements IImageList {

    // Constructor -- initializes variables, and creates a new LinkedList.
    public imageList() {
        this.size = 0;
        this.images = new LinkedList();
    }

    // Clears the list and sets the size to zero.
    /* (non-Javadoc)
	 * @see jimagemosaic.imageutils.IImageList#clear()
	 */
    @Override
	public void clear() {
        this.size = 0;
        this.images.clear();
        
    }

    // Adds an imageInfo record into the list at location "i".
    /* (non-Javadoc)
	 * @see jimagemosaic.imageutils.IImageList#put(jimagemosaic.imageutils.imageInfo)
	 */
    @Override
	public void put(IImageInfo i) {
        images.add(i);
        this.size++;
    }

    // Returns an imageInfo record based on a particular file name/path.
    /* (non-Javadoc)
	 * @see jimagemosaic.imageutils.IImageList#get(java.lang.String)
	 */
    @Override
	public IImageInfo get(String f) {
      int index = 0;
      ListIterator li = images.listIterator();     // Iterate through the LinkedList.
        while (li.hasNext()) {
            IImageInfo ii = (IImageInfo) li.next();
            if (ii.getFileName() == f) {          // If this is the record, return it.
                return ii;
            }
        }
        return null;  // No record found?  Return NULL.
    }

    // Returns an imageInfo record basaed on its index in the list.
    /* (non-Javadoc)
	 * @see jimagemosaic.imageutils.IImageList#get(int)
	 */
    @Override
	public IImageInfo get(int i) {
        return (IImageInfo) images.get(i);
    }

    // Returns the file name/path of an image based on its index in the list.
    /* (non-Javadoc)
	 * @see jimagemosaic.imageutils.IImageList#getName(int)
	 */
    @Override
	public String getName(int i) {
        IImageInfo ii = (IImageInfo) images.get(i);
        return ii.getFileName();
    }

    // Returns the file name, without path, of an image based on its index.
    /* (non-Javadoc)
	 * @see jimagemosaic.imageutils.IImageList#getNameNoPath(int)
	 */
    @Override
	public String getNameNoPath(int i) {
      IImageInfo ii = (IImageInfo) images.get(i);
      return ii.getFileName().substring(ii.getFileName().lastIndexOf('/') +1, ii.getFileName().length());  // String parsing.
    }

    // Removes an imageInfo record at index "i".
    /* (non-Javadoc)
	 * @see jimagemosaic.imageutils.IImageList#remove(int)
	 */
    @Override
	public void remove(int i) {
        images.remove(i);
        size--;
    }

    // Changes the imageInfo record that is in a specific location.
    /* (non-Javadoc)
	 * @see jimagemosaic.imageutils.IImageList#replace(int, jimagemosaic.imageutils.imageInfo)
	 */
    @Override
	public void replace(int i, IImageInfo img) {
        images.set(i, img);
    }

    // This is used for debugging purposes.  It echos the list out to std_out.
    /* (non-Javadoc)
	 * @see jimagemosaic.imageutils.IImageList#printList()
	 */
    @Override
	public void printList() {
        ListIterator li = images.listIterator();
        while (li.hasNext()) {
            IImageInfo ii = (IImageInfo) li.next();
            System.out.println("Name: " + ii.getFileName() + "   Color: " + ii.getColor());
        }
    }

    // Return the size of the list.
    /* (non-Javadoc)
	 * @see jimagemosaic.imageutils.IImageList#getSize()
	 */
    @Override
	public int getSize() {
        return size;
    }

    // Class variables.
    private LinkedList images;  // The list of images.
    private int size;           // The size of the list.

}
