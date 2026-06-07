package top.niunaijun.blackbox.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Utility class for converting Android {@link Drawable} objects to {@link Bitmap}.
 * Handles both {@link BitmapDrawable} instances directly and other drawable types
 * by rendering them onto a canvas.
 */
public class DrawableUtils {
    /**
     * Converts a {@link Drawable} to a {@link Bitmap} with the specified dimensions.
     * If the drawable is already a {@link BitmapDrawable}, its underlying bitmap is returned
     * directly. Otherwise, the drawable is rendered onto a newly created bitmap.
     *
     * @param drawable the drawable to convert; may be {@code null}
     * @param width the width of the output bitmap in pixels
     * @param height the height of the output bitmap in pixels
     * @return the resulting bitmap, or {@code null} if the drawable is {@code null}
     */
    public static Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        if (drawable == null)
            return null;

        if (drawable instanceof BitmapDrawable)
            return ((BitmapDrawable) drawable).getBitmap();

        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ?
                Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;

        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }
}
