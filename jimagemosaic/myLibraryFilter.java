/************************************************
 *  myLibraryFilter.java
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
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 *
 * This class filters out everything but JImage Mosaic libraries (*.JML).
 ************************************************/

//  A good part of this file was taken directly from Sun's ImageFilter.java file.
//  Sun's file can be found at http://java.sun.com/docs/books/tutorial/uiswing/components.example-swing/ImageFilter.java

import java.io.File;
import javax.swing.filechooser.*;

public class myLibraryFilter extends FileFilter {

    // Decide what should and shouldn't be shown.
    public boolean accept(File f) {
        if (f.isDirectory()) {      // Directories are okay.
            return true;
        }

        String extn = Utils.getExtension(f);
        if (extn != null) {
            if (extn.equals("jml")) {       // So are these.
                    return true;
            } else {
                    return false;
            }
        }
        return false;
    }

    public String getDescription() {
        return "JImage Mosaic Libraries (*.jml)";  // File description.
    }
}