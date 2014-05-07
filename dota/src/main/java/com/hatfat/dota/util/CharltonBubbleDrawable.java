package com.hatfat.dota.util;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.hatfat.dota.DotaFriendApplication;
import com.hatfat.dota.R;

public class CharltonBubbleDrawable extends Drawable {

    @Override
    public void draw(Canvas canvas) {

        int bubbleCornerRadius = (int) DotaFriendApplication.CONTEXT.getResources().getDimension(R.dimen.bubble_corner_radius);
        int bubbleWidth = bubbleCornerRadius * 2;

        Paint paint = new Paint();
        paint.setColor(DotaFriendApplication.CONTEXT.getResources().getColor(R.color.off_white));

        RectF rect = new RectF(bubbleWidth, 0, canvas.getWidth(), canvas.getHeight());
        canvas.drawRoundRect(rect, bubbleCornerRadius, bubbleCornerRadius, paint);

        int talkingHeight = bubbleWidth;
        int talkingVerticalOffset = bubbleCornerRadius * 3;

        Path talkingArrowPath = new Path();

        talkingArrowPath.moveTo(0.0f, talkingVerticalOffset);
        talkingArrowPath.lineTo(bubbleWidth, talkingVerticalOffset + talkingHeight / 2);
        talkingArrowPath.lineTo(bubbleWidth, talkingVerticalOffset - talkingHeight / 2);
        talkingArrowPath.close();

        canvas.drawPath(talkingArrowPath, paint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
