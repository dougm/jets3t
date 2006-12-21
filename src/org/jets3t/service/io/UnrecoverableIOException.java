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
package org.jets3t.service.io;

import java.io.IOException;

/**
 * Indicates an IOException that cannot, or should not, be recovered from. For example, if a user
 * deliberately cancels an upload this exception should be thrown to indicate to jets3t that the
 * error was intended.
 *  
 * @author James Murty
 */
public class UnrecoverableIOException extends IOException {

    public UnrecoverableIOException(String message) {
        super(message);
    }    
    
}
