/**
 * Prometheus Copyright (C) 2012 Kelvin Miles This program is free software: you
 * can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package prometheus.gui;

import prometheus.Resources;
import java.awt.Component;
import java.awt.Container;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import prometheus.icy.ShoutcastRecorder;
import prometheus.icy.ShoutcastStation;
import prometheus.io.ConsoleOutputStream;
import prometheus.io.M3UFileFilter;
import prometheus.io.PLSFileFilter;
import prometheus.io.RadioStation;
import prometheus.util.PrometheusUtil;
import prometheus.util.StationPreset;

/**
 * This is the main UI panel for this application.
 *
 * @author kelvin
 */
public class PrometheusUI extends javax.swing.JPanel implements Observer {

    // UI Frame for this application
    final JFrame parentFrame = new JFrame();
    // Radio station to record
    private RadioStation shoutcastStation = null;
    // List of station presets
    protected DefaultListModel<StationPreset> presetListModel = new DefaultListModel<StationPreset>();

    /**
     * Creates new form PrometheusUI
     */
    public PrometheusUI() {
        initComponents();

        // hide playlist view on startup
        cbxShowPlaylist.setSelected(false);
        playlistPanel.setVisible(false);

        // add drag and drop for loading playlist files
        addDnD(presetList, PrometheusUI.this);
        addDnD(presetScrollPane, PrometheusUI.this);

        // create a Etched title border for panel sections
        Border border = new EtchedBorder();
        sourcePanel.setBorder(new TitledBorder(border, "Broadcast Parameters"));
        destinationPanel.setBorder(new TitledBorder(border, "Output"));
        trackPanel.setBorder(new TitledBorder(border, "Current MP3"));

    }

    /**
     * Clear current download information
     */
    public void clearFields() {
        trackLabel.setText("--");
        stationLabel.setText("--");
        genreLabel.setText("--");
        readBytes.setText("0 bytes");
        fileCount.setText("0 songs downloaded");
    }

