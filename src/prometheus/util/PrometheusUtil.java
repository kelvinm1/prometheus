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
package prometheus.util;

import prometheus.Resources;
import prometheus.io.BasicPlaylistStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods used by the Prometheus application.
 * @author kelvin
 */
public class PrometheusUtil {
    /**
     * Return a string representing the approximate amount of memory specified in bytes.
     * @param l memory in bytes
     * @return string approximating the amount of memory
     */
    public static String memSizeToString(long l) {
        String[] sizes = new String[]{"byte", "kilobyte", "megabyte",
            "gigabyte"};
        double d = l;
        int i = 0;
        while ((d >= 1024) && (i < sizes.length)) {
            ++i;
            d /= 1024;
        }
        if (i >= sizes.length) {
            i = sizes.length - 1;
        }
        StringBuilder sb = new StringBuilder();
        long whole = (long) d;
        if (whole == d) {
            if (whole == 1) {
                sb.append(whole);
                sb.append(' ');
                sb.append(sizes[i]);
            } else {
                sb.append(whole);
                sb.append(' ');
                sb.append(sizes[i]);
                sb.append('s');
            }
        } else {
            // two decimal digits
            DecimalFormat df = new DecimalFormat("#.00");
            sb.append(df.format(d));
            sb.append(' ');
            sb.append(sizes[i]);
            sb.append('s');
        }
        return sb.toString();
    }

    public static boolean isNullOrEmpty(String s) {
        if (s == null || s.isEmpty()) {
            return true;
        }

        return false;
    }
    
    public static String getVersion() {
        String version = Resources.majorVersion + "." 
                + Resources.minorVersion + "." 
                + Resources.buildNum;                
        
        return version;
    }
    
    /**
     * Verifies that specified URL or file is a valid SHOUTcast station playlist
     * @param url Station or playlist URL (http: or file:)
     * @return A valid Stream URL to use for recording
     */
    public static String getValidUrl(String url) throws MalformedURLException, IOException {
        if (url.toLowerCase().endsWith(".pls")) {
            BasicPlaylistStream plist;
            try {
                if (!url.toLowerCase().startsWith("http:") && !url.toLowerCase().startsWith("file:")) {
                    File f = new File(url);

                    if (f.exists()) {
                        url = f.toURI().toURL().toString();
                    } else {
                        return "";
                    }
                }

                plist = new BasicPlaylistStream(new URL(url).openStream());
                url = plist.getValidStream().getTarget();
            } catch (MalformedURLException ex) {
                throw new MalformedURLException("URL address provided was invalid format.");
            } catch (IOException ex) {
                throw new IOException("Unable to establish connection to radio station");
            }
        } else {
            try {
                URL address = new URL(url);
            } catch (MalformedURLException e) {                
                throw e;
            }

        }

        return url;
    }
    
