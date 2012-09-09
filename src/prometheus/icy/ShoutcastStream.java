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
package prometheus.icy;

import java.io.*;
import java.net.*;
import java.util.regex.*;
import prometheus.io.BasicStream;
import prometheus.io.StreamException;

/**
 * SHOUTcast implementation of Stream 
 */
public class ShoutcastStream extends BasicStream {

    // Regular expression used for icy and http prefixes
    protected Pattern icyokre = Pattern.compile("^ICY\\s*\\w*\\s*OK", Pattern.CASE_INSENSITIVE);
    protected Pattern icy4xxre = Pattern.compile("^ICY\\s*40.*", Pattern.CASE_INSENSITIVE);
    protected Pattern httpre = Pattern.compile("^HTTP.*OK", Pattern.CASE_INSENSITIVE);
    
    /**
     * Create a new ShoutcastStream 
     * @param target HTTP URL string
     */
    public ShoutcastStream(String target) {
        targetURL = target;
        streamMetadata.put("targetURL", targetURL);
        streamMetadata.put("total-bytes", "1");
        streamMetadata.put("song-bytes", "1");
    }

    /** 
     * Verify address and connect to the radio stream
     * @throws StreamException thrown if there something wrong with the data
     * being used to connect     
     */
    @Override
    public void connect() throws StreamException {
        redirectCount++;
        log.setStatus("[Connecting...]");
        log.printStatus();
        URL url = null;
        try {
            url = new URL(targetURL);
        } catch (MalformedURLException e) {
            throw new StreamException("Malformed URL");
        } 
            
        int port = 80;
        if (url.getPort() != -1) {
            port = url.getPort();
        }
        urlDetails[0] = url.getHost();
        urlDetails[1] = String.valueOf(port);
        urlDetails[2] = url.getPath();
        tryConnect(urlDetails);
        startListening();
    }

    /** 
     * Try to connect to the stream.  This method might be called more than once
     * if it fails the first time, or is called with a new address due to a redirect
     */
    @Override
    protected void tryConnect(String[] info) {
        try {
            log.setStatus("Trying " + urlDetails[0]);
            log.printStatus();
            int port = Integer.parseInt(info[1]);

            connection = new Socket(info[0], port);

            connected = connection.isConnected();
            log.setStatus("Connected: " + String.valueOf(connected));
            log.printStatus();
        } catch (UnknownHostException e) {
            log.printException(e, "tryConnect: Host not found", false);
        } catch (ConnectException e) {
            log.printException(e, "tryConnect: Connect to host failed", false);
        } catch (IOException e) {
            log.printException(e, "tryConnect: Socket I/O Error", true);
        } catch (NullPointerException e) {
            log.printException(e, "tryConnect: Host url is null", false);
        }

        if (connected == true) {
            try {
                socketReader = new DataInputStream(connection.getInputStream());
                socketWriter = new DataOutputStream(connection.getOutputStream());
            } catch (IOException e) {
                System.err.println("tryConnect: I/O Error");
            }
        }

    }

    /** 
     * Collect information from the server (make sure its a SHOUTcast server)
     * @throws IOException thrown if something goes wrong writing or reading from the server
     */
    @Override
    protected void startListening() throws StreamException {
        redirectCount += 1;
        if (urlDetails[2].equals("")) {
            urlDetails[2] = "/";
        }
        String tempURL = urlDetails[2];
        String msg1 = "GET " + tempURL + " HTTP/1.0\r\nIcy-MetaData:1\r\nuser-agent:xmms/1.2.7\r\n\r\n";
        String msg2 = "GET " + targetURL + " HTTP/1.0\r\nIcy-MetaData:1\r\nuser-agent:xmms/1.2.7\r\n\r\n";
        try {
            socketWriter.writeBytes(msg1);
            socketWriter.writeBytes(msg2);
        } catch (IOException e) {
            throw new StreamException("Error writing to stream");
        } catch (NullPointerException e) {
            throw new StreamException("Error writing to stream");
        }
        String icyok = readLine();
        log.setStatus(icyok);
        log.printStatus();
        Matcher icymatch = icy4xxre.matcher(icyok);
        Matcher icyokmatch = icyokre.matcher(icyok);
        if (icymatch.matches() == true) {
            throw new StreamException(icyok);
        }
        if (icyokmatch.matches() != true) {
            if (streamMetadata.containsKey("Location") && redirectCount < 10) {
                log.setStatus("Got redirect: " + streamMetadata.get("Location"));
                log.printStatus();
                targetURL = streamMetadata.get("Location").trim();
                connect();
            } else if (redirectCount >= 10) {
                log.setStatus("Too many redirects, terminating...");
                log.printStatus();
                close();
                System.exit(1);
            }
            close();
            throw new StreamException(icyok);
        }
        log.setStatus("[Waiting for metadata...]");
        log.printStatus();
        getHeader(icyok);
        log.setStatus("[Done connecting to stream...]");
        log.printStatus();
    }

    /** 
     * Read a line from a server
     * @throws StreamException
     */
    @Override
    public String readLine() throws StreamException {
        String buffer = "";
        log.setStatus("[Reading from server...]");
        try {
            char inChar;
            buffer = "";
            while ((inChar = (char) socketReader.readByte()) != '\n') {
                buffer += String.valueOf(inChar);
            }
            buffer = buffer.replaceAll("\r", "").replaceAll("\n", "");

        } catch (IOException e) {
            throw new StreamException(e.getMessage() + "\n");
        }
        return buffer;
    }

    /** 
     * Get the radio stream's header.  The header contains much useful info about the stream
     * (name, genre, meta-int, stream-type, stream's homepage, etc..)
     * @throws StreamException thrown if this isn't a shoutcast stream
     */
    protected void getHeader(String icy) throws StreamException {
        String temp = "#####";
        while (temp.length() > 0) {
            temp = readLine();
            log.setStatus(temp);
            log.printStatus();
            int index = temp.indexOf(":");
            if (index >= 0) {
                streamMetadata.put(temp.substring(0, index).trim(), temp.substring(
                        index + 1, temp.length()).trim());
            }
        }
    }

    /** 
     * Returns a string representation of the stream.
     * If the stream is not connected or does not currently
     * have a title, a status message is returned.  Otherwise,
     * the name of the stream is returned
     */
    @Override
    public String toString() {
        if (streamMetadata != null && streamMetadata.containsKey("icy-name")) {
            return streamMetadata.get("icy-name");
        } else {
            if (log == null) {
                return this.targetURL;
            }
            return log.getStatus();
        }
    }

    /** 
     * Perform cleanup operations 
     */
    @Override
    public void close() {
        log.setStatus("[Closing stream...]");
        log.printStatus();
        if (connected == true) {
            try {
                socketReader.close();
                socketWriter.close();
                connection.close();
                connected = false;
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /** 
     * Called when this object is garbage collected, this should not be called
     * in normal programming.
     * @throws IOException
     */
    @Override
    protected void finalize() throws IOException {        
        socketReader.close();
        socketWriter.close();
        connection.close();
    }
}
