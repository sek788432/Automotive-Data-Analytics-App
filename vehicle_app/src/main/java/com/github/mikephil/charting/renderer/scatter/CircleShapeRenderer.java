package com.github.mikephil.charting.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class CircleShapeRenderer implements IShapeRenderer {
    public void renderShape(Canvas c, IScatterDataSet dataSet, ViewPortHandler viewPortHandler, float posX, float posY, Paint renderPaint) {
        Canvas canvas = c;
        float f = posX;
        float f2 = posY;
        Paint paint = renderPaint;
        float shapeSize = dataSet.getScatterShapeSize();
        float shapeHalf = shapeSize / 2.0f;
        float shapeHoleSizeHalf = Utils.convertDpToPixel(dataSet.getScatterShapeHoleRadius());
        float shapeStrokeSize = (shapeSize - (shapeHoleSizeHalf * 2.0f)) / 2.0f;
        float shapeStrokeSizeHalf = shapeStrokeSize / 2.0f;
        int shapeHoleColor = dataSet.getScatterShapeHoleColor();
        if (((double) shapeSize) > Utils.DOUBLE_EPSILON) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(shapeStrokeSize);
            canvas.drawCircle(f, f2, shapeHoleSizeHalf + shapeStrokeSizeHalf, paint);
            if (shapeHoleColor != 1122867) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(shapeHoleColor);
                canvas.drawCircle(f, f2, shapeHoleSizeHalf, paint);
                return;
            }
            return;
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(f, f2, shapeHalf, paint);
    }
}
