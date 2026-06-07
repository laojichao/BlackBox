package top.niunaijun.blackbox.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.system.Os;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class providing comprehensive file system operations including file reading,
 * writing, copying, deletion, permission management, symlink handling, and Parcel
 * serialization. Also includes a file locking mechanism and Unix file mode constants.
 */
public class FileUtils {

    /**
     * Counts the entries in a file or directory. Returns 1 for regular files,
     * the number of direct children for directories, or -1 if the path does not exist.
     *
     * @param file the file or directory to count
     * @return the count of entries, or -1 if the file does not exist
     */
    public static int count(File file) {
        if (!file.exists()) {
            return -1;
        }
        if (file.isFile()) {
            return 1;
        }
        if (file.isDirectory()) {
            String[] fs = file.list();
            return fs == null ? 0 : fs.length;
        }
        return 0;
    }

    /**
     * Extracts the file extension from a filename (the portion after the last dot).
     *
     * @param filename the filename to extract the extension from
     * @return the file extension without the leading dot, or an empty string if no extension exists
     */
    public static String getFilenameExt(String filename) {
        int dotPos = filename.lastIndexOf('.');
        if (dotPos == -1) {
            return "";
        }
        return filename.substring(dotPos + 1);
    }

    /**
     * Returns a new {@link File} with the extension changed to the target extension.
     * If the file already has the target extension, the original file is returned unchanged.
     *
     * @param f the original file
     * @param targetExt the desired extension (without leading dot)
     * @return a new File with the changed extension, or the original file if already matching
     */
    public static File changeExt(File f, String targetExt) {
        String outPath = f.getAbsolutePath();
        if (!getFilenameExt(outPath).equals(targetExt)) {
            int dotPos = outPath.lastIndexOf(".");
            if (dotPos > 0) {
                outPath = outPath.substring(0, dotPos + 1) + targetExt;
            } else {
                outPath = outPath + "." + targetExt;
            }
            return new File(outPath);
        }
        return f;
    }

    /**
     * Renames the original file to the new file path.
     *
     * @param origFile the file to rename
     * @param newFile the target file path
     * @return {@code true} if the rename was successful
     */
    public static boolean renameTo(File origFile, File newFile) {
        return origFile.renameTo(newFile);
    }

