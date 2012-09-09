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

import com.beaglebuddy.mp3.MP3;
import java.io.*;
import java.net.URL;
import java.util.*;
import org.apache.commons.io.*;
import prometheus.Resources;
import prometheus.io.BasicPlaylistStream;
import prometheus.io.BasicRecorder;
import prometheus.io.RadioStream;
import prometheus.io.RecorderException;
import prometheus.io.StreamException;


/** 
 * SHOUTcast implementation of Recorder
 */
public class ShoutcastRecorder extends BasicRecorder {

    // Set overwrite option as disabled
    protected boolean overwrite = false;
    
    // Set option to set ID3v1 tags as enabled
    protected boolean addID3Tags = true;
    
    // Set option to add sequence as disabled
    protected boolean addSequence = false;
    
    // Output stream for writing mp3 file
    protected BufferedOutputStream bufferedOutputFile;

    /** 
     * Create a new ShoutcastRecorder.
     * @param stream stream to rip
     * @param output folder to place output files in
     */
    public ShoutcastRecorder(RadioStream stream, String output) {
        this.stream = stream;
        this.streamMetadata = stream.getStreamMetadata();
        this.outputDir = output;
        this.currentRip = null;
    }
    
    /**
     * Set flag to overwrite file if one already exists
     * @param enabled true/false
     */
    public void setOverwriteFlag(boolean enabled) {
        overwrite = enabled;
    }
    
    /**
     * Returns the state of the overwrite flag     
     */
    public boolean getOverwriteFlag() {
        return overwrite;
    }
    
    /**
     * Set flag to add ID3v1 tag information to mp3 file
     * @param enabled true/false
     */
    public void setID3TagFlag(boolean enabled) {
        addID3Tags = enabled;
    }
    
    /**
     * Returns the state of the ID3 tag flag
     */
    public boolean getID3TagFlag() {
        return addID3Tags;
    }
    
    /**
     * Set flag to add number sequence to file name if it already exist
     * @param enabled true/false
     */
    public void setSequenceFlag(boolean enabled) {
        addSequence = enabled;
    }
    
    /**
     * Returns the state of the add sequence flag     
     */
    public boolean getSequenceFlag() {
        return addSequence;
    }

    /** 
     * Starts the recording thread 
     */
    @Override
    public void run() {
        try {
            startRecording();
        } catch (RecorderException e) {
            System.err.println(e.getMessage());
        }
    }

    /** 
     * Start recording the radio stream
     * @throws RecorderException     
     */
    @Override
    public void startRecording() throws RecorderException {
        getIOStreams();
        while (closing == false) {
            byte[] buffer = new byte[Integer.parseInt(streamMetadata.get("icy-metaint"))];
            try {
                streamReader.readFully(buffer);
                onPacket(buffer);
            } catch (IOException e) {
                if (closing == true) {
                    return;
                }
                log.setStatus("Error reading block");
                log.printException(e, log.getStatus(), false);
            }
            byte headerByte = 0;
            int icyBytes = 0;
            byte[] metaData = null;
            try {
                headerByte = streamReader.readByte();
                icyBytes = headerByte * 16;
                metaData = new byte[icyBytes];

            } catch (IOException e) {
                if (closing == false) {
                    stopRecording();
                    throw new RecorderException("Error recieving header byte");
                }
            }
            if (icyBytes > 0) {
                readMetadata(metaData);
            }

        }
    }

    /** 
     * Gets I/O streams from the ShoutcastStream object. If the radio stream isn't connected,
     * that is done also.
     * @throws RecorderException thrown if the stream could not be connected     
     */
    protected void getIOStreams() throws RecorderException {
        if (stream == null || stream.isConnected() == false) {
            try {
                stream.connect();
            } catch (StreamException e) {
                if (e.getMessage().equals("Playlist address")) {
                    try {
                        BasicPlaylistStream playlist = new BasicPlaylistStream(new URL(stream.getTarget()).openStream());
                        stream = playlist.getValidStream();
                        ((ShoutcastStream) stream).setLogger(this.log);
                        stream.connect();
                        this.streamMetadata = stream.getStreamMetadata();
                    } catch (IOException e2) {
                        System.err.println(e2.getMessage());
                        return;
                    } catch (StreamException e3) {
                        System.err.println(e3.getMessage());
                        return;
                    }
                } else {
                    throw new RecorderException("Could not connect stream: " + e.getMessage());
                }
            }
        }
        try {
            streamReader = stream.getReader();
        } catch (StreamException e) {
            throw new RecorderException(e.getMessage());
        }
        log.setStatus("Download directory is: " + outputDir);
        log.printStatus();
    }

