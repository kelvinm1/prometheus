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
package prometheus;

import prometheus.gui.PrometheusUI;
import prometheus.icy.ShoutcastRecorder;
import prometheus.icy.ShoutcastStation;
import prometheus.io.ConsoleOutputStream;
import prometheus.util.PrometheusUtil;
import java.io.*;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.cli.*;

/**
 * Console application to download tracks from an SHOUTcast Internet radio
 * stream as MP3 files.
 */
public class Prometheus {

    // Command line cmdLineOptions
    private Options cmdLineOptions = null;
    // Command Line arguments
    private CommandLine cmdLineParser = null;
    // Option to set user-defined destination directory
    private final String DESTINATION_DIR_OPTION = "d";
    // Option to display version information about this program
    private final String VERSION_INFO_OPTION = "v";
    // Option to add number sequence to files downloaded
    private final String ADD_SEQUENCE_OPTION = "q";
    // Option to overwrite files that already exists
    private final String OVERWRITE_FILES_OPTION = "o";
    // Option to add ID3v1 tag to mp3 file
    private final String ADD_ID3V1_OPTION = "i";
    // Option to run in UI mode
    private final String RUN_UI_OPTION = "x";
    // Usage text
    private final String USAGE_TEXT = "java -jar prometheus.jar <stream-url> [options]";

    /**
     * Create new instance of this console application
     */
    public Prometheus() {
        // Setup command line cmdLineOptions
        cmdLineOptions = new Options();
        cmdLineOptions.addOption(DESTINATION_DIR_OPTION, true,
                Resources.APP_RESOURCE.getString("DESTINATION_DIR_TEXT"));
        cmdLineOptions.addOption(VERSION_INFO_OPTION, false,
                Resources.APP_RESOURCE.getString("VERSION_INFO_TEXT"));
        cmdLineOptions.addOption(ADD_SEQUENCE_OPTION, false,
                Resources.APP_RESOURCE.getString("ADD_SEQUENCE_TEXT"));
        cmdLineOptions.addOption(OVERWRITE_FILES_OPTION, false,
                Resources.APP_RESOURCE.getString("OVERWRITE_FILES_TEXT"));
        cmdLineOptions.addOption(ADD_ID3V1_OPTION, false,
                Resources.APP_RESOURCE.getString("ADD_ID3V1_TEXT"));
        cmdLineOptions.addOption(RUN_UI_OPTION, false,
                Resources.APP_RESOURCE.getString("RUN_UI_TEXT"));
    }

    /**
     * Set look and feel for any UI screens used by this application.
     */
    protected void setLookAndFeel() {
        try {
            // Use Java Platform Look & Feel
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Verify version of Java platform running is at least 7.0
     */
    protected void checkJavaVersion() {
        // Check for Java 1.6 or later
        String javaVersion = System.getProperty("java.version");
        if (javaVersion.compareTo("1.7") < 0) {
            System.err.println("You are running Java version "
                    + javaVersion + '.');
            System.err.println(Resources.applicationName + " requires Java 1.6 or later.");
            System.exit(1);
        }
    }

    /**
     * Validate and set command line arguments. Exit after printing usage if
     * anything is astray
     *
     * @param args String[] args as featured in public static void main()
     */
    public void launch(String[] args) {
        checkJavaVersion();
        setLookAndFeel();

        // Print license notice...
        System.out.println("\n" + Resources.APP_RESOURCE.getString("NOTICE") + "\n");

        // If no command line arguments, then just display the UI
        if (args.length < 1) {           
            final PrometheusUI ui = new PrometheusUI();
            ui.displayInWindow();
        } else {
            
            try {
                CommandLineParser parser = new PosixParser();
                cmdLineParser = parser.parse(cmdLineOptions, args);

                // All user wants is version information...so display it and exit application
                if (cmdLineParser.hasOption(VERSION_INFO_OPTION)) {
                    System.out.println(Resources.applicationName + " " + PrometheusUtil.getVersion());
                    System.exit(0);
                }

                String url;
                String dir = System.getProperty("user.dir"); // Use current working directory

                // Check for mandatory URL arg
                url = PrometheusUtil.getValidUrl(args[0]);                               

                // Check for destination option        
                if (cmdLineParser.hasOption(DESTINATION_DIR_OPTION)) {
                    dir = cmdLineParser.getOptionValue(DESTINATION_DIR_OPTION);
                }

                // Lets launch the application using option passed in command line.        
                File destination = new File(dir);
                if (!destination.exists()) {
                    destination.mkdirs();
                }

                // See if user wants to run in UI mode
                if (cmdLineParser.hasOption((RUN_UI_OPTION))) {
                    final PrometheusUI ui = new PrometheusUI();
                    ui.setSource(url);
                    ui.setDestinationPath(dir);
                    ui.setSequenceOption(cmdLineParser.hasOption(ADD_SEQUENCE_OPTION));
                    ui.setOverwriteOption(cmdLineParser.hasOption(OVERWRITE_FILES_OPTION));
                    ui.setTaggingOption(cmdLineParser.hasOption(ADD_ID3V1_OPTION));
                    ui.displayInWindow();
                    ui.start(); // Automatically launch recording                                    
                } else {
                    // Else, we just run in terminal screen.
                    ShoutcastStation shoutcastStation = new ShoutcastStation(url, dir);
                    shoutcastStation.setLogger(new ConsoleOutputStream("Waiting for connection..."));

                    ShoutcastRecorder recorder = (ShoutcastRecorder) shoutcastStation.getStationRecorder();
                    recorder.setSequenceFlag(cmdLineParser.hasOption(ADD_SEQUENCE_OPTION));
                    recorder.setOverwriteFlag(cmdLineParser.hasOption(OVERWRITE_FILES_OPTION));
                    recorder.setID3TagFlag(cmdLineParser.hasOption(ADD_ID3V1_OPTION));

                    // print settings to console
                    System.out.println("Add Sequence flag is: " + (recorder.getSequenceFlag() ? "ON" : "OFF"));
                    System.out.println("Add ID3v1 tag flag is: " + (recorder.getID3TagFlag() ? "ON" : "OFF"));
                    System.out.println("Overwrite flag is: " + (recorder.getOverwriteFlag() ? "ON" : "OFF"));
                    System.out.println();

                    // Start recording thread
                    Thread currThread = new Thread(shoutcastStation);
                    currThread.start();
                }
            } catch (Exception e) {
                System.err.println("Error parsing arguments: " + e.getMessage());
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(USAGE_TEXT, cmdLineOptions);
                System.exit(1);
            }
        }
    }

    /**
     * Main entry
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Prometheus app = new Prometheus();
        app.launch(args);
    }
}
