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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.Iterator;

/**
 *
 * @author kelvin
 */
public class FileDropTargetListener implements DropTargetListener {
    
    private PrometheusUI mainUI;
    
    FileDropTargetListener(PrometheusUI mf) {
            this.mainUI = mf;
        }

        @Override
        public void dragEnter(final DropTargetDragEvent dtde) {
        }

        @Override
        public void dragExit(final DropTargetEvent dtde) {
        }

        @Override
        public void dragOver(final DropTargetDragEvent dtde) {
        }

        @Override
        public void dropActionChanged(final DropTargetDragEvent dtde) {
        }

        @Override
        public void drop(final DropTargetDropEvent event) {
            try {
                Transferable transferable = event.getTransferable();
                System.out.println("transferable = " + transferable);
                if (transferable.isDataFlavorSupported(
                        DataFlavor.javaFileListFlavor)) {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    final Object os = transferable.getTransferData(
                            DataFlavor.javaFileListFlavor);
                    if (!(os instanceof java.util.Collection)) {
                        MessageFactory.showErrorMessageBox(mainUI, "Not a collection!");
                        return;
                    }
                    boolean err = false;
                    for (Iterator i = ((java.util.Collection) os).iterator(); i.hasNext();) {
                        File f = (File) i.next();
                        if (f != null) {
                            mainUI.load(f.getAbsolutePath());
                        }
                    }
                    event.getDropTargetContext().dropComplete(true);
                } else if (transferable.isDataFlavorSupported(
                        DataFlavor.stringFlavor)) {
                    event.acceptDrop(DnDConstants.ACTION_COPY);

                    // Modifications made by bharathch follow.The
                    // modifications were made to address issue #17:
                    // "Dragging new feeds from Firefox.."
                    String urlString = transferable.getTransferData(
                            DataFlavor.stringFlavor).toString();
                    // DnD from firefox introduces a \n followed by the
                    // anchor label that appears on the page. Hence, use
                    // only the substring upto the \n
                    int linebreakIndex = urlString.indexOf('\n');
                    String insertText = null;
                    if (linebreakIndex != -1) {
                        insertText = urlString.substring(0, linebreakIndex);
                    } else {
                        // DnD from IE and other browsers;use the string as
                        // it is.
                        insertText = urlString;
                    }
                    final String it = insertText;

                    // This is how the previous insertText was previously
                    // obtained:
                    // insertText = transferable.getTransferData(
                    //                DataFlavor.stringFlavor).toString();
                    // Modifications end
                    (new Thread() {

                        @Override
                        public void run() {

                            try {
                                mainUI.load(it);
                            } catch (Throwable t2) {
                                MessageFactory.showErrorMessageBox(mainUI, t2.getMessage());
                            }
                        }
                    }).start();
                    event.getDropTargetContext().dropComplete(true);
                } else {
                    MessageFactory.showErrorMessageBox(mainUI, "Sorry - I can only record streams and URLs");
                    event.rejectDrop();
                }
            } catch (Throwable t) {
                event.rejectDrop();
            }
        }
    
}
