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
 * Set status, and optionally print that status to the console 
 */
public class ConsoleOutputStream implements RadioOutputStream {

    // Status message
    protected String statusMsg;

    /** 
     * Constructs a new ConsoleLog with an initial status message 
     * @param status message to be printed
     */
    public ConsoleOutputStream(String status) {
        statusMsg = status;
    }

    /** 
     * Prints the current status message to the console 
     */
    @Override
    public void printStatus() {
        System.out.println(statusMsg);
    }

    /**
     * Takes an exception and prints it to the console
     * @param e exception to be printed
     * @param msg message to be printed, "" for no message
     * @param stackTrace print a stack trace
     */
    @Override
    @SuppressWarnings("CallToThreadDumpStack")
    public void printException(Throwable e, String msg, boolean stackTrace) {
        String message;
        if (msg.equals("")) {
            message = e.getMessage();
        } else {
            message = msg;
        }
        System.out.println(message);
        if (stackTrace) {
            e.printStackTrace();
        }
    }

    /** 
     * Set the current status message 
     */
    @Override
    public void setStatus(String msg) {
        statusMsg = msg;
    }

    /** 
     * Returns the current status message 
     */
    @Override
    public String getStatus() {
        return statusMsg;
    }

    /** 
     * Return a copy of this object 
     */
    @Override
    public RadioOutputStream copy() {
        return new ConsoleOutputStream(statusMsg);
    }
}
