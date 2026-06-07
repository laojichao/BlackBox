package top.canyie.dreamland.utils;

import android.system.ErrnoException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Utility class for common I/O operations used by the Xposed compatibility layer.
 *
 * <p>Provides convenience methods for reading files into strings, writing strings to files,
 * closing resources silently, ensuring directory existence, and extracting POSIX errno values
 * from IOExceptions.
 */
public final class IOUtils {
    private static final String TAG = "IOUtils";
    private IOUtils() {}

    /**
     * Reads the entire content of a file into a string.
     *
     * @param file the file to read
     * @return the file content as a string
     * @throws IOException if an I/O error occurs
     */
    public static String readAllString(File file) throws IOException {
        return readAllString(new FileReader(file));
    }

    /**
     * Reads the entire content of an input stream into a string.
     *
     * @param input the input stream to read
     * @return the stream content as a string
     * @throws IOException if an I/O error occurs
     */
    public static String readAllString(InputStream input) throws IOException {
        return readAllString(new InputStreamReader(input));
    }

    /**
     * Reads all lines from a {@link Reader} and returns them as a single string, with lines
     * separated by newline characters.
     *
     * @param input the reader to read from
     * @return the content as a string
     * @throws IOException if an I/O error occurs
     */
    public static String readAllString(Reader input) throws IOException {
        BufferedReader br = new BufferedReader(input);
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } finally {
            closeQuietly(br);
        }
    }

    /**
     * Writes a string to a file, creating or overwriting it.
     *
     * @param file the file to write to
     * @param content the string content to write
     * @throws IOException if an I/O error occurs
     */
    public static void writeToFile(File file, String content) throws IOException {
        FileWriter fw = new FileWriter(file);
        try {
            fw.write(content);
        } finally {
            closeQuietly(fw);
        }
    }

    /**
     * Closes a {@link Closeable} resource, logging and swallowing any {@link IOException}.
     *
     * @param closeable the resource to close, may be {@code null}
     */
    public static void closeQuietly(Closeable closeable) {
        if(closeable != null) {
            try {
                closeable.close();
            } catch(IOException e) {
                Log.e(TAG, "Error while closing Closeable " + closeable, e);
            }
        }
    }

    /**
     * Ensures that the given directory exists, creating it (and any parent directories) if needed.
     *
     * @param directory the directory to ensure
     * @return the same directory reference
     * @throws IllegalStateException if the directory cannot be created
     */
    public static File ensureDirectoryExisting(File directory) {
        if (!(directory.exists() || directory.mkdirs() || directory.exists())) {
            throw new IllegalStateException("Can't create directory: " + directory.getAbsolutePath());
        }
        return directory;
    }

    /**
     * Extracts the POSIX errno value from an {@link IOException} by walking its cause chain
     * looking for an {@link ErrnoException}.
     *
     * @param e the IOException to inspect
     * @return the errno value, or 0 if no ErrnoException is found in the cause chain
     */
    public static int getErrno(IOException e) {
        for (Throwable cause = e;cause != null;cause = cause.getCause()) {
            if (cause instanceof ErrnoException) {
                return ((ErrnoException) cause).errno;
            }
        }
        return 0;
    }
}
