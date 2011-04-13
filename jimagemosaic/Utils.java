/************************************************
 *  Utils.java
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
 * This class performs some very basic utility
 * functions for the program.
 ************************************************/

import java.io.File;
import java.util.LinkedList;
import java.util.Random;

public class Utils {
    public final static String jpeg="jpeg";
    public final static String jpg="jpg";
    public final static String gif="gif";

    // Returns the file extention of a given file.
    public static String getExtension(File f) {
        String exten = null;
        String fileN = f.getName();
        int index = fileN.lastIndexOf(".");
        if (index > 0 && index < fileN.length() - 1) {
            exten = fileN.substring(index+1).toLowerCase();
        }
        return exten;
    }

    // Returns the appropriate slash, depending on the operating system.
    public static String getSlashType() {
        return System.getProperty("file.separator");
    }
    
}