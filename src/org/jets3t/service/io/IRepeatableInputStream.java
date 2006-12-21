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
 * A marker for an input stream that can be repeated, at least under some circumstances.
 * An input stream is repeatable if it can be reset to its starting point and re-read.
 * <p>
 * Such input streams are useful when transmitting data, as a transmission failure can be recovered
 * from by re-transmitting with repeated data from the input stream.
 * 
 * @author James Murty
 */
public interface IRepeatableInputStream {

    /**
     * Resets the input stream to the beginning. After this method call, the input stream will 
     * provide data as if it was just created. 
     * 
     * @throws IOException
     * when the input stream cannot be repeated, such as if the amount of buffered repeatable data
     * is insufficient. Ideally this method should throw an UnrecoverableIOException to indicate
     * that no further IO operations as possible.
     */
    public void repeatInputStream() throws IOException;

}
