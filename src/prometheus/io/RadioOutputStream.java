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
 * Keeps track of an object's status and optionally prints that status
 */
public interface RadioOutputStream{
	
    /** 
     * Get the current status 
     */
    public String getStatus();

    /** 
     * Set the current status 
     * @param msg message to be printed
     */
    public void setStatus(String msg);

    /** 
     * Print the current status (dependent on implementation) 
     */
    public void printStatus();

    /** 
     * Print an exception message 
     * @param e exception to be printed
     * @param msg message to be printed
     * @param stackTrace print a stack trace
     */    
    public void printException(Throwable e, String msg, boolean stackTrace);

    /** 
     * Copy the logger object 
     */
    public RadioOutputStream copy();
}