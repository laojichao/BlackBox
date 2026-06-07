package top.niunaijun.blackbox.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Utility class for safely closing one or more {@link Closeable} resources.
 * Suppresses any {@link IOException} thrown during close operations to simplify
 * cleanup code in finally blocks.
 */
public class CloseUtils {
    /**
     * Closes all provided {@link Closeable} resources, silently ignoring any
     * {@link IOException} thrown during the close operation. Null entries are skipped.
     *
     * @param closeables the closeable resources to close; may be {@code null}
     */
    public static void close(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
