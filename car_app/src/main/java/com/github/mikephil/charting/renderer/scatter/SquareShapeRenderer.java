package com.github.mikephil.charting.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class SquareShapeRenderer implements IShapeRenderer {
    public void renderShape(Canvas c, IScatterDataSet dataSet, ViewPortHandler viewPortHandler, float posX, float posY, Paint renderPaint) {
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
            c.drawRect((posX - shapeHoleSizeHalf) - shapeStrokeSizeHalf, (posY - shapeHoleSizeHalf) - shapeStrokeSizeHalf, posX + shapeHoleSizeHalf + shapeStrokeSizeHalf, posY + shapeHoleSizeHalf + shapeStrokeSizeHalf, renderPaint);
            if (shapeHoleColor != 1122867) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(shapeHoleColor);
                c.drawRect(posX - shapeHoleSizeHalf, posY - shapeHoleSizeHalf, posX + shapeHoleSizeHalf, posY + shapeHoleSizeHalf, renderPaint);
                return;
            }
            return;
        }
        paint.setStyle(Paint.Style.FILL);
        c.drawRect(posX - shapeHalf, posY - shapeHalf, posX + shapeHalf, posY + shapeHalf, renderPaint);
    }
}
