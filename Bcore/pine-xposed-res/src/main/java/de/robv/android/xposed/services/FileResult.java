package de.robv.android.xposed.services;

import java.io.InputStream;

/**
 * Holder for the result of a {@link BaseService#readFile} or {@link BaseService#statFile} call.
 *
 * <p>Instances carry either the file content (as a byte array or input stream), the file size,
 * and the last modification time. When a file has not changed since the last read, the content
 * fields will be {@code null} but the size and time fields are still populated.
 */
public final class FileResult {
    /** File content, might be {@code null} if the file wasn't read. */
    public final byte[] content;
    /** File input stream, might be {@code null} if the file wasn't read. */
    public final InputStream stream;
    /** File size. */
    public final long size;
    /** File last modification time. */
    public final long mtime;

    /** @hide */
    /*package*/ FileResult(long size, long mtime) {
        this.content = null;
        this.stream = null;
        this.size = size;
        this.mtime = mtime;
    }

    /** @hide */
    /*package*/ FileResult(byte[] content, long size, long mtime) {
        this.content = content;
        this.stream = null;
        this.size = size;
        this.mtime = mtime;
    }

    /** @hide */
    /*package*/ FileResult(InputStream stream, long size, long mtime) {
        this.content = null;
        this.stream = stream;
        this.size = size;
        this.mtime = mtime;
    }

    /** @hide */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        if (content != null) {
            sb.append("content.length: ");
            sb.append(content.length);
            sb.append(", ");
        }
        if (stream != null) {
            sb.append("stream: ");
            sb.append(stream.toString());
            sb.append(", ");
        }
        sb.append("size: ");
        sb.append(size);
        sb.append(", mtime: ");
        sb.append(mtime);
        sb.append("}");
        return sb.toString();
    }
}