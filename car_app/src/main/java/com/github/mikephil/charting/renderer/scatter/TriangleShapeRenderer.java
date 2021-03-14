package com.github.mikephil.charting.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class TriangleShapeRenderer implements IShapeRenderer {
    protected Path mTrianglePathBuffer = new Path();

    public void renderShape(Canvas c, IScatterDataSet dataSet, ViewPortHandler viewPortHandler, float posX, float posY, Paint renderPaint) {
        Canvas canvas = c;
        float f = posX;
        Paint paint = renderPaint;
        float shapeSize = dataSet.getScatterShapeSize();
        float shapeHalf = shapeSize / 2.0f;
        float shapeStrokeSize = (shapeSize - (Utils.convertDpToPixel(dataSet.getScatterShapeHoleRadius()) * 2.0f)) / 2.0f;
        int shapeHoleColor = dataSet.getScatterShapeHoleColor();
        paint.setStyle(Paint.Style.FILL);
        Path tri = this.mTrianglePathBuffer;
        tri.reset();
        tri.moveTo(f, posY - shapeHalf);
        tri.lineTo(f + shapeHalf, posY + shapeHalf);
        tri.lineTo(f - shapeHalf, posY + shapeHalf);
        if (((double) shapeSize) > Utils.DOUBLE_EPSILON) {
            tri.lineTo(f, posY - shapeHalf);
            tri.moveTo((f - shapeHalf) + shapeStrokeSize, (posY + shapeHalf) - shapeStrokeSize);
            tri.lineTo((f + shapeHalf) - shapeStrokeSize, (posY + shapeHalf) - shapeStrokeSize);
            tri.lineTo(f, (posY - shapeHalf) + shapeStrokeSize);
            tri.lineTo((f - shapeHalf) + shapeStrokeSize, (posY + shapeHalf) - shapeStrokeSize);
        }
        tri.close();
        canvas.drawPath(tri, paint);
        tri.reset();
        if (((double) shapeSize) > Utils.DOUBLE_EPSILON && shapeHoleColor != 1122867) {
            paint.setColor(shapeHoleColor);
            tri.moveTo(f, (posY - shapeHalf) + shapeStrokeSize);
            tri.lineTo((f + shapeHalf) - shapeStrokeSize, (posY + shapeHalf) - shapeStrokeSize);
            tri.lineTo((f - shapeHalf) + shapeStrokeSize, (posY + shapeHalf) - shapeStrokeSize);
            tri.close();
            canvas.drawPath(tri, paint);
            tri.reset();
        }
    }
}
