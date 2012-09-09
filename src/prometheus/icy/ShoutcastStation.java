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


import java.io.IOException;
import java.net.URL;
import java.util.regex.*;
import prometheus.io.BasicPlaylistStream;
import prometheus.io.RadioOutputStream;
import prometheus.io.RadioStation;
import prometheus.io.RadioStream;
import prometheus.io.RecorderException;
import prometheus.io.StreamException;
import prometheus.io.StreamRecorder;


/** 
 * SHOUTcast implementation of Station 
 */
public class ShoutcastStation implements RadioStation {

    protected final Pattern httpPattern = Pattern.compile("http://classic\\.shoutcast\\.com" +
            "/sbin/shoutcast-playlist\\.pls\\?rn=[0-9]+\\&file=filename\\.pls");
    protected final Pattern httpPattern2 = Pattern.compile("http://yp\\.shoutcast\\.com" +
            "/sbin/tunein-station\\.pls\\?id=[0-9]+\\&file=filename\\.pls");
    protected final Pattern filePattern = Pattern.compile("file://yp\\.shoutcast\\.com" +
            "/sbin/tunein-station\\.pls\\?id=[0-9]+\\&file=filename\\.pls");
    protected final Pattern ipPattern = Pattern.compile("http://(\\d{1,3}\\.){3}\\d");
    protected ShoutcastStream stream;
    protected ShoutcastRecorder recorder;
    protected Thread recorderThread;
    protected String outputDir = ".";
    

    /** 
     * Constructs a new ShoutcastStation
     */
    public ShoutcastStation(String url, String outputDir) {
        this.outputDir = outputDir;
        Matcher httpMatcher = httpPattern.matcher(url);
        Matcher httpMatcher2 = httpPattern2.matcher(url);
        if (httpMatcher.matches() || httpMatcher2.matches()) {
            try {
                BasicPlaylistStream playlist = new BasicPlaylistStream(new URL(url).openStream());
                stream = (ShoutcastStream) playlist.getValidStream();
            } catch (IOException e2) {
                System.err.println(e2.getMessage());
                return;
            }
        } else {
            stream = new ShoutcastStream(url);
        }
        recorder = new ShoutcastRecorder(stream, this.outputDir);
    }

    public ShoutcastStation(String url, String outputDir, boolean connect) throws StreamException, IOException {
        this(url, outputDir);
        if (connect) {
            stream.connect();
        }
    }

    /** 
     * Returns the ShoutcastStream for this ShoutcastStation 
     */
    @Override
    public RadioStream getStationStream() {
        return this.stream;
    }

    /** 
     * Returns the ShoutcastRecorder for this ShoutcastStation 
     */
    @Override
    public StreamRecorder getStationRecorder() {
        return this.recorder;
    }

    /** 
     * Returns the logger object for this ShoutcastStation 
     * @param log OutputStream used for logging messages
     */    
    @Override
    public void setLogger(RadioOutputStream log) {
        stream.setLogger(log);
        recorder.setLogger(log);
    }

    /** 
     * Connect to the stream and start recording 
     */
    @Override
    public void run() {
        try {
            if (!stream.isConnected()) {
                stream.connect();
            }
            recorder.startRecording();
        } catch (StreamException e) {
            System.err.println("StreamException: "+e.getMessage());  
            
        } catch (RecorderException e) {
            System.err.println("RecorderException: "+e.getMessage());   
            
        }
    }

    /** 
     * Stop recording and close any open files/sockets 
     */
    @Override
    public void close() {
        recorder.stopRecording();
    }

    /** 
     * Returns a String representing this ShoutcastStation 
     */
    @Override
    public String toString() {
        String streamMsg = stream.toString();
        String recorderMsg = recorder.toString();
        if (recorderMsg.equals("")) {
            recorderMsg = "Waiting..";
        }
        return streamMsg + "\n" + recorderMsg;
    }
}
