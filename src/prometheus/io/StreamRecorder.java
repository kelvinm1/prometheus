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


/** 
 * Records a Internet radio stream
 */
public interface StreamRecorder extends Runnable {
	
    /** 
     * Start recording the radio stream 
     */
    public void startRecording() throws RecorderException;

    /** 
     * Return the stream that this Recorder is associated with 
     */
    public RadioStream getStream();

    /** 
     * Returns the directory where the recorder is placing its files 
     */
    public String getOutputPath();

    /**
     * Returns the number of streams currently recording     
     */
    public int getCount();

    /** 
     * Stop recording the radio stream 
     */
    public void stopRecording();

    /** 
     * Check to see if the stream is still recording 
     */
    public boolean isRecording();

    /** 
     * Return a string representation of the ripper.
     * If the ripper is currently recording a song,
     * the name of that song is returned.  Otherwise, 
     * the method will return a status message.
     */
    @Override
    public String toString();
	
}