    /**
     * Reads the entire contents of a file into a string.
     *
     * @param fileName the absolute path of the file to read
     * @return the file contents as a string
     * @throws IOException if an I/O error occurs while reading
     */
    public static String readToString(String fileName) throws IOException {
        InputStream is = new FileInputStream(fileName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }

    /**
     * Reads the contents of a file into a {@link Parcel} object. The file is expected
     * to contain Parcel-serialized binary data.
     *
     * @param file the file to read
     * @return a {@link Parcel} containing the deserialized file contents, positioned at the start
     * @throws IOException if an I/O error occurs while reading
     */
    public static Parcel readToParcel(File file) throws IOException {
        Parcel in = Parcel.obtain();
        byte[] bytes = toByteArray(file);
        in.unmarshall(bytes, 0, bytes.length);
        in.setDataPosition(0);
        return in;
    }

    /**
     * Sets the POSIX file permissions on the given path. On Android Lollipop and above,
     * uses {@code Os.chmod()}; falls back to the {@code chmod} shell command on older versions.
     *
     * @param path the file or directory path to modify
     * @param mode the POSIX permission mode as an octal integer (see {@link FileMode})
     * @throws Exception if the chmod operation fails
     */
    public static void chmod(String path, int mode) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Os.chmod(path, mode);
                return;
            } catch (Exception e) {
                // ignore
            }
        }

        File file = new File(path);
        String cmd = "chmod ";
        if (file.isDirectory()) {
            cmd += " -R ";
        }
        String cmode = String.format("%o", mode);
        Runtime.getRuntime().exec(cmd + cmode + " " + path).waitFor();
    }

    /**
     * Creates a hard link from the new path to the existing path. On Android Lollipop and above,
     * uses {@code Os.link()}; falls back to the {@code ln -s} shell command on older versions.
     *
     * @param oldPath the path of the existing file
     * @param newPath the path of the new link to create
     * @throws Exception if the link creation fails
     */
    public static void createSymlink(String oldPath, String newPath) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Os.link(oldPath, newPath);
                return;
            } catch (Throwable e) {
                //ignore
            }
        }
        Runtime.getRuntime().exec("ln -s " + oldPath + " " + newPath).waitFor();
    }

    /**
     * Checks whether the given file is a symbolic link by comparing its canonical
     * and absolute paths.
     *
     * @param file the file to check
     * @return {@code true} if the file is a symbolic link
     * @throws IOException if an I/O error occurs while resolving the canonical path
     * @throws NullPointerException if the file is {@code null}
     */
    public static boolean isSymlink(File file) throws IOException {
        if (file == null)
            throw new NullPointerException("File must not be null");
        File canon;
        if (file.getParent() == null) {
            canon = file;
        } else {
            File canonDir = file.getParentFile().getCanonicalFile();
            canon = new File(canonDir, file.getName());
        }
        return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
    }

    /**
     * Serializes the given {@link Parcel} and writes its bytes to the specified file.
     *
     * @param p the Parcel to serialize and write
     * @param file the target file to write to
     * @throws IOException if an I/O error occurs while writing
     */
    public static void writeParcelToFile(Parcel p, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(p.marshall());
        fos.close();
    }

    /**
     * Serializes the given {@link Parcel} and writes its bytes to the specified output stream.
     *
     * @param p the Parcel to serialize and write
     * @param fos the output stream to write to
     * @throws IOException if an I/O error occurs while writing
     */
    public static void writeParcelToOutput(Parcel p, FileOutputStream fos) throws IOException {
        fos.write(p.marshall());
    }

    /**
     * Reads the entire contents of a file into a byte array.
     *
     * @param file the file to read
     * @return a byte array containing all file data
     * @throws IOException if an I/O error occurs while reading
     */
    public static byte[] toByteArray(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            return toByteArray(fileInputStream);
        } finally {
            closeQuietly(fileInputStream);
        }
    }

    /**
     * Reads the entire contents of an input stream into a byte array.
     *
     * @param inStream the input stream to read
     * @return a byte array containing all stream data
     * @throws IOException if an I/O error occurs while reading
     */
    public static byte[] toByteArray(InputStream inStream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        return swapStream.toByteArray();
    }

    /**
     * Recursively deletes a directory and all its contents. Symlinks are not followed.
     *
     * @param dir the directory to delete
     * @return the total number of files and directories successfully deleted
     */
    public static int deleteDir(File dir) {
        int count = 0;
        if (dir.isDirectory()) {
            boolean link = false;
            try {
                link = isSymlink(dir);
            } catch (Exception e) {
                //ignore
            }
            if (!link) {
                String[] children = dir.list();
                for (String file : children) {
                    count += deleteDir(new File(dir, file));
                }
            }
        }
        if (dir.delete()) {
            count++;
        }
        return count;
    }

    /**
     * Recursively deletes a directory and all its contents by path string.
     *
     * @param dir the directory path to delete
     * @return the total number of files and directories successfully deleted
     */
    public static int deleteDir(String dir) {
        return deleteDir(new File(dir));
    }

    /**
     * Writes data from an input stream to a target file using buffered output.
     *
     * @param dataIns the input stream providing the data to write
     * @param target the destination file to write to
     * @throws IOException if an I/O error occurs during writing
     */
    public static void writeToFile(InputStream dataIns, File target) throws IOException {
        final int BUFFER = 1024;
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));
        int count;
        byte data[] = new byte[BUFFER];
        while ((count = dataIns.read(data, 0, BUFFER)) != -1) {
            bos.write(data, 0, count);
        }
        bos.close();
    }

    /**
     * Writes a byte array to a target file using NIO channel transfer for efficiency.
     *
     * @param data the byte array to write
     * @param target the destination file to write to
     * @throws IOException if an I/O error occurs during writing
     */
    public static void writeToFile(byte[] data, File target) throws IOException {
        FileOutputStream fo = null;
        ReadableByteChannel src = null;
        FileChannel out = null;
        try {
            src = Channels.newChannel(new ByteArrayInputStream(data));
            fo = new FileOutputStream(target);
            out = fo.getChannel();
            out.transferFrom(src, 0, data.length);
        } finally {
            if (fo != null) {
                fo.close();
            }
            if (src != null) {
                src.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * Copies data from an input stream to a target file. Exceptions are silently ignored.
     * Both the input stream and output stream are closed after the operation.
     *
     * @param inputStream the source input stream
     * @param target the destination file
     */
    public static void copyFile(InputStream inputStream, File target) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(target);
            byte[] data = new byte[4096];
            int len;
            while ((len = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, len);
            }
            outputStream.flush();
        } catch (Throwable e) {
            //ignore
        } finally {
            closeQuietly(inputStream);
            closeQuietly(outputStream);
        }
    }

    /**
     * Copies the contents of a source file to a target file using NIO channel-based
     * transfer with a 1024-byte buffer.
     *
     * @param source the source file to copy from
     * @param target the destination file to copy to
     * @throws IOException if an I/O error occurs during copying
     */
    public static void copyFile(File source, File target) throws IOException {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(source);
            outputStream = new FileOutputStream(target);
            FileChannel iChannel = inputStream.getChannel();
            FileChannel oChannel = outputStream.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (true) {
                buffer.clear();
                int r = iChannel.read(buffer);
                if (r == -1)
                    break;
                buffer.limit(buffer.position());
                buffer.position(0);
                oChannel.write(buffer);
            }
        } finally {
            closeQuietly(inputStream);
            closeQuietly(outputStream);
        }
    }

    /**
     * Closes a {@link Closeable} resource, silently ignoring any exception.
     *
     * @param closeable the resource to close; may be {@code null}
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Reads a 4-byte integer from a byte array at the given offset with the specified byte order.
     * This method does not validate the offset or bounds.
     *
     * @param bytes the byte array to read from
     * @param value the starting offset in the array
     * @param endian the byte order to use ({@link ByteOrder#BIG_ENDIAN} or {@link ByteOrder#LITTLE_ENDIAN})
     * @return the 4-byte integer value read from the array
     */
    public static int peekInt(byte[] bytes, int value, ByteOrder endian) {
        int v2;
        int v0;
        if (endian == ByteOrder.BIG_ENDIAN) {
            v0 = value + 1;
            v2 = v0 + 1;
            v0 = (bytes[v0] & 255) << 16 | (bytes[value] & 255) << 24 | (bytes[v2] & 255) << 8 | bytes[v2 + 1] & 255;
        } else {
            v0 = value + 1;
            v2 = v0 + 1;
            v0 = (bytes[v0] & 255) << 8 | bytes[value] & 255 | (bytes[v2] & 255) << 16 | (bytes[v2 + 1] & 255) << 24;
        }

        return v0;
    }

    private static boolean isValidExtFilenameChar(char c) {
        switch (c) {
            case '\0':
            case '/':
                return false;
            default:
                return true;
        }
    }

    /**
     * Check if given filename is valid for an ext4 filesystem.
     */
    public static boolean isValidExtFilename(String name) {
        return (name != null) && name.equals(buildValidExtFilename(name));
    }

    /**
     * Mutate the given filename to make it valid for an ext4 filesystem,
     * replacing any invalid characters with "_".
     */
    public static String buildValidExtFilename(String name) {
        if (TextUtils.isEmpty(name) || ".".equals(name) || "..".equals(name)) {
            return "(invalid)";
        }
        final StringBuilder res = new StringBuilder(name.length());
        for (int i = 0; i < name.length(); i++) {
            final char c = name.charAt(i);
            if (isValidExtFilenameChar(c)) {
                res.append(c);
            } else {
                res.append('_');
            }
        }
        return res.toString();
    }

    /**
     * Creates the directory named by this path, including any necessary parent directories.
     *
     * @param path the directory path to create
     */
    public static void mkdirs(File path) {
        if (!path.exists())
            path.mkdirs();
    }

    /**
     * Creates the directory named by this path string, including any necessary parent directories.
     *
     * @param path the directory path string to create
     */
    public static void mkdirs(String path) {
        mkdirs(new File(path));
    }

    /**
     * Checks whether a file or directory exists at the given path.
     *
     * @param path the path to check
     * @return {@code true} if a file or directory exists at the path
     */
    public static boolean isExist(String path) {
        return new File(path).exists();
    }

    /**
     * Checks whether the file at the given path is readable.
     *
     * @param path the file path to check
     * @return {@code true} if the file exists and is readable
     */
    public static boolean canRead(String path) {
        return new File(path).canRead();
    }

    /**
     * Constants representing POSIX file permission mode bits for use with {@link FileUtils#chmod}.
     * Includes owner, group, and other read/write/execute bits as well as setuid, setgid,
     * and sticky bits.
     */
    public interface FileMode {
        int MODE_ISUID = 04000;
        int MODE_ISGID = 02000;
        int MODE_ISVTX = 01000;
        int MODE_IRUSR = 00400;
        int MODE_IWUSR = 00200;
        int MODE_IXUSR = 00100;
        int MODE_IRGRP = 00040;
        int MODE_IWGRP = 00020;
        int MODE_IXGRP = 00010;
        int MODE_IROTH = 00004;
        int MODE_IWOTH = 00002;
        int MODE_IXOTH = 00001;

        int MODE_755 = MODE_IRUSR | MODE_IWUSR | MODE_IXUSR
                | MODE_IRGRP | MODE_IXGRP
                | MODE_IROTH | MODE_IXOTH;
    }

    /**
     * Singleton file locking utility that uses NIO {@link java.nio.channels.FileLock} to
     * provide exclusive file locking with reference counting. Each lock call increments
     * the reference count and each unlock decrements it; the underlying lock is only
     * released when the count reaches zero.
     */
    public static class FileLock {
        private static FileLock singleton;
        private Map<String, FileLockCount> mRefCountMap = new ConcurrentHashMap<String, FileLockCount>();

        /**
         * Returns the singleton instance of {@link FileLock}.
         *
         * @return the shared {@link FileLock} instance
         */
        public static FileLock getInstance() {
            if (singleton == null) {
                singleton = new FileLock();
            }
            return singleton;
        }

        private int RefCntInc(String filePath, java.nio.channels.FileLock fileLock, RandomAccessFile randomAccessFile,
                              FileChannel fileChannel) {
            int refCount;
            if (this.mRefCountMap.containsKey(filePath)) {
                FileLockCount fileLockCount = this.mRefCountMap.get(filePath);
                int i = fileLockCount.mRefCount;
                fileLockCount.mRefCount = i + 1;
                refCount = i;
            } else {
                refCount = 1;
                this.mRefCountMap.put(filePath, new FileLockCount(fileLock, refCount, randomAccessFile, fileChannel));

            }
            return refCount;
        }

        private int RefCntDec(String filePath) {
            int refCount = 0;
            if (this.mRefCountMap.containsKey(filePath)) {
                FileLockCount fileLockCount = this.mRefCountMap.get(filePath);
                int i = fileLockCount.mRefCount - 1;
                fileLockCount.mRefCount = i;
                refCount = i;
                if (refCount <= 0) {
                    this.mRefCountMap.remove(filePath);
                }
            }
            return refCount;
        }

        /**
         * Acquires an exclusive lock on a lock file located in the same parent directory
         * as the target file. The lock is reference-counted, so multiple calls for the
         * same target only increment the counter.
         *
         * @param targetFile the file whose parent directory should be locked
         * @return {@code true} if the lock was successfully acquired
         */
        public boolean LockExclusive(File targetFile) {

            if (targetFile == null) {
                return false;
            }
            try {
                File lockFile = new File(targetFile.getParentFile().getAbsolutePath().concat("/lock"));
                if (!lockFile.exists()) {
                    lockFile.createNewFile();
                }
                RandomAccessFile randomAccessFile = new RandomAccessFile(lockFile.getAbsolutePath(), "rw");
                FileChannel channel = randomAccessFile.getChannel();
                java.nio.channels.FileLock lock = channel.lock();
                if (!lock.isValid()) {
                    return false;
                }
                RefCntInc(lockFile.getAbsolutePath(), lock, randomAccessFile, channel);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * Releases the exclusive lock on the lock file in the same parent directory
         * as the target file. Decrements the reference count and releases the underlying
         * NIO lock only when the count reaches zero.
         *
         * @param targetFile the file whose parent directory lock should be released
         */
        public void unLock(File targetFile) {

            File lockFile = new File(targetFile.getParentFile().getAbsolutePath().concat("/lock"));
            if (!lockFile.exists()) {
                return;
            }
            if (this.mRefCountMap.containsKey(lockFile.getAbsolutePath())) {
                FileLockCount fileLockCount = this.mRefCountMap.get(lockFile.getAbsolutePath());
                if (fileLockCount != null) {
                    java.nio.channels.FileLock fileLock = fileLockCount.mFileLock;
                    RandomAccessFile randomAccessFile = fileLockCount.fOs;
                    FileChannel fileChannel = fileLockCount.fChannel;
                    try {
                        if (RefCntDec(lockFile.getAbsolutePath()) <= 0) {
                            if (fileLock != null && fileLock.isValid()) {
                                fileLock.release();
                            }
                            if (randomAccessFile != null) {
                                randomAccessFile.close();
                            }
                            if (fileChannel != null) {
                                fileChannel.close();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private class FileLockCount {
            FileChannel fChannel;
            RandomAccessFile fOs;
            java.nio.channels.FileLock mFileLock;
            int mRefCount;

            FileLockCount(java.nio.channels.FileLock fileLock, int mRefCount, RandomAccessFile fOs,
                          FileChannel fChannel) {
                this.mFileLock = fileLock;
                this.mRefCount = mRefCount;
                this.fOs = fOs;
                this.fChannel = fChannel;
            }
        }
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
