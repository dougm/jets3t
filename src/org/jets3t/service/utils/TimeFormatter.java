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
    
    private String hourSuffix = null;
    private String hoursSuffix = null;
    private String minuteSuffix = null;
    private String minutesSuffix = null;
    private String secondSuffix = null;
    private String secondsSuffix = null;
    
    public TimeFormatter() {
        this(" hour", " hours", " minute", " minutes", " second", " seconds");
    }
    
    public TimeFormatter(String hourSuffix, String hoursSuffix, String minuteSuffix, 
        String minutesSuffix, String secondSuffix, String secondsSuffix) 
    {
        this.hourSuffix = hourSuffix;
        this.hoursSuffix = hoursSuffix;
        this.minuteSuffix = minuteSuffix;
        this.minutesSuffix = minutesSuffix;
        this.secondSuffix = secondSuffix;
        this.secondsSuffix = secondsSuffix;
    }

    /**
     * Formats a seconds time value into a brief representation, such as <code>37 minutes</code>.
     * Unicode characters are used to represent 1/4, 1/2 and 3/4 fractions.
     * 
     * @param seconds
     * the number of seconds time value.
     * 
     * @return
     * a representation of the time.
     */
    public String formatTime(long seconds) {
        return formatTime(seconds, true);
    }
    
    /**
     * Formats a seconds time value into a brief representation, such as <code>37 minutes</code>.
     * 
     * @param seconds
     * the number of seconds time value.
     * @param useUnicodeChars
     * if true, special unicode characters are used to represent 1/4, 1/2 and 3/4 fractions. 
     * If false, the fractions are displayed in standard text.
     * 
     * @return
     * a representation of the time.
     */
    public String formatTime(long seconds, boolean useUnicodeChars) {
        int hours = 0;
        if (seconds > 3600) {
            hours = (int) seconds / 3600;
            seconds = seconds - (hours * 3600L); 
        } 
        
        int mins = (int) seconds / 60;
        seconds = seconds - (mins * 60L); 
        
        if (hours > 0) {
            if (mins > 45) {
                return (hours + 1) + hourSuffix;
            } else if (mins > 30) {
                if (useUnicodeChars) {
                    return hours + "\u00BE" + hoursSuffix; // Three quarters
                } else {
                    return hours + " 3/4" + hoursSuffix;
                }
            } else if (mins > 15) {
                if (useUnicodeChars) {
                    return hours + "\u00BD" + hoursSuffix; // One half               
                } else {
                    return hours + " 1/2" + hoursSuffix;
                }
            } else if (mins > 0) {
                if (useUnicodeChars) {
                    return hours + "\u00BC" + hoursSuffix; // One quarter               
                } else {
                    return hours + " 1/4" + hoursSuffix;
                }
            } else {
                return hours + " hour" + (hours > 1? "s" : "");                
            }
        } else if (mins > 0) {
            if (seconds > 45) {
                return (mins + 1) + minutesSuffix;
            } else if (seconds > 30) {
                if (useUnicodeChars) {
                    return mins + "\u00BE" + minutesSuffix; // Three quarters
                } else {
                    return mins + " 3/4" + minutesSuffix;
                }
            } else if (seconds > 15) {
                if (useUnicodeChars) {
                    return mins + "\u00BD" + minutesSuffix; // One half
                } else {
                    return mins + " 1/2" + minutesSuffix;                    
                }
            } else if (seconds > 0) {
                if (useUnicodeChars) {
                    return mins + "\u00BC" + minutesSuffix; // One quarter
                } else {
                    return mins + " 1/4" + minutesSuffix;
                }
            } else {
                return mins + (mins > 1? minutesSuffix : minuteSuffix);
            }
        } else {
            return seconds + (seconds != 1? secondsSuffix : secondSuffix);
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
