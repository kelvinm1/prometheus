/**
 * Prometheus
 * Copyright (C) 2012  Kelvin Miles
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package prometheus.gui;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Factory class for displaying message dialogs.
 */
public class MessageFactory {

    /**
     * Displays error message dialog.
     * 
     * @param parent UI component
     * @param msg Message to display
     */
    public static void showErrorMessageBox(Component parent, String msg) {
        Object[] options = {"OK"};
        JOptionPane.showOptionDialog(parent, msg, "Error", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE, null, options, options[0]);
    }

    /**
     * Displays warning message dialog.
     * 
     * @param parent UI component
     * @param msg Message to display
     * @return Integer value corresponding to button in dialog clicked...OK or Cancel
     */
    public static int showWarningMessageBox(Component parent, String msg) {
        Object[] options = {"OK", "Cancel"};
        int optionType = JOptionPane.showOptionDialog(parent, msg, "Error", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        return optionType;
    }

    /**
     * Displays confirmation message dialog.
     * 
     * @param parent UI component
     * @param msg Message to display
     * @return Integer value corresponding to button in dialog clicked...Yes, No, or Cancel
     */
    public static int showConfirmMessageBox(Component parent, String msg) {
        Object[] options = {"Yes", "No", "Cancel"};
        int optionType = JOptionPane.showOptionDialog(parent, msg, "Error", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        return optionType;
    }

    /**
     * Displays basic information message dialog.
     * 
     * @param parent UI component
     * @param msg Message to display
     */
    public static void showInfoMessageBox(Component parent, String msg) {
        Object[] options = {"OK"};
        JOptionPane.showOptionDialog(parent, msg, "Error", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
    }

    

    
}
