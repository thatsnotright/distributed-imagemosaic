/************************************************
 *  plugins/plugin.java
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
 * The generic plugin class.  All plugin types will
 * extend this class, and the actual plugins will
 * extend those types.
 ************************************************/

package plugins;

public class plugin {

    /** Creates new plugin */
    public plugin() {
    }

    // Gets the name of the plugin -- This will be used once we list the plugins, and there is a classloader.
    public String getName() {
        return pluginName;
    }

    // Gets a description of what the plugin does.
    public String getDescription() {
        return pluginDescription;
    }

    // Returns the category of plugin.  This too will be used for listing plugins once a classloader is implemented.
    public String getPluginType() {
        return null;  // The following are the different return types.
        // return "storage";    // these plugins store image libraries (ex. File, Database, etc.)
        // return "evaluator";  // these plugins are evaluation algorithms for the tile images.
        // return "generator";  // these plugins are the major image generators.
        // return "post";       // these plugins do post-generation image corrections.
        // return "save";     // these plugins export the image in various formats (i.e. JPG, GIF, etc.)
    }

    public void setPluginName(String name) {
        this.pluginName = name;
    }
    private String pluginName = "Unnamed Plugin";  // Overwrite this to give your plugin a name.
    private String pluginDescription = "No Description Available.";  // Overwrite this to give a description to your plugin.

}
