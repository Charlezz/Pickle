package com.charlezz.pickle.util.glide;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class RoundedCornersLineOverDrawTransFormation extends BitmapTransformation {
    private static final String ID = "com.nhn.android.band.image.transformation.RoundedCornersLineOverDrawTransFormation";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);
    private final int roundingRadius;
    private final float lineWidth;
    private final int lineColor;

    public RoundedCornersLineOverDrawTransFormation(int roundingRadius, float lineWidth, int lineColor) {
        Preconditions.checkArgument(roundingRadius > 0, "roundingRadius must be greater than 0.");
        Preconditions.checkArgument(lineWidth > 0, "lineWidth must be greater than 0.");
        this.roundingRadius = roundingRadius;
        this.lineWidth = lineWidth;
        this.lineColor = lineColor;
    }

    public RoundedCornersLineOverDrawTransFormation(int roundingRadius, float lineWidth) {
        this(roundingRadius, lineWidth, Color.parseColor("#0F000000"));
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap result = TransformationUtils.roundedCorners(pool, toTransform, roundingRadius);
        Paint borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(lineColor);

        RectF rect = new RectF(0, 0, outWidth, outHeight);
        RectF lineRect = new RectF(lineWidth, lineWidth, outWidth - lineWidth, outHeight - lineWidth);
        Canvas canvas = new Canvas(result);
        Path path = new Path();
        path.addRoundRect(lineRect, roundingRadius, roundingRadius, Path.Direction.CCW);
        canvas.clipPath(path, Region.Op.DIFFERENCE);
        canvas.drawRoundRect(rect, roundingRadius, roundingRadius, borderPaint);

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RoundedCornersLineOverDrawTransFormation) {
            RoundedCornersLineOverDrawTransFormation other = (RoundedCornersLineOverDrawTransFormation) o;
            return lineWidth == other.lineWidth && roundingRadius == other.roundingRadius;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Util.hashCode(ID.hashCode(), Util.hashCode(lineWidth + roundingRadius));
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);

        byte[] radiusData = ByteBuffer.allocate(4).putFloat(lineWidth + roundingRadius).array();
        messageDigest.update(radiusData);
    }
}
