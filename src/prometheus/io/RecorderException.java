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
 * This exception occurs when there is a problem recording an SHOUTcast radio stream
 */
public class RecorderException extends Exception {

    // Exception message
    protected String message;

    /**
     * Create instance of StreamException 
     * @param msg Exception message
     */
    public RecorderException(String msg) {
        message = msg;
    }

    /**
     * Returns the exception message as string.
     * @return Exception message
     */
    @Override
    public String getMessage() {
        return message;
    }
}