    /**
     * Parses a PLS or M3U file for valid station addresses
     * @param path the location and name of the PLS/M3U playlist file
     * @return a String that contains the stream data
     */
    public static String[][] anlyseFile(String path) throws FileNotFoundException, IOException {

        File playlistFile = new File(path);
        System.out.println("Loading file ....");

        // String[i][0] = Title
        // String[i][1] = Address
        // String[i][2] = Web site
        String[][] streams = null;
                        
        List<String> titleVector = new ArrayList<>();    // saves the Stream names
        List<String> addressVector = new ArrayList<>();  // saves the Stream address
        List<String> websiteVector = new ArrayList<>();  // saves the website address

        if (playlistFile.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(path));
                System.out.println("Successfully loaded ....");

                String readBuffer = "";
                String address = "";
                String title = "";
                String website = "";

                if (path.endsWith(".pls")) {
                    
                    while ((readBuffer = br.readLine()) != null) {
                        if (!address.equals("") && readBuffer.startsWith("File")) {
                            // add name, address, website temporary in List                            
                            titleVector.add(address);
                            addressVector.add(title);
                            websiteVector.add(website);

                            // reset address, title, and website
                            address = "";
                            title = "";
                            website = "";
                        }

                        if (readBuffer.trim().startsWith("File")) {
                            address = readBuffer.substring(readBuffer.indexOf("=") + 1, readBuffer.length());
                        }

                        if (readBuffer.trim().startsWith("Title")) {
                            title = readBuffer.substring(readBuffer.indexOf("=") + 1, readBuffer.length());
                        }

                        if (readBuffer.trim().startsWith("Website")) {
                            website = readBuffer.substring(readBuffer.indexOf("=") + 1, readBuffer.length());
                        }
                    }
                    // add the last one
                    titleVector.add(address);
                    addressVector.add(title);
                    websiteVector.add(website);

                    //copy in to an String[][]
                    streams = new String[titleVector.size()][3];

                    for (int i = 0; i < streams.length; i++) {
                        streams[i][0] = titleVector.get(i).toString();
                        streams[i][1] = addressVector.get(i).toString();
                        streams[i][2] = websiteVector.get(i).toString();
                    }
                    return streams;
                } else if (path.endsWith("m3u")) {
                    //first look if it is an extended m3u
                    readBuffer = br.readLine();

                    //its an extm3u playlist
                    if (readBuffer.trim().equals("#EXTM3U")) {
                        while ((readBuffer = br.readLine()) != null) {
                            if (readBuffer.trim().startsWith("#EXTINF:")) {
                                address = readBuffer.substring(readBuffer.indexOf(",") + 1, readBuffer.length());

                                //And now read the next line and ignore all comments
                                while ((readBuffer = br.readLine()) != null) {
                                    if (readBuffer.trim().startsWith("#EXTSTRIPPER:")) {
                                        website = readBuffer.substring(readBuffer.indexOf("=") + 1, readBuffer.length());
                                    }

                                    if (!(readBuffer.trim().startsWith("#"))) {
                                        title = readBuffer;
                                        break;
                                    }
                                }
                                titleVector.add(title);
                                addressVector.add(address);
                                websiteVector.add(website);
                                title = "";
                                address = "";
                                website = "";
                            }
                        }
                        //copy in to an String[][]

                        streams = new String[titleVector.size()][3];

                        for (int i = 0; i < streams.length; i++) {
                            streams[i][0] = titleVector.get(i).toString();
                            streams[i][1] = addressVector.get(i).toString();
                            streams[i][2] = websiteVector.get(i).toString();
                        }
                        return streams;
                    } //its not an extm3u -> a normal m3u
                    else {
                        if (!readBuffer.startsWith("#") || !readBuffer.trim().equals("")) {
                            titleVector.add("");
                            addressVector.add(readBuffer);
                            websiteVector.add("");
                        }

                        while ((readBuffer = br.readLine()) != null) {
                            if (!readBuffer.startsWith("#") || !readBuffer.trim().equals("")) {
                                titleVector.add("");
                                addressVector.add(readBuffer);
                                websiteVector.add("");
                            }

                            while ((readBuffer = br.readLine()) != null) {
                                if (!readBuffer.startsWith("#") || !readBuffer.trim().equals("")) {
                                    titleVector.add("");
                                    addressVector.add(readBuffer);
                                    websiteVector.add("");
                                }
                            }
                        }

                        //copy in to an String[][]
                        streams = new String[titleVector.size()][3];

                        for (int i = 0; i < streams.length; i++) {
                            streams[i][1] = titleVector.get(i).toString();
                            streams[i][0] = addressVector.get(i).toString();
                            streams[i][2] = websiteVector.get(i).toString();
                        }
                        return streams;
                    }
                }

                br.close();
            } catch (FileNotFoundException e) {
                throw e;               
            } catch (IOException e) {
                throw e;                
            }
        } 
        
        return streams;
    }
    
}
