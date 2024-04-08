package com.lichenaut.worldgrowth.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class WGCopier {

    public static void smallCopy(InputStream in, String outFilePath) throws IOException {
        if (Files.exists(Path.of(outFilePath))) return;

        try (BufferedInputStream bufferedIn = new BufferedInputStream(in);
             BufferedOutputStream bufferedOut = new BufferedOutputStream(new FileOutputStream(outFilePath))) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedIn.read(buffer)) != -1) bufferedOut.write(buffer, 0, bytesRead);
        }
    }
}
