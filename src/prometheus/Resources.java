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
package prometheus;

import java.io.File;
import java.util.ResourceBundle;

/**
 * This class manages resource files used by the project.
 */
public class Resources {

    // Load bundled resources from properties class. */
    public static final ResourceBundle APP_RESOURCE = ResourceBundle.getBundle("prometheus.app");
    public static final ResourceBundle BUILD_RESOURCE = ResourceBundle.getBundle("prometheus.build");    
    
    /**
     * APP RESOURCES
     */
    
    // Set application home directory
    public static final File applicationDirectory = new File(System.getProperty("user.home"), 
            APP_RESOURCE.getString("application.dirname"));    
    
    // Set output directory for downloaded MP3s 
    public static final File downloadDirectory = new File(applicationDirectory, 
            APP_RESOURCE.getString("application.library"));
    
    public static final String applicationName = APP_RESOURCE.getString("application.name");
    
    /**
     * BUILD RESOURCES
     */
    public static final String buildNum = BUILD_RESOURCE.getString("build_num");
    public static final String majorVersion = BUILD_RESOURCE.getString("major_version");
    public static final String minorVersion = BUILD_RESOURCE.getString("minor_version");
            
}
