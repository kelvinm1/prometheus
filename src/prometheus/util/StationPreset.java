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

/**
 * Represents the Name/URL key value pair for the SHOUTcast station.
 *  
 */
public class StationPreset extends Object {

    // Station name
    protected String name;
    
    // Station HTTP web address
    protected String urlString;
    
    /**
     * Creates an instance of a Preset station setting.
     * @param name Station name
     * @param urlString Station HTTP URL
     */
    public StationPreset(String name, String urlString) {
        this.name = name; 
        this.urlString = urlString;
    }

    /**
     * Get station name
     * @return Station name
     */
    public String getName() {
        return name;
    }

    /**
     * Get station URL
     * @return Station HTTP URL
     */
    public String getURLString() {
        return urlString;
    }

    /**
     * Uses station name as object string representation.
     * @return Station name
     */
    @Override
    public String toString() {
        return name;
    }
}
