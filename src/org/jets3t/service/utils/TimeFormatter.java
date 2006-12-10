/*
 * jets3t : Java Extra-Tasty S3 Toolkit (for Amazon S3 online storage service)
 * This is a java.net project, see https://jets3t.dev.java.net/
 * 
 * Copyright 2006 James Murty
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.jets3t.service.utils;

/**
 * Formats time values into human-readable strings.
 * 
 * @author James Murty
 */
public class TimeFormatter {

    /**
     * Formats a seconds time value into a brief representation: <code>h:mm:ss</code>.
     * If the time value is less than one hour, the hours component will not be displayed.
     * 
     * @param seconds
     * the number of seconds time value.
     * 
     * @return
     * a representation of the time.
     */
    public String formatTime(long seconds) {
        int hours = 0;
        if (seconds > 3600) {
            hours = (int) seconds / 3600;
            seconds = seconds - (hours * 3600); 
        } 
        
        int mins = (int) seconds / 60;
        seconds = seconds - (mins * 60); 
        
        if (hours > 0) {
            if (mins > 45) {
                return (hours + 1) + " hours";
            } else if (mins > 30) {
                return hours + "\u00BE hours"; // Three quarters               
            } else if (mins > 15) {
                return hours + "\u00BD hours"; // One half               
            } else if (mins > 0) {
                return hours + "\u00BC hours"; // One quarter               
            } else {
                return hours + " hour" + (hours > 1? "s" : "");                
            }
        } else if (mins > 0) {
            if (seconds > 45) {
                return (mins + 1) + " minutes";
            } else if (seconds > 30) {
                return mins + "\u00BE minutes"; // Three quarters
            } else if (seconds > 15) {
                return mins + "\u00BD minutes"; // One half
            } else if (seconds > 0) {
                return mins + "\u00BC minutes"; // One quarter
            } else {
                return mins + " minute" + (mins > 1? "s" : "");
            }
        } else {
            return seconds + " second" + (seconds != 1? "s" : "");
        }
    }
    
//    public static void main(String[] args) throws Exception {
//        TimeFormatter formatter = new TimeFormatter();
//        long seconds = 2 * 60 * 60;
//        
//        while (seconds >= 0) {
//            System.out.println(formatter.formatTime(seconds));
//            Thread.sleep(5);
//            seconds--;
//        }
//                
//    }

}