    /**
     * Display panel in UI frame
     */
    public void displayInWindow() {
        parentFrame.setTitle(Resources.applicationName);
        parentFrame.setLayout(new java.awt.BorderLayout());
        parentFrame.add(this, java.awt.BorderLayout.CENTER);
        parentFrame.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        parentFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                if (okToClose() == JOptionPane.YES_OPTION) {
                    stop();
                    parentFrame.dispose();
                    System.exit(0);
                }
            }
        });
        parentFrame.pack();
        parentFrame.setResizable(false);
        parentFrame.setVisible(true);
    }

    /**
     * Confirm with user if ok to remove all streams
     *
     * @return the integer value of YES, NO, or CANCEL
     */
    public int okToClose() {
        int option = JOptionPane.YES_OPTION;

        try {
            if (shoutcastStation.getStationRecorder().isRecording()) {
                option = JOptionPane.showConfirmDialog(this, "You still have a station recording."
                        + "\nWould you like to exit anyway?",
                        "Confirm Close",
                        JOptionPane.YES_NO_CANCEL_OPTION);
            }
        } catch (NullPointerException e) {
            return JOptionPane.YES_OPTION;
        }
        return option;
    }

    public void load(String path) {

        if (path.toLowerCase().endsWith(".pls") || path.toLowerCase().endsWith(".m3u")) {
            try {
                //clear old streams
                presetListModel.clear();

                //get filtered Streams
                String[][] importableStreams = PrometheusUtil.anlyseFile(path);

                if (importableStreams != null || importableStreams.length > 0) {
                    //fill in preset with list of new streams from playlist
                    for (int i = 0; i < importableStreams.length; i++) {
                        String name = importableStreams[i][1];
                        String url = importableStreams[i][0];
                        StationPreset preset = new StationPreset(name, url);
                        presetListModel.addElement(preset);
                    }
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                MessageFactory.showErrorMessageBox(this, ex.getMessage());
            }
        }
    }

    private void addDnD(final Component c, final PrometheusUI main) {
        DropTargetListener dtl = new FileDropTargetListener(main);
        DropTarget dt = new DropTarget(c, dtl);
        dt.setActive(true);
    }

    private void setPanelEnabled(JPanel panel, boolean enabled) {
        panel.setEnabled(enabled);
        for (Component component : panel.getComponents()) {
            if (component instanceof JPanel) {
                setPanelEnabled((JPanel) component, enabled);
                continue;
            }
            component.setEnabled(enabled);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblPlaylist = new javax.swing.JLabel();
        sourcePanel = new javax.swing.JPanel();
        lblSource = new javax.swing.JLabel();
        txtSource = new javax.swing.JTextField();
        cmdSelect1 = new javax.swing.JButton();
        cbxShowPlaylist = new javax.swing.JCheckBox();
        playlistPanel = new javax.swing.JPanel();
        presetScrollPane = new javax.swing.JScrollPane();
        presetList = new javax.swing.JList();
        commandPanel = new javax.swing.JPanel();
        cmdOpen = new javax.swing.JButton();
        cmdClear = new javax.swing.JButton();
        destinationPanel = new javax.swing.JPanel();
        lblSaveTo = new javax.swing.JLabel();
        txtDestination = new javax.swing.JTextField();
        cmdSelect2 = new javax.swing.JButton();
        chkAddSequence = new javax.swing.JCheckBox();
        chkTagFile = new javax.swing.JCheckBox();
        chkOverwrite = new javax.swing.JCheckBox();
        trackPanel = new javax.swing.JPanel();
        trackInfoScrollPane = new javax.swing.JScrollPane();
        trackInfoPanel = new javax.swing.JPanel();
        trackStaticLabel = new javax.swing.JLabel();
        trackLabel = new javax.swing.JLabel();
        stationStaticLabel = new javax.swing.JLabel();
        stationLabel = new javax.swing.JLabel();
        genreStaticLabel = new javax.swing.JLabel();
        genreLabel = new javax.swing.JLabel();
        statusStaticLabel = new javax.swing.JLabel();
        statusPanel = new javax.swing.JPanel();
        readBytes = new javax.swing.JLabel();
        commaLabel = new javax.swing.JLabel();
        fileCount = new javax.swing.JLabel();
        cmdStartAndStop = new javax.swing.JButton();
        lblVersion = new javax.swing.JLabel();

        lblPlaylist.setText("Or select individual station from user playlist file (*.m3u or *.pls):");

        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new java.awt.GridBagLayout());

        sourcePanel.setLayout(new java.awt.GridBagLayout());

        lblSource.setText("Enter location to station server or playlist file:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        sourcePanel.add(lblSource, gridBagConstraints);

        txtSource.setColumns(40);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        sourcePanel.add(txtSource, gridBagConstraints);

        cmdSelect1.setText("Browse...");
        cmdSelect1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelect1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        sourcePanel.add(cmdSelect1, gridBagConstraints);

        cbxShowPlaylist.setText("Select from user playlist file (*.m3u or *.pls):");
        cbxShowPlaylist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxShowPlaylistActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        sourcePanel.add(cbxShowPlaylist, gridBagConstraints);

        playlistPanel.setOpaque(false);
        playlistPanel.setPreferredSize(new java.awt.Dimension(500, 200));
        playlistPanel.setLayout(new java.awt.GridBagLayout());

        presetList.setModel(presetListModel);
        presetList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        presetList.setVisibleRowCount(10);
        presetList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                presetListValueChanged(evt);
            }
        });
        presetScrollPane.setViewportView(presetList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        playlistPanel.add(presetScrollPane, gridBagConstraints);

        commandPanel.setOpaque(false);
        commandPanel.setLayout(new java.awt.GridLayout(1, 0, 5, 5));

        cmdOpen.setText("Open File");
        cmdOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdOpenActionPerformed(evt);
            }
        });
        commandPanel.add(cmdOpen);

        cmdClear.setText("Clear");
        cmdClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdClearActionPerformed(evt);
            }
        });
        commandPanel.add(cmdClear);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        playlistPanel.add(commandPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        sourcePanel.add(playlistPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(sourcePanel, gridBagConstraints);

        destinationPanel.setLayout(new java.awt.GridBagLayout());

        lblSaveTo.setText("Select destination folder to save downloads:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        destinationPanel.add(lblSaveTo, gridBagConstraints);

        txtDestination.setColumns(40);
        txtDestination.setText(prometheus.Resources.applicationDirectory.getAbsolutePath());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        destinationPanel.add(txtDestination, gridBagConstraints);

        cmdSelect2.setText("Browse...");
        cmdSelect2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelect2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        destinationPanel.add(cmdSelect2, gridBagConstraints);

        chkAddSequence.setSelected(true);
        chkAddSequence.setText("Add Sequence Number to Output Track");
        chkAddSequence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAddSequenceActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        destinationPanel.add(chkAddSequence, gridBagConstraints);

        chkTagFile.setSelected(true);
        chkTagFile.setText("Add ID3v1 Tag to Output");
        chkTagFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTagFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        destinationPanel.add(chkTagFile, gridBagConstraints);

        chkOverwrite.setText("Overwrite Tracks If They Already Exist");
        chkOverwrite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOverwriteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        destinationPanel.add(chkOverwrite, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(destinationPanel, gridBagConstraints);

        trackPanel.setLayout(new java.awt.BorderLayout());

        trackInfoScrollPane.setOpaque(false);
        trackInfoScrollPane.setPreferredSize(new java.awt.Dimension(500, 125));

        trackInfoPanel.setBackground(java.awt.Color.white);
        trackInfoPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        trackInfoPanel.setLayout(new java.awt.GridBagLayout());

        trackStaticLabel.setText("Track:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        trackInfoPanel.add(trackStaticLabel, gridBagConstraints);

        trackLabel.setText("--");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        trackInfoPanel.add(trackLabel, gridBagConstraints);

        stationStaticLabel.setText("Station:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        trackInfoPanel.add(stationStaticLabel, gridBagConstraints);

        stationLabel.setText("--");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        trackInfoPanel.add(stationLabel, gridBagConstraints);

        genreStaticLabel.setText("Genre:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        trackInfoPanel.add(genreStaticLabel, gridBagConstraints);

        genreLabel.setText("--");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        trackInfoPanel.add(genreLabel, gridBagConstraints);

        statusStaticLabel.setText("Status:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        trackInfoPanel.add(statusStaticLabel, gridBagConstraints);

        statusPanel.setOpaque(false);
        statusPanel.setLayout(new java.awt.GridBagLayout());

        readBytes.setText("0 bytes");
        statusPanel.add(readBytes, new java.awt.GridBagConstraints());

        commaLabel.setText(",");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        statusPanel.add(commaLabel, gridBagConstraints);

        fileCount.setText("0 songs downloaded");
        statusPanel.add(fileCount, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        trackInfoPanel.add(statusPanel, gridBagConstraints);

        trackInfoScrollPane.setViewportView(trackInfoPanel);

        trackPanel.add(trackInfoScrollPane, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(trackPanel, gridBagConstraints);

        cmdStartAndStop.setText("Start Recording");
        cmdStartAndStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdStartAndStopActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(cmdStartAndStop, gridBagConstraints);

        lblVersion.setText("ver. " + prometheus.util.PrometheusUtil.getVersion());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(lblVersion, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void cmdSelect1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelect1ActionPerformed
        try {
            JFileChooser plsChooser = new JFileChooser();
            plsChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            plsChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            plsChooser.setMultiSelectionEnabled(false);
            //plsChooser.setFileFilter(new PLSFileFilter());
            plsChooser.addChoosableFileFilter(new M3UFileFilter());
            plsChooser.addChoosableFileFilter(new PLSFileFilter());
            plsChooser.setDialogTitle("Select a Playlist File");

            int i = plsChooser.showOpenDialog(this);
            if (i == JFileChooser.APPROVE_OPTION) {
                File file = plsChooser.getSelectedFile();
                if (file != null) {
                    txtSource.setText(file.toURI().toURL().toString());
                    cmdStartAndStop.requestFocus();
                }
            }
        } catch (Exception e) {
            System.err.print(e.getMessage());
            MessageFactory.showErrorMessageBox(this, e.getMessage());
        }

    }//GEN-LAST:event_cmdSelect1ActionPerformed

    private void cmdSelect2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelect2ActionPerformed
        try {
            JFileChooser dirChooser = new JFileChooser();
            dirChooser.setDialogTitle("Choose a directory to save MP3 files");
            dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            dirChooser.setMultiSelectionEnabled(false);
            dirChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

            int returnVal = dirChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File tempFile = dirChooser.getSelectedFile();

                String sSelection = tempFile.getAbsolutePath() + File.separator;
                txtDestination.setText(sSelection);
            }
        } catch (Exception e) {
            System.err.print(e.getMessage());
            MessageFactory.showErrorMessageBox(this, e.getMessage());
        }


    }//GEN-LAST:event_cmdSelect2ActionPerformed

    private void cmdStartAndStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdStartAndStopActionPerformed
        if (shoutcastStation != null
                && shoutcastStation.getStationRecorder().isRecording()) {
            stop();
        } else {
            start();
        }
    }//GEN-LAST:event_cmdStartAndStopActionPerformed

    private void cmdClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdClearActionPerformed
        presetListModel.clear();
    }//GEN-LAST:event_cmdClearActionPerformed

    private void cmdOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdOpenActionPerformed
        JFileChooser plsChooser = new JFileChooser();
        plsChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        plsChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        plsChooser.setMultiSelectionEnabled(false);
        plsChooser.addChoosableFileFilter(new PLSFileFilter());
        plsChooser.addChoosableFileFilter(new M3UFileFilter());
        plsChooser.setDialogTitle("Select a Playlist File");

        int i = plsChooser.showOpenDialog(this);
        if (i == JFileChooser.APPROVE_OPTION) {
            File file = plsChooser.getSelectedFile();
            if (file != null) {
                load(file.getAbsolutePath());
                //sourceTextField.setText(file.toURI().toURL().toString());
            }
        }
    }//GEN-LAST:event_cmdOpenActionPerformed

    private void presetListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_presetListValueChanged
        if (evt.getSource() == presetList) {
            Object selection = presetList.getSelectedValue();
            if ((selection == null) || (!(selection instanceof StationPreset))) {
                return;
            }

            StationPreset preset = (StationPreset) selection;
            txtSource.setText(preset.getURLString());
        }
    }//GEN-LAST:event_presetListValueChanged

    private void cbxShowPlaylistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxShowPlaylistActionPerformed
        playlistPanel.setVisible(cbxShowPlaylist.isSelected());

        Container topLevel = this.getTopLevelAncestor();
        if (topLevel instanceof java.awt.Window) {
            java.awt.Window dlg = (java.awt.Window) topLevel;
            dlg.pack();
        }

    }//GEN-LAST:event_cbxShowPlaylistActionPerformed

    private void chkOverwriteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOverwriteActionPerformed
        // TODO add your handling code here:
        ShoutcastRecorder recorder = (ShoutcastRecorder) shoutcastStation.getStationRecorder();
        if (recorder != null) {
            recorder.setOverwriteFlag(chkOverwrite.isSelected());
        }
    }//GEN-LAST:event_chkOverwriteActionPerformed

    private void chkAddSequenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAddSequenceActionPerformed
        ShoutcastRecorder recorder = (ShoutcastRecorder) shoutcastStation.getStationRecorder();
        if (recorder != null) {
            recorder.setSequenceFlag(chkAddSequence.isSelected());
        }
    }//GEN-LAST:event_chkAddSequenceActionPerformed

    private void chkTagFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTagFileActionPerformed
        ShoutcastRecorder recorder = (ShoutcastRecorder) shoutcastStation.getStationRecorder();
        if (recorder != null) {
            recorder.setID3TagFlag(chkTagFile.isSelected());
        }
    }//GEN-LAST:event_chkTagFileActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbxShowPlaylist;
    private javax.swing.JCheckBox chkAddSequence;
    private javax.swing.JCheckBox chkOverwrite;
    private javax.swing.JCheckBox chkTagFile;
    private javax.swing.JButton cmdClear;
    private javax.swing.JButton cmdOpen;
    public javax.swing.JButton cmdSelect1;
    protected javax.swing.JButton cmdSelect2;
    public static javax.swing.JButton cmdStartAndStop;
    private javax.swing.JLabel commaLabel;
    private javax.swing.JPanel commandPanel;
    private javax.swing.JPanel destinationPanel;
    public javax.swing.JLabel fileCount;
    public javax.swing.JLabel genreLabel;
    private javax.swing.JLabel genreStaticLabel;
    private javax.swing.JLabel lblPlaylist;
    private javax.swing.JLabel lblSaveTo;
    private javax.swing.JLabel lblSource;
    private javax.swing.JLabel lblVersion;
    private javax.swing.JPanel playlistPanel;
    private javax.swing.JList presetList;
    private javax.swing.JScrollPane presetScrollPane;
    public javax.swing.JLabel readBytes;
    private javax.swing.JPanel sourcePanel;
    public javax.swing.JLabel stationLabel;
    private javax.swing.JLabel stationStaticLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JLabel statusStaticLabel;
    private javax.swing.JPanel trackInfoPanel;
    private javax.swing.JScrollPane trackInfoScrollPane;
    public javax.swing.JLabel trackLabel;
    private javax.swing.JPanel trackPanel;
    private javax.swing.JLabel trackStaticLabel;
    protected javax.swing.JTextField txtDestination;
    protected javax.swing.JTextField txtSource;
    // End of variables declaration//GEN-END:variables

    public void setSource(String station) {
        txtSource.setText(station);
    }

    public String getSource() {
        return txtSource.getText().trim();
    }

    public void setDestinationPath(String directory) {
        txtDestination.setText(directory);
    }

    public String getDestinationPath() {
        return txtDestination.getText().trim();
    }

    public void setSequenceOption(boolean b) {
        this.chkAddSequence.setSelected(b);
    }

    public boolean isSequenceEnabled() {
        return this.chkAddSequence.isSelected();
    }

    public void setTaggingOption(boolean b) {
        this.chkTagFile.setSelected(b);
    }

    public boolean isTaggingEnabled() {
        return this.chkTagFile.isSelected();
    }

    public void setOverwriteOption(boolean b) {
        this.chkOverwrite.setSelected(b);
    }

    public boolean isOverwriteEnabled() {
        return this.chkOverwrite.isSelected();
    }

    public void start() {
        File tempFile = new File(txtDestination.getText().trim());
        String sSelection = tempFile.getAbsolutePath() + File.separator;
        txtDestination.setText(sSelection);

        if (txtDestination.getText().trim().equals("")) {
            MessageFactory.showWarningMessageBox(this, "Please choose a valid download destination.");
            return;
        }

        // Create destination path if does not already exists
        if (!new File(sSelection).exists()) {
            new File(sSelection).mkdirs();
        }

        try {
            String url = PrometheusUtil.getValidUrl(txtSource.getText().trim());
            String destination = txtDestination.getText().trim();

            shoutcastStation = new ShoutcastStation(url, destination);
            shoutcastStation.setLogger(new ConsoleOutputStream("Waiting..."));
            ShoutcastRecorder recorder = (ShoutcastRecorder) shoutcastStation.getStationRecorder();
            recorder.setSequenceFlag(chkAddSequence.isSelected());
            recorder.setOverwriteFlag(chkOverwrite.isSelected());
            recorder.setID3TagFlag(chkTagFile.isSelected());
            recorder.addObserver(this);
            Thread currThread = new Thread(shoutcastStation);
            currThread.start();
            cmdStartAndStop.setText("Stop Recording");

        } catch (Exception e) {
            if (shoutcastStation != null) {
            shoutcastStation.close();
            }
            System.err.println(e.getMessage());
            MessageFactory.showErrorMessageBox(this, e.getMessage());
        }        
    }

    public void stop() {
        try {
            shoutcastStation.getStationRecorder().stopRecording();
            clearFields();
            cmdStartAndStop.setText("Start Recording");
            setPanelEnabled(sourcePanel, true);
            setPanelEnabled(destinationPanel, true);
            shoutcastStation = null;
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void update(Observable o, Object o1) {
        // reload data because data file have changed
        String[] info = shoutcastStation.toString().split("\n");

        trackLabel.setText(info[1].replaceAll(".mp3", ""));
        stationLabel.setText(info[0]);

        try {
            long transfered = Long.parseLong(
                    shoutcastStation.getStationRecorder().getStream().getStreamMetadata().get("song-bytes"));
            readBytes.setText(PrometheusUtil.memSizeToString(transfered));
        } catch (NumberFormatException e) {
            readBytes.setText("--");
        } catch (NullPointerException e) {
            readBytes.setText("--");
        }

        genreLabel.setText(shoutcastStation.getStationStream().getStreamMetadata().get("icy-genre"));

        fileCount.setText(String.valueOf(shoutcastStation.getStationRecorder().getCount() - 1) + " songs downloaded");

    }

    
}
