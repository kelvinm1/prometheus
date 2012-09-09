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
package prometheus.io;

import java.util.Map;
import java.io.*;
import java.util.Observable;

/** 
 * General implementation of a radio stream recorder.
 */
public abstract class BasicRecorder extends Observable implements StreamRecorder {

    protected Thread ripperThread;
    protected File currentRip;
    protected DataOutputStream streamWriter, outputFile;
    protected DataInputStream streamReader;
    protected RadioStream stream;
    protected Map<String, String> streamMetadata;
    protected RadioOutputStream log;
    protected String outputDir = ".";
    protected boolean writing = false, firstSong = true, closing = false;
    protected int recCount = 0;

    /** 
     * Return the stream associated with the ripper 
     */
    @Override
    public RadioStream getStream() {
        return stream;
    }

    /** 
     * Returns the output directory of this ripper 
     */
    @Override
    public String getOutputPath() {
        return outputDir;
    }

    /**
     * Returns the number of streams currently recording     
     */
    @Override
    public int getCount() {
        return recCount;
    }

    /**
     * Sets the path to where downloads are saved.
     * @param dir 
     */
    public void setOutputPath(String dir) {
        this.outputDir = dir;
    }

    /** 
     * Set the way the class logs its status, must be called before startRipping() is called. 
     * Use a GUILogger to output to a JTextArea,
     * use a ConsoleLogger to output to the console */
    public void setLogger(RadioOutputStream log) {
        this.log = log;
    }

    /** 
     * Starts the recording thread 
     */
    @Override
    public abstract void run();

    /** 
     * Start recording the radio stream 
     * @throws RecorderException
     */
    @Override
    public abstract void startRecording() throws RecorderException;

    /** 
     * Check to see if the stream is still recording 
     */
    @Override
    public abstract boolean isRecording();

    /** 
     * Handle a packet of mp3 data and write it to the mp3
     * @param packet packet of mp3 data
     * @throws RecorderException
     */
    protected abstract void onPacket(byte[] packet) throws RecorderException;

    /** 
     * Extract metadata from an array of bytes
     * @param meta array of bytes holding the metadata
     * @return a map containing the extracted metadata
     */
    protected abstract Map getMetadata(byte[] meta);

    /** 
     * Process new metadata.  If the new metadata has a different stream title,
     * onStreamChange is called with the new title.
     * @param meta map of the new metadata
     * @throws RecorderException
     */
    protected abstract void onNewMetadata(Map meta) throws RecorderException;

    /** 
     * Called whenever the name of the stream changes.
     * This usually indicates the start of a new track.
     * @param newName the stream's new title
     * @throws RecorderException
     * */
    protected abstract void onStreamChange(String newName) throws RecorderException;

    /** 
     * Stop recording the radio stream 
     */
    @Override
    public abstract void stopRecording();

    /** 
     * Return a string representation of the ripper.
     * If the ripper is currently recording a song,
     * the name of that song is returned.  Otherwise, 
     * the method will return a status message.
     */
    @Override
    public abstract String toString();
}
