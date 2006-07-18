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
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InterruptableInputStream extends InputStream implements InputStreamWrapper {
    private final Log log = LogFactory.getLog(InterruptableInputStream.class);

    private InputStream inputStream = null;

    private boolean interrupted = false;

    public InterruptableInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    private void maybeInterruptInputStream() throws IOException {
        if (interrupted) {
            log.debug("Input stream interrupted, closing underlying input stream " + 
                this.inputStream.getClass());
            try {
                close();
            } catch (IOException ioe) {
                log.warn("Unable to close underlying InputStream on interrupt");
            }
            throw new IOException("Input Stream Interrupted");
        }
    }

    public int read() throws IOException {
        maybeInterruptInputStream();
        return inputStream.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        maybeInterruptInputStream();
        return inputStream.read(b, off, len);
    }

    public int available() throws IOException {
        maybeInterruptInputStream();
        return inputStream.available();
    }

    public void close() throws IOException {
        inputStream.close();
    }

    public InputStream getWrappedInputStream() {
        return inputStream;
    }

    public void interrupt() {
        interrupted = true;
    }

}
