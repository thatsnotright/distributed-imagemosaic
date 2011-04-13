/************************************************
 *  plugins/saveTypePlugin.java
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
 * saveTypePlugin is the generic class for saving
 * files.  This is extended by classes in the
 * saveTypes package.
 ************************************************/

package plugins;

import javax.media.jai.TiledImage;

public class saveTypePlugin extends plugin {

    // Construct a saveTypePlugin.
    public saveTypePlugin() {
    }

    // Saves the image to a file.
    public boolean saveImageToFile(TiledImage outputImage, String fileName) {
        return false;
    }

}