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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A repeatable input stream for files. This input stream can be repeated an unlimited number of 
 * times, without any limitation on when a repeat can occur.
 * 
 * @author James Murty
 */
public class RepeatableFileInputStream extends InputStream implements IRepeatableInputStream, InputStreamWrapper {
    private final Log log = LogFactory.getLog(RepeatableFileInputStream.class);

    private File file = null;
    private FileInputStream fis = null;
    private int bytesReadTotal = 0;

    /**
     * Creates a repeatable input stream based on a file.
     * 
     * @param file
     * @throws FileNotFoundException
     */
    public RepeatableFileInputStream(File file) throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        this.fis = new FileInputStream(file);
        this.file = file;
    }
    
    /**
     * Resets the input stream to the beginning by creating a new FileInputStream based on the 
     * underlying file. 
     * 
     * @throws UnrecoverableIOException
     * when the FileInputStream cannot be re-created.
     */
    public void repeatInputStream() throws IOException {
        try {
            this.fis.close();
            this.fis = new FileInputStream(file);
            log.debug("Reset after returning " + bytesReadTotal + " bytes");
            bytesReadTotal = 0;
        } catch (IOException e) {
            throw new UnrecoverableIOException("Input stream is not repeatable: " + e.getMessage());
        }
    }
    
    public int available() throws IOException {
        return fis.available();
    }

    public void close() throws IOException {
        fis.close();
    }

    public int read() throws IOException {
        int byteRead = fis.read();
        if (byteRead != -1) {
            bytesReadTotal++;
            return byteRead;
        } else {
            return -1;
        }
    }

    public int read(byte[] arg0, int arg1, int arg2) throws IOException {
        int count = fis.read(arg0, arg1, arg2);
        bytesReadTotal += count;
        return count;
    }

    public InputStream getWrappedInputStream() {
        return this.fis;
    }

}
