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

import prometheus.icy.ShoutcastStream;
import java.io.*;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

/** 
 * Object representing a playlist holding stream addresses 
 */
public class BasicPlaylistStream {

    protected BufferedReader playlist;

    /** 
     * Constructs a new playlist given an InputStream pointing to the file 
     */
    public BasicPlaylistStream(InputStream in) {
        playlist = new BufferedReader(new InputStreamReader(in));
    }

    /** 
     * Extract the entries that contain Internet radio stream addresses 
     */
    protected String[] getEntries() throws IOException {
        int numEntries;
        String[] entries;
        try {
            playlist.readLine();
            numEntries = Integer.parseInt(playlist.readLine().split("=")[1]);
            String str;
            entries = new String[numEntries];
            int i = 0;
            while ((str = playlist.readLine()) != null && i < numEntries) {
                if (str.startsWith("File")) {
                    entries[i] = str.split("=")[1];
                    i++;
                }
            }
        } catch (IOException e) {
            throw new IOException("I/O Error");
        }

        return entries;
    }

    /** 
     * Use regular expression to check if the address is valid 
     */
    protected boolean isValidAddress(String address) {
        URL url;
        int iPort;

        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            System.out.println("MalformedURL found: " + e.getMessage());
            return false;
        }

        if (!url.getProtocol().matches("http")) {
            System.out.println("Only http streams are supported..");
            return false;
        }

        iPort = url.getPort();

        if (iPort == -1) {
            return false;
        }

        boolean connected = false;
        Socket connection = null;
        DataInputStream socketReader = null;
        DataOutputStream socketWriter = null;

        try {
            connection = new Socket(url.getHost(), iPort);
            connected = connection.isConnected();
        } catch (UnknownHostException e) {
            System.err.println("isValidStream: Host not found");
            return false;
        } catch (ConnectException e) {
            System.err.println("isValidStream: Connect to host failed");
            return false;
        } catch (IOException e) {
            System.err.println("isValidStream: Socket I/O Error");
            return false;
        } catch (NullPointerException e) {
            System.err.println("isValidStream: Host url is null");
            return false;
        }

        if (connected == true) {
            try {
                socketReader = new DataInputStream(connection.getInputStream());
                socketWriter = new DataOutputStream(connection.getOutputStream());
            } catch (IOException e) {
                System.err.println("isValidStream: I/O Error");
                return false;
            }
        }

        return true;
    }

    /** 
     * Return the first valid stream from the list of addresses 
     */
    public RadioStream getValidStream() throws IOException {
        RadioStream validStream = null;
        try {
            String[] entries = getEntries();
            for (String entry : entries) {
                if (isValidAddress(entry)) {
                    validStream = new ShoutcastStream(entry);
                    break;
                }
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
        return validStream;
    }

    /** 
     * Return the first valid stream from the list of addresses 
     */
    public String getValidStreamAsString() throws IOException {
        String validStream = null;
        try {
            String[] entries = getEntries();
            for (String entry : entries) {
                if (isValidAddress(entry)) {
                    validStream = entry;
                    break;
                }
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
        return validStream;
    }
}
