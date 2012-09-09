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
import java.util.Map;

/** 
 * Represents a connection to an Internet radio stream
 */
public interface RadioStream {

    /** 
     * Read a line of data from the server 
     * @throws StreamException thrown if line cannot be read      
     */
    public String readLine() throws StreamException;

    /** 
     * Returns a string representation of the stream. 
     * If the stream is not connected or does not currently
     * have a title, a status message is returned.  Otherwise,
     * the name of the stream is returned
     */
    @Override
    public String toString();

    /** 
     * Returns an output stream connected to the radio stream
     * @return output stream that writes to server     
     * @throws StreamException thrown if output stream cannot be retrieved
     */
    public DataOutputStream getWriter() throws StreamException;

    /** 
     * Returns an input stream connected to the radio stream
     * @return input stream that reads from server
     * @throws StreamException thrown if input stream cannot be retrieved
     */
    public DataInputStream getReader() throws StreamException;

    /**
     * Returns the address of the radio stream 
     */
    public String getTarget();

    /** 
     * Returns the name of the current track 
     */
    public String getCurrentTrack();

    /** 
     * Connect to the Internet radio stream
     * @throws StreamException thrown if there is something wrong with the 
     * data being used to connect to the stream
     * 
     */
    public void connect() throws StreamException;

    /** 
     * Returns a map of the stream's metadata 
     */
    public Map<String, String> getStreamMetadata();

    /** 
     * Is the stream connected 
     */
    public boolean isConnected();

    /** 
     * Closes the connection to the radio stream and any opened I/O streams 
     */
    public void close();
}