    /** 
     * Reads the radio stream's metadata.  Metadata is passed to getMetadata
     * to be processed
     * @param metaData array of bytes to read the metadata into
     * @throws RecorderException thrown when problems reading the metadata occur
     */
    protected void readMetadata(byte[] metaData) throws RecorderException {
        try {
            streamReader.readFully(metaData);
        } catch (IOException e) {
            throw new RecorderException("Error reading metadata from stream");
        }
        String metaString = new String(metaData);
        for (int firstZero = 0; (firstZero <= metaData.length) && (metaData[firstZero] != 0); firstZero++) {
            metaString += (char) metaData[firstZero];
        }
        streamMetadata.putAll(getMetadata(metaString.getBytes()));
        onNewMetadata(streamMetadata);
    }

    /** 
     * Process metadata bytes
     * @param meta array of bytes holding the metadata
     * @return a map containing the extracted metadata
     */
    @Override
    protected Map<String, String> getMetadata(byte[] meta) {
        String[] newMeta = new String(meta).split(";");
        Map<String, String> metaMap = new HashMap<String, String>();
        for (String tag : newMeta) {
            int index = tag.indexOf("=");
            if (index >= 0) {
                metaMap.put(tag.substring(0, index).trim(), dequote(tag.substring(index + 1)).trim());
            }
        }
        return metaMap;
    }

    /** 
     * Handle a packet of mp3 data and write it to the mp3
     * @param packet packet of mp3 data
     * @throws RecorderException thrown if there are problems writing to the mp3
     */
    @Override
    protected void onPacket(byte[] packet) throws RecorderException {
        long bytes = Long.parseLong(streamMetadata.get("song-bytes"));
        bytes += packet.length;
        streamMetadata.put("song-bytes", String.valueOf(bytes));
        if (writing == true) {
            try {
                bufferedOutputFile.write(packet);
                //System.out.println(streamMetadata.toString());
                setChanged();
                notifyObservers();
            } catch (IOException e) {
                stopRecording();
                throw new RecorderException("Error writing to file");
            }
        }
    }

    /** 
     * Process new metadata.  If the new metadata has a different stream title,
     * onStreamChange is called with the new title.
     * @param meta map of the new metadata
     * @throws RecorderException thrown if there are problems creating the output file
     */
    @Override
    protected void onNewMetadata(Map meta) throws RecorderException {
        String currentSong = stream.getCurrentTrack();
        if (!currentSong.equals(meta.get("StreamTitle")) && meta.get("StreamTitle") != null) {
            currentSong = (String) meta.get("StreamTitle");
            onStreamChange(currentSong);
            recCount++;
        }
    }
        
    /** 
     * Called whenever the name of the stream changes.
     * This usually indicates the start of a new track.
     * @param newName the stream's new title
     * @throws RecorderException
     * @throws IOException thrown if there are problems creating the output file
     */
    @Override
    protected void onStreamChange(String newName) throws RecorderException {
        String path = outputDir + System.getProperty("file.separator") + cleanFilename(streamMetadata.get("icy-name"));
        if (writing == true) {
            try {
                outputFile.close();
                if (currentRip.getPath().contains("incomplete") == false) {
                    File destFile = new File(path + System.getProperty("file.separator") + currentRip.getName());
                                  
                    
                    if (destFile.exists() && !overwrite) {
                        log.setStatus(("File already exists. Overwrite option is OFF."));
                    } else {
                        
                        if (addSequence) {
                            int fileCount = 0;                

                            // while the file exists, count the number of copies
                            // add the next sequence to the file name
                            while ((destFile.exists())) {
                                fileCount++;
                                destFile = new File(path + System.getProperty("file.separator") + FilenameUtils.removeExtension(currentRip.getName()) + "_" + fileCount + ".mp3");
                            } // end while loop
                        }
                        
                        // Replace mp3 file if file overwrite is turned ON
                        FileUtils.copyFile(currentRip, destFile);
                        log.setStatus("Save file as: " + destFile.getAbsolutePath());        
                        log.printStatus();
                        
                        // Apply ID3v1 tags to mp3 file
                        if (addID3Tags) {
                            log.setStatus("[Adding ID3v1 tags...]");
                            setID3v1Tag(destFile);
                        }
                    }
                        
                    log.printStatus();
                    
                    FileUtils.forceDelete(currentRip);
                    
                }
            } catch (IOException e) {
                log.printException(e, "Error closing file" + outputFile, false);
            } 
            log.setStatus("[Changing Songs...]");
            log.printStatus();
        }
        String filename = streamMetadata.get("StreamTitle");
        filename = cleanFilename(filename);
        if (firstSong == true) {
            firstSong = false;
            path += (System.getProperty("file.separator") + "incomplete");
        } else {
            path += (System.getProperty("file.separator") + "work");
        }
        if (streamMetadata.containsKey("content-type") == false) {
            streamMetadata.put("content-type", "x-audio/mp3");
        }
        if (streamMetadata.get("content-type").matches("video/nsv")) {
            filename += ".nsv";
        } else {
            filename += ".mp3";           
        }
        log.setStatus("Filename: " + filename);
        log.printStatus();
            
        File filePath = new File(path, filename);        
        log.setStatus("Location: " + path);
        log.printStatus();
        if (filePath.getParentFile().exists() == true) {
            log.setStatus("Directory exists");
        } else {
            filePath.getParentFile().mkdirs();
        }
        try {
            outputFile = new DataOutputStream(new FileOutputStream(filePath));
            bufferedOutputFile = new BufferedOutputStream(outputFile);
            writing = true;
            currentRip = filePath;
            log.setStatus("[Recording Song...]");
            log.printStatus();
            streamMetadata.put("song-bytes", "0");
        } catch (IOException e) {
            throw new RecorderException("Error creating output file" + outputFile);
        }
        setChanged();
        notifyObservers();
    }

