/************************************************
 *  myComboBoxRenderer.java
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
 * This is the renderer class required to display the
 * render algorithms properly in a combo box.
 ************************************************/

import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import plugins.plugin;

public class myComboBoxRenderer extends JLabel implements ListCellRenderer {

    // Constructor - Set up aesthetics of the combo box.
    public myComboBoxRenderer() {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
    }

    // This is how to display the cell components.
    public java.awt.Component getListCellRendererComponent(javax.swing.JList theList, java.lang.Object value, int index, boolean isSelected, boolean cellHasFocus) {
        
        // Set up the backgrounds.
        if (isSelected) {
            setBackground(theList.getSelectionBackground());
            setForeground(theList.getSelectionForeground());
        } else {
            setBackground(theList.getBackground());
            setForeground(theList.getForeground());
        }

        // The object getting passed in is a render algorithm..
        plugin p = (plugin)value;
        // Get the plugin's name, and set the text to that.
        if (p == null) {
            setText("Unknown Plugin Name");
        } else {
            setText(p.getName());
        }
        return this;
    }

}