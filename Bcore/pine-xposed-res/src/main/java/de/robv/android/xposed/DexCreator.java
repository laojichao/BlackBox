package de.robv.android.xposed;

import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;

import static de.robv.android.xposed.XposedHelpers.inputStreamToByteArray;

/**
 * Helper class which can create a very simple .dex file, containing only a class definition
 * with a super class (no methods, fields, or other members).
 *
 * <p>This is used by the Xposed framework to create runtime superclasses for framework classes
 * (e.g. {@code XResourcesSuperClass}) that allow module code to properly extend Android framework
 * classes which cannot be directly subclassed due to visibility or class loader constraints.
 *
 * <p>The generated dex file defines a single public class that extends the specified superclass,
 * using the {@code xposed.dummy} package namespace.
 */
// Dreamland changed: DexCreator is public
public class DexCreator {
    /** The default dalvik-cache directory where generated dex files are stored. */
    public static File DALVIK_CACHE = new File(Environment.getDataDirectory(), "dalvik-cache");

    /**
     * Returns the default dex file name for a generated super class.
     *
     * @param childClz the simple class name used to derive the file name
     * @return the default dex file in the dalvik-cache directory
     */
    public static File getDefaultFile(String childClz) {
        return new File(DALVIK_CACHE, "dreamland_" + childClz.substring(childClz.lastIndexOf('.') + 1) + ".dex");
    }

    /**
     * Creates (or returns the cached path to) a dex file that defines a generated superclass for
     * {@code clz}, extending {@code realSuperClz} which must itself extend {@code topClz}.
     *
     * @param clz the simple class name to generate a super class for
     * @param realSuperClz the actual superclass the generated class should extend
     * @param topClz the required ancestor class that {@code realSuperClz} must extend
     * @return the path to the generated or cached dex file
     * @throws IOException if the dex file cannot be created
     * @throws ClassCastException if {@code realSuperClz} does not extend {@code topClz}
     */
    public static File ensure(String clz, Class<?> realSuperClz, Class<?> topClz) throws IOException {
        if (!topClz.isAssignableFrom(realSuperClz)) {
            throw new ClassCastException("Cannot initialize " + clz + " because " + realSuperClz + " does not extend " + topClz);
        }

        try {
            return ensure("xposed.dummy." + clz + "SuperClass", realSuperClz);
        } catch (IOException e) {
            throw new IOException("Failed to create a superclass for " + clz, e);
        }
    }

    /**
     * Ensures a dex file exists at the default location that defines a class extending {@code superClz}.
     *
     * @param childClz the simple class name used for the generated super class
     * @param superClz the class to extend
     * @return the path to the generated or cached dex file
     * @throws IOException if the dex file cannot be created
     */
    /** Like {@link #ensure(File, String, String)}, just for the default dex file name. */
    public static File ensure(String childClz, Class<?> superClz) throws IOException {
        return ensure(getDefaultFile(childClz), childClz, superClz.getName());
    }

    /**
     * Makes sure that the given file is a valid dex file containing the specified class relationship.
     * Creates or replaces the file if it does not match.
     *
     * @param file the target dex file path
     * @param childClz the fully qualified class name of the child class
     * @param superClz the fully qualified class name of the super class
     * @return the path to the validated or newly created dex file
     * @throws IOException if the dex file cannot be created or read
     */
    public static File ensure(File file, String childClz, String superClz) throws IOException {
        // First check if a valid file exists.
        try {
            byte[] dex = inputStreamToByteArray(new FileInputStream(file));
            if (matches(dex, childClz, superClz)) {
                return file;
            } else {
                file.delete();
            }
        } catch (IOException e) {
            file.delete();
        }

        // If not, create a new dex file.
        byte[] dex = create(childClz, superClz);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(dex);
        fos.close();
        return file;
    }

    /**
     * Checks whether a dex byte array matches the expected child and super class names.
     * Assumes the dex was created by this class.
     *
     * @param dex the dex file content as a byte array
     * @param childClz the fully qualified child class name
     * @param superClz the fully qualified super class name
     * @return {@code true} if the dex contains the expected class names
     * @throws IOException if the class names cannot be converted to bytes
     */
    public static boolean matches(byte[] dex, String childClz, String superClz) throws IOException {
        boolean childFirst = childClz.compareTo(superClz) < 0;
        byte[] childBytes = stringToBytes("L" + childClz.replace('.', '/') + ";");
        byte[] superBytes = stringToBytes("L" + superClz.replace('.', '/') + ";");

        int pos = 0xa0;
        if (pos + childBytes.length + superBytes.length >= dex.length) {
            return false;
        }

        for (byte b : childFirst ? childBytes : superBytes) {
            if (dex[pos++] != b) {
                return false;
            }
        }

        for (byte b : childFirst ? superBytes: childBytes) {
            if (dex[pos++] != b) {
                return false;
            }
        }

        return true;
    }

    /**
     * Creates a dex byte buffer defining a class named {@code xposed.dummy.<superOf>SuperClass}
     * that extends {@code superclass}.
     *
     * @param superOf the simple class name used to derive the generated class name
     * @param superclass the class to extend
     * @return a {@link ByteBuffer} wrapping the generated dex bytes
     * @throws IOException if the dex cannot be created
     */
    // Dreamland added
    public static ByteBuffer create(String superOf, Class<?> superclass) throws IOException {
        return ByteBuffer.wrap(create("xposed.dummy." + superOf + "SuperClass", superclass.getName()));
    }

