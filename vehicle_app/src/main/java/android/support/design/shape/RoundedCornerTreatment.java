package android.support.design.shape;

import android.support.design.internal.Experimental;

@Experimental("The shapes API is currently experimental and subject to change")
public class RoundedCornerTreatment extends CornerTreatment {
    private final float radius;

    public RoundedCornerTreatment(float radius2) {
        this.radius = radius2;
    }

    public void getCornerPath(float angle, float interpolation, ShapePath shapePath) {
        shapePath.reset(0.0f, this.radius * interpolation);
        shapePath.addArc(0.0f, 0.0f, this.radius * 2.0f * interpolation, this.radius * 2.0f * interpolation, angle + 180.0f, 90.0f);
    }
}
