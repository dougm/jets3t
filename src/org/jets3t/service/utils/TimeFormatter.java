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
        String result = "";
        if (seconds > 3600) {
            int hours = (int) seconds / 3600;
            result = hours + ":";
            seconds = seconds - (hours * 3600); 
        } 
        
        int mins = (int) seconds / 60;
        result += (mins < 10 ? "0" : "") + mins + ":";
        seconds = seconds - (mins * 60);
        
        result += (seconds < 10 ? "0" : "") + seconds;
        
        return result;
    }

}