    /**
     * Creates the raw byte array for a dex file defining {@code childClz} extending {@code superClz}.
     *
     * @param childClz the fully qualified child class name
     * @param superClz the fully qualified super class name
     * @return the dex file content as a byte array
     * @throws IOException if the class names cannot be converted to bytes
     */
    public static byte[] create(String childClz, String superClz) throws IOException {
        boolean childFirst = childClz.compareTo(superClz) < 0;
        byte[] childBytes = stringToBytes("L" + childClz.replace('.', '/') + ";");
        byte[] superBytes = stringToBytes("L" + superClz.replace('.', '/') + ";");
        int stringsSize = childBytes.length + superBytes.length;
        int padding = -stringsSize & 3;
        stringsSize += padding;

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // header
        out.write("dex\n035\0".getBytes()); // magic
        out.write(new byte[24]);            // placeholder for checksum and signature
        writeInt(out, 0xfc + stringsSize);  // file size
        writeInt(out, 0x70);                // header size
        writeInt(out, 0x12345678);          // endian constant
        writeInt(out, 0);                   // link size
        writeInt(out, 0);                   // link offset
        writeInt(out, 0xa4 + stringsSize);  // map offset
        writeInt(out, 2);                   // strings count
        writeInt(out, 0x70);                // strings offset
        writeInt(out, 2);                   // types count
        writeInt(out, 0x78);                // types offset
        writeInt(out, 0);                   // prototypes count
        writeInt(out, 0);                   // prototypes offset
        writeInt(out, 0);                   // fields count
        writeInt(out, 0);                   // fields offset
        writeInt(out, 0);                   // methods count
        writeInt(out, 0);                   // methods offset
        writeInt(out, 1);                   // classes count
        writeInt(out, 0x80);                // classes offset
        writeInt(out, 0x5c + stringsSize);  // data size
        writeInt(out, 0xa0);                // data offset

        // string map
        writeInt(out, 0xa0);
        writeInt(out, 0xa0 + (childFirst ? childBytes.length : superBytes.length));

        // types
        writeInt(out, 0); // first type = first string
        writeInt(out, 1); // second type = second string

        // class definitions
        writeInt(out, childFirst ? 0 : 1); // class to define = child type
        writeInt(out, 1);                  // access flags = public
        writeInt(out, childFirst ? 1 : 0); // super class = super type
        writeInt(out, 0);                  // no interface
        writeInt(out, -1);                 // no source file
        writeInt(out, 0);                  // no annotations
        writeInt(out, 0);                  // no class data
        writeInt(out, 0);                  // no static values

        // string data
        out.write(childFirst ? childBytes : superBytes);
        out.write(childFirst ? superBytes : childBytes);
        out.write(new byte[padding]);

        // annotations
        writeInt(out, 0); // no items

        // map
        writeInt(out, 7);                                 // items count
        writeMapItem(out, 0, 1, 0);                       // header
        writeMapItem(out, 1, 2, 0x70);                    // strings
        writeMapItem(out, 2, 2, 0x78);                    // types
        writeMapItem(out, 6, 1, 0x80);                    // classes
        writeMapItem(out, 0x2002, 2, 0xa0);               // string data
        writeMapItem(out, 0x1003, 1, 0xa0 + stringsSize); // annotations
        writeMapItem(out, 0x1000, 1, 0xa4 + stringsSize); // map list

        byte[] buf = out.toByteArray();
        updateSignature(buf);
        updateChecksum(buf);
        return buf;
    }

    private static void updateSignature(byte[] dex) {
        // Update SHA-1 signature
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(dex, 32, dex.length - 32);
            md.digest(dex, 12, 20);
        } catch (NoSuchAlgorithmException | DigestException e) {
            throw new RuntimeException(e);
        }
    }

    private static void updateChecksum(byte[] dex) {
        // Update Adler32 checksum
        Adler32 a32 = new Adler32();
        a32.update(dex, 12, dex.length - 12);
        int chksum = (int) a32.getValue();
        dex[8] = (byte) (chksum & 0xff);
        dex[9] = (byte) (chksum >> 8 & 0xff);
        dex[10] = (byte) (chksum >> 16 & 0xff);
        dex[11] = (byte) (chksum >> 24 & 0xff);
    }

    private static void writeUleb128(OutputStream out, int value) throws IOException {
        while (value > 0x7f) {
            out.write((value & 0x7f) | 0x80);
            value >>>= 7;
        }
        out.write(value);
    }

    private static void writeInt(OutputStream out, int value) throws IOException {
        out.write(value);
        out.write(value >> 8);
        out.write(value >> 16);
        out.write(value >> 24);
    }

    private static void writeMapItem(OutputStream out, int type, int count, int offset) throws IOException {
        writeInt(out, type);
        writeInt(out, count);
        writeInt(out, offset);
    }

    private static byte[] stringToBytes(String s) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        writeUleb128(bytes, s.length());
        // This isn't MUTF-8, but should be OK.
        bytes.write(s.getBytes(StandardCharsets.UTF_8));
        bytes.write(0);
        return bytes.toByteArray();
    }

    private DexCreator() {}
}