package com.outjected.email.impl.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * @author Cody Lerum
 * 
 */
public class Streams {

    private static final int BUFFER_SIZE = 0x1000;

    public static byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        copy(is, os);
        return os.toByteArray();
    }

    /**
     * Inspired by Guava's ByteStreams copy
     * 
     * @param is
     * @param os
     * @return
     * @throws IOException
     */
    public static long copy(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        long total = 0;
        while (true) {
            int i = is.read(buf);
            if (i == -1) {
                break;
            }
            os.write(buf, 0, i);
            total += i;
        }
        return total;
    }
}
