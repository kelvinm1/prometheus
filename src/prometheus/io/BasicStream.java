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

import java.io.*;
import java.net.Socket;
import java.util.*;

/** 
 * Basic foundation of a connection to an Internet radio stream
 */
public abstract class BasicStream implements RadioStream {

    protected boolean connected = false;
    protected String targetURL;
    protected String currentSong = "";
    protected String[] urlDetails = new String[3];
    protected int listerIndex = -1, redirectCount = 0;
    protected Map<String, String> streamMetadata = new HashMap<String, String>();
    protected Socket connection;
    protected DataInputStream socketReader;
    protected DataOutputStream socketWriter;
    protected RadioOutputStream log;

    /** 
     * Returns an output stream connected to the radio stream
     * @return output stream that writes to server
     * @throws StreamException thrown if output stream cannot be retrieved
     */
    @Override
    public DataOutputStream getWriter() throws StreamException {
        try {
            return new DataOutputStream(connection.getOutputStream());
        } catch (IOException e) {
            throw new StreamException("I/O Error");
        }
    }

    /** 
     * Returns an input stream connected to the radio stream
     * @return input stream that reads from server
     * @throws StreamException thrown if input stream cannot be retrieved
     */
    @Override
    public DataInputStream getReader() throws StreamException {
        try {
            return new DataInputStream(connection.getInputStream());
        } catch (IOException e) {
            throw new StreamException("I/O Error");
        }
    }

    /**
     * Returns the address of the radio stream
     */
    @Override
    public String getTarget() {
        return targetURL;
    }

    /** 
     * Returns the name of the current track      
     */
    @Override
    public String getCurrentTrack() {
        return currentSong;
    }

    /** 
     * Returns a map of the stream's metadata 
     */
    @Override
    public Map<String, String> getStreamMetadata() {
        return streamMetadata;
    }

    /** 
     * Set the way the class logs its status must be called before connect().      
     * Use a ConsoleOutputStream to output to the console 
     */
    public void setLogger(RadioOutputStream log) {
        this.log = log;
    }

    /** 
     * Is the stream connected 
     */
    @Override
    public boolean isConnected() {
        return connected;
    }

    /** 
     * Returns a string representation of the stream. 
     * If the stream is not connected or does not currently
     * have a title, a status message is returned.  Otherwise,
     * the name of the stream is returned
     */
    @Override
    public abstract String toString();

    /** 
     * Read a line of data from the server      
     * @throws StreamException
     */
    @Override
    public abstract String readLine() throws StreamException;

    /** 
     * Verify address and connect to the radio stream
     * @throws StreamException thrown if there something wrong with the data 
     * being used to connect     
     */
    @Override
    public abstract void connect() throws StreamException;

    /** 
     * Try to connect to the stream.  This method might be called more than once 
     * if it fails the first time, or is called with a new address due to a redirect
     */
    protected abstract void tryConnect(String[] info);

    /** 
     * Collect information from the server
     * @throws StreamException thrown if something goes wrong writing or reading from the server
     */
    protected abstract void startListening() throws StreamException;

    @Override
    public abstract void close();
}