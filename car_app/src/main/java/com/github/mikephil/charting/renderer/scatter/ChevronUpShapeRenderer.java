package com.github.mikephil.charting.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class ChevronUpShapeRenderer implements IShapeRenderer {
    public void renderShape(Canvas c, IScatterDataSet dataSet, ViewPortHandler viewPortHandler, float posX, float posY, Paint renderPaint) {
        float shapeHalf = dataSet.getScatterShapeSize() / 2.0f;
        renderPaint.setStyle(Paint.Style.STROKE);
        renderPaint.setStrokeWidth(Utils.convertDpToPixel(1.0f));
        Canvas canvas = c;
        float f = posX;
        float f2 = posY;
        Paint paint = renderPaint;
        canvas.drawLine(f, posY - (shapeHalf * 2.0f), posX + (shapeHalf * 2.0f), f2, paint);
        canvas.drawLine(f, posY - (shapeHalf * 2.0f), posX - (2.0f * shapeHalf), f2, paint);
    }
}