    /** 
     * Remove quotes, semicolons, and backslashes from the beginning and end of the string
     * @param str string to be dequoted
     * @return str with quotes, semicolons, and backslashes from its beginning and end
     */
    protected String dequote(String str) {
        String newStr = str.trim();
        if ((newStr.charAt(0) == '\"' && newStr.charAt(newStr.length() - 1) == '\"')
                || (newStr.charAt(0) == '\'' && newStr.charAt(newStr.length() - 1) == '\'')) {
            newStr = newStr.substring(1, newStr.length());
        }
        if (newStr.charAt(newStr.length() - 1) == ';' || newStr.charAt(newStr.length() - 1) == '\'') {
            newStr = newStr.substring(0, newStr.length() - 1);
        }
        return newStr;
    }

    /** 
     * Purge any illegal characters from a filename
     * @param filename filename to be cleaned
     */
    protected String cleanFilename(String filename) {
        filename = dequote(filename);
        filename = filename.replaceAll("\\\\|\\+|\\*|\\:|\\?|/", "-");
        filename = filename.replaceAll(";", "");
        if (filename.charAt(filename.length() - 1) == '\'') {
            filename = filename.substring(0, filename.length() - 1);
        }
        return filename.trim();
    }
    
    /**
     * Tag a recorded mp3 using ID3v1. Since most mp3s are named in the format
     * [artist] - [title].mp3, this is how the mp3s are tagged.
     * @param mp3 MP3 file to be tagged
     * @throws IOException thrown if there are problems r/w to the mp3
     * @throws Mp3Exception thrown if there are problems getting properties of mp3
     * @throws Id3Exception thrown if there are problems tagging the mp3
     */
    protected void setID3v1Tag(File mp3) throws IOException {
        String[] tagEntries = mp3.getName().split(" - ");
        MP3 mp3File = new MP3(mp3);        
        mp3File.setBand(tagEntries[0]);
        if (tagEntries.length > 1) {
            mp3File.setTitle(tagEntries[1].replace(".mp3", "").trim());
        }
        mp3File.setComments("Ripped by " + Resources.applicationName);
        mp3File.save();
    }              

    /** 
     * Perform cleanup operations 
     */
    @Override
    public void stopRecording() {
        closing = true;
        log.setStatus("[Closing recorder...]");
        log.printStatus();
        writing = false;
        try {
            if (streamWriter != null) {
                streamWriter.close();
            }
            if (streamReader != null) {
                streamReader.close();
            }
            if (outputFile != null) {
                outputFile.close();
            }
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            System.err.println("I/O error while closing");
        }
    }

    /**
     * Is stream recording
     */
    @Override
    public boolean isRecording() {
        if (closing && !writing) {
            return false; //was true
        } else {
            return true; //was false
        }
    }

    /** 
     * Return a string representation of the ripper.
     * If the ripper is currently recording a song,
     * the name of that song is returned.  Otherwise,
     * the method will return a status message.
     */
    @Override
    public String toString() {
        if (currentRip != null && !currentRip.getName().equals("")) {
            return currentRip.getName();
        } else {
            return log.getStatus();
        }
    }

    /** 
     * Called when this object is garbage collected, this should not be called
     * in normal programming.
     */
    @Override
    @SuppressWarnings("FinalizeDeclaration")
    protected void finalize() throws Throwable {
        try {
            streamWriter.close();
            streamReader.close();
            outputFile.close();
            stream.close();

        } finally {
            super.finalize();
        }

    }
}
