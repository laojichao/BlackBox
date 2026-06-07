package top.niunaijun.blackbox.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for computing MD5 hash digests from strings, files, and input streams.
 * Produces lowercase hexadecimal MD5 hash strings suitable for integrity verification
 * and cache key generation.
 */
public class Md5Utils {

    private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f' };


    /**
     * Computes the MD5 hash of the given input string using UTF-8 encoding.
     *
     * @param input the string to hash
     * @return the lowercase hexadecimal MD5 hash string, or {@code null} if the input is {@code null}
     *         or an error occurs
     */
    public static String md5(String input) {
        if (input == null)
            return null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] inputByteArray = input.getBytes("utf-8");
            messageDigest.update(inputByteArray);
            byte[] resultByteArray = messageDigest.digest();
            return byteArrayToHex(resultByteArray);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Computes the MD5 hash of the contents of the given file.
     *
     * @param file the file whose contents will be hashed
     * @return the lowercase hexadecimal MD5 hash string, or {@code null} if the file does not
     *         exist, is not a regular file, or an I/O error occurs
     */
    public static String md5(File file) {
        try {
            if (!file.isFile()) {

                return null;
            }

            FileInputStream in = new FileInputStream(file);

            String result = md5(in);

            in.close();

            return result;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Computes the MD5 hash of the data read from the given input stream.
     * The stream is closed after reading.
     *
     * @param in the input stream to read and hash
     * @return the lowercase hexadecimal MD5 hash string, or {@code null} if an error occurs
     */
    public static String md5(InputStream in) {

        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = in.read(buffer)) != -1) {
                messagedigest.update(buffer, 0, read);
            }

            in.close();

            String result = byteArrayToHex(messagedigest.digest());

            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String byteArrayToHex(byte[] byteArray) {

        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }

        return new String(resultCharArray);

    }